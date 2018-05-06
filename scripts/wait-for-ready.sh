#!/bin/bash
#Created by Sam Gleske
nexus_url="${1:-http://localhost:8081}"
until curl -fsSIu admin:admin123 -m 15 -o /dev/null \
      "${nexus_url%/}"/service/metrics/ping; do
  sleep 5
done
