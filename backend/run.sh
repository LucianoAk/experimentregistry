#!/usr/bin/env bash

if [ -z "$1" ]; then
  echo "Error: you must specify a Spring profile."
  echo "Usage: ./run.sh <profile>"
  exit 1
fi

PROFILE="$1"

./mvnw spring-boot:run -Dspring-boot.run.profiles="$PROFILE"
