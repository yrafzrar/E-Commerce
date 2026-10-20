#!/bin/sh
set -eu
cd "$(dirname "$0")/GamesRun"
exec sh mvnw clean spring-boot:run "-Dspring-boot.run.profiles=mock"
