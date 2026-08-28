#!/usr/bin/env bash

if [ -z "$1" ]; then
  echo "Error: you must specify a Spring profile."
  echo "Usage: ./test.sh <profile> [test]"
  exit 1
fi

PROFILE="$1"
TEST="$2"

if [ -z "$TEST" ]; then
  ./mvnw test -Dspring.profiles.active="$PROFILE"
else
  if [[ "$TEST" == *.* ]]; then
    TEST="${TEST/./\$}"
  fi

  ./mvnw test \
    -Dspring.profiles.active="$PROFILE" \
    -Dtest="$TEST"
fi
