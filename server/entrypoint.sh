#!/usr/bin/env bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

pushd ${SCRIPT_DIR} >/dev/null

mvn -T 1C clean package -Dmaven.test.skip=true

mvn spring-boot:run
