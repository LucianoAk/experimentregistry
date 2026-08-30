#!/usr/bin/env bash

set -e

MODE="${1:-}"

if [[ -z "$MODE" ]]; then
  echo "Error: you must specify a mode."
  echo "Usage: ./run.sh <mode>"
  exit 1
fi

start_containers() {
  if command -v docker >/dev/null 2>&1; then
    docker compose up -d --build
    return
  fi

  if command -v podman >/dev/null 2>&1; then
    podman compose up -d --build
    return
  fi

  echo "Error: Docker or Podman is required."
  exit 1
}

run_tests() {
  if [[ $# -eq 0 ]]; then
    ./mvnw test \
      -Dspring.profiles.active="$MODE"
    return
  fi

  local tests=()

  for test in "$@"; do
    if [[ "$test" == *.* ]]; then
      test="${test/./#}"
    fi

    tests+=("$test")
  done

  local test_pattern
  test_pattern=$(
    IFS=,
    echo "${tests[*]}"
  )

  ./mvnw test \
    -Dspring.profiles.active="$MODE" \
    -Dtest="$test_pattern"
}

run_spring_boot() {
  ./mvnw spring-boot:run -Dspring-boot.run.profiles="$MODE"
}

case "$MODE" in
--help | -h)
  echo "Usage: ./run.sh <mode>"
  ;;

full)
  start_containers
  ;;

dev)
  run_spring_boot
  ;;

test)
  run_tests "${@:2}"
  ;;

*)
  echo "Unknown mode: $MODE"
  echo "Run './run.sh --help' for usage."
  exit 1
  ;;
esac
