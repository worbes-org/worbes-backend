#!/bin/bash

set -e
echo "--------------- start deploy worbes batch -----------------"
cd /home/ubuntu/worbes-server
docker stop worbes-api-batch || true
docker rm worbes-api-batch || true
docker compose -f compose.dev.yml pull worbes-batch
docker compose -f compose.dev.yml up -d worbes-batch
docker image prune -af
rm -rf compose.dev.yml
rm -rf .env.dev
echo "--------------- 서버 배포 끝 -----------------"
exit 0
