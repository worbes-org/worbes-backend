#!/bin/bash

set -e
echo "--------------- start deploy worbes batch -----------------"
cd /home/ubuntu/worbes-server
docker stop worbes-batch-dev || true
docker rm worbes-batch-dev || true
docker compose -f compose.dev.yml pull worbes-batch
docker compose -f compose.dev.yml up -d worbes-batch
docker image prune -af
rm -rf compose.dev.yml
rm -rf .env.dev
echo "--------------- end deploy -----------------"
exit 0
