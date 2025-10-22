#!/bin/bash

set -e
echo "--------------- start deploy worbes-api -----------------"
cd /home/ubuntu/worbes-server
docker stop worbes-api-dev || true
docker rm worbes-api-dev || true
docker compose -f compose.dev.yml pull worbes-api
docker compose -f compose.dev.yml up -d worbes-api
docker image prune -af
rm -rf compose.dev.yml
rm -rf .env.dev
echo "--------------- deploy end -----------------"
exit 0
