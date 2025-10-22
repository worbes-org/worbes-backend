#!/bin/bash

echo "--------------- worbes api 서버 배포 시작 -----------------"
docker stop worbes-api-dev || true
docker rm worbes-api-dev || true
docker compose -f compose.dev.yml pull worbes-api
docker compose -f compose.dev.yml up -d worbes-api
docker image prune -af
rm -rf compose.dev.yml
rm -rf .env.dev
echo "--------------- 서버 배포 끝 -----------------"
