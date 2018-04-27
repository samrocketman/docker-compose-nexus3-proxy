#!/bin/bash
#Created by Sam Gleske
until curl -fsSIu admin:admin123 -m 15 -o /dev/null \
      http://localhost:8081/service/metrics/ping; do
  sleep 5
done
