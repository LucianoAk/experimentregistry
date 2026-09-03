#!/usr/bin/env bash

set -e

RED='\033[0;31m'
BOLD='\033[1m'
RESET='\033[0m'

MODE="${1:-}"

if [[ -z "$MODE" ]]; then
  printf '%bError:%b you must specify a mode.\n' "$RED" "$RESET" >&2
  printf 'Usage: ./run.sh <mode>\n' >&2
  exit 1
fi

# Container Configurations

define_container_runtime() {
  if command -v docker >/dev/null 2>&1; then
    printf 'docker\n'
    return
  fi

  if command -v podman >/dev/null 2>&1; then
    printf 'podman\n'
    return
  fi

  printf '%bError:%b Neither Docker nor Podman was found.\n' "$RED" "$RESET" >&2
  return 1
}

configure_container_runtime() {
  local runtime
  runtime=$(define_container_runtime) || return 1

  if [[ "$runtime" == "docker" ]]; then
    if [[ ! -S /var/run/docker.sock ]]; then
      printf '%bError:%b Could not initialize containers.\n' "$RED" "$RESET" >&2
      printf 'Docker is installed, but /var/run/docker.sock is not available.\n' >&2
      return 1
    fi

    export DOCKER_HOST="unix:///var/run/docker.sock"
  fi

  if [[ "$runtime" == "podman" ]]; then
    if ! systemctl --user is-active --quiet podman.socket 2>/dev/null; then
      printf '%bError:%b Could not initialize containers.\n' "$RED" "$RESET" >&2
      printf 'Podman is installed, but %s/podman/podman.sock is not available.\n' "$XDG_RUNTIME_DIR" >&2
      printf 'Start it with: %bsystemctl --user start podman.socket%b\n' "$BOLD" "$RESET" >&2
      printf 'To start it automatically on login, run: %bsystemctl --user enable podman.socket%b\n' "$BOLD" "$RESET" >&2
      return 1
    fi

    export DOCKER_HOST="unix://${XDG_RUNTIME_DIR}/podman/podman.sock"
  fi
}

# Full Mode

start_containers() {
  local runtime
  runtime=$(define_container_runtime) || return 1
  "$runtime" compose up -d --build
}

# Test Mode

run_tests() {
  local use_containers=true

  if [[ "${1:-}" == "--no-containers" || "${1:-}" == "--noc" ]]; then
    use_containers=false
    shift
  fi

  if $use_containers; then
    configure_container_runtime || {
      printf 'To skip container setup, run %b./run.sh test --no-containers%b or %b./run.sh test --noc%b. Tests that require containers may fail.\n' \
        "$BOLD" "$RESET" "$BOLD" "$RESET" >&2
      return 1
    }
  fi

  if [[ $# -eq 0 ]]; then
    ./mvnw test \
      -Dspring.profiles.active="$MODE"
    return
  fi

  local tests=("$@")

  local test_pattern
  test_pattern=$(
    IFS=,
    printf '%s\n' "${tests[*]}"
  )

  ./mvnw test \
    -Dspring.profiles.active="$MODE" \
    -Dtest="$test_pattern"
}

# Dev Mode

run_spring_boot() {
  ./mvnw spring-boot:run -Dspring-boot.run.profiles="$MODE"
}

# Main

case "$MODE" in
--help | -h)
  printf 'Usage: ./run.sh <mode>\n'
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
  printf '%bError:%b Unknown mode: %s\n' "$RED" "$RESET" "$MODE" >&2
  printf "Run './run.sh --help' for usage.\n" >&2
  exit 1
  ;;
esac
