#!/usr/bin/env bash
# Installs the template locally, generates a project from it with the given
# template options and checks that the project builds and runs:
#
#   scripts/test-template.sh            # default options
#   scripts/test-template.sh +test +sass
#
# The project is generated under target/generated/. Needs lein, JDK 21+,
# Node.js and curl; +test also needs Chrome for Karma.
set -euo pipefail

cd "$(dirname "$0")/.."

name=myapp
version=$(head -n 1 project.clj | cut -d '"' -f 2)
dir="$PWD/target/generated/${name}${*:+-}$(echo "$*" | tr -d '+ ')"
# Port for running the uberjar; the shadow-cljs dev server uses 3000 (shadow-cljs.edn).
port=3100

has_opt() {
  local opt
  for opt in "${@:2}"; do
    [[ $opt == "$1" ]] && return 0
  done
  return 1
}

# Waits until the server answers, then checks that each path returns 200.
check_urls() {
  local base=$1 pid=$2 path status
  shift 2
  local ready=
  for _ in $(seq 120); do
    # 7: connection refused, i.e. not listening yet
    if curl -s --max-time 10 -o /dev/null "$base/"; [[ $? != 7 ]]; then
      ready=1
      break
    fi
    if ! kill -0 "$pid" 2>/dev/null; then
      echo "Server exited before it started listening" >&2
      return 1
    fi
    sleep 1
  done
  if [[ -z $ready ]]; then
    echo "Server did not start listening on $base within 120 s" >&2
    return 1
  fi
  for path in "$@"; do
    status=$(curl -s --max-time 30 -o /dev/null -w '%{http_code}' "$base$path" || true)
    echo "GET $base$path -> $status"
    if [[ $status != 200 ]]; then
      return 1
    fi
  done
}

stop() {
  # Kill the whole process group: npx/lein start child processes.
  kill -- "-$1" 2>/dev/null || true
  wait "$1" 2>/dev/null || true
}

echo "::group::Install template $version"
lein install
echo "::endgroup::"

echo "::group::Generate project: lein new reagent $name $*"
rm -rf "$dir"
mkdir -p "$(dirname "$dir")"
lein new reagent "$name" --to-dir "$dir" --template-version "$version" -- "$@"
if [[ ! -f "$dir/project.clj" ]]; then
  echo "lein new did not generate a project" >&2
  exit 1
fi
cd "$dir"
echo "::endgroup::"

echo "::group::npm install"
npm install --no-audit --no-fund
echo "::endgroup::"

if has_opt +less "$@"; then
  echo "::group::Compile less"
  lein less4clj once
  echo "::endgroup::"
fi

if has_opt +sass "$@"; then
  echo "::group::Compile sass"
  lein sass4clj once
  echo "::endgroup::"
fi

echo "::group::shadow-cljs release (warnings as errors)"
npx shadow-cljs release app --config-merge '{:compiler-options {:warnings-as-errors true}}'
echo "::endgroup::"

if has_opt +test "$@"; then
  echo "::group::npm test"
  npm test
  echo "::endgroup::"
fi

echo "::group::Uberjar"
# shellcheck disable=SC1010 # "do" is the lein task
lein do clean, uberjar
echo "::endgroup::"

echo "::group::Run uberjar"
set -m # own process group, so stop can kill the children too
PORT=$port java -jar "target/$name.jar" &
pid=$!
set +m
trap 'stop $pid' EXIT
check_urls "http://localhost:$port" "$pid" / /about /items/1 /js/app.js /css/site.min.css
stop "$pid"
trap - EXIT
echo "::endgroup::"

echo "::group::shadow-cljs dev build and server"
npx shadow-cljs compile app
set -m
npx shadow-cljs server &
pid=$!
set +m
trap 'stop $pid' EXIT
check_urls http://localhost:3000 "$pid" / /about /items/1 /js/app.js /css/site.css
stop "$pid"
trap - EXIT
echo "::endgroup::"

echo "OK: lein new reagent $name $*"
