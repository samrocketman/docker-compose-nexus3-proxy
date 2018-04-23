#!/bin/bash
#Created by Sam Gleske
#License: ASLv2 https://github.com/samrocketman/docker-compose-local-nexus3-proxy
#Sun Apr 22 15:53:51 PDT 2018
#Ubuntu 16.04.4 LTS
#Linux 4.13.0-38-generic x86_64
#GNU bash, version 4.3.48(1)-release (x86_64-pc-linux-gnu)
#curl 7.47.0 (x86_64-pc-linux-gnu) libcurl/7.47.0 GnuTLS/3.5.5 zlib/1.2.8 libidn/1.32 librtmp/2.3

# DESCRIPTION:
#     Upload script to Nexus 3 scripting endpoint.

NEXUS_ENDPOINT="${NEXUS_ENDPOINT:-http://localhost:8081}"
# remove trailing slashes
NEXUS_ENDPOINT="${NEXUS_ENDPOINT%/}"
NEXUS_USER="${NEXUS_USER:-admin}"
NEXUS_PASSWORD="${NEXUS_PASSWORD:-admin123}"

if [ $# -eq 0 ]; then
  echo 'ERROR: first argument must be repository JSON configuration.' >&2
  exit 1
fi

FILE="$1"
shift

curl "$@" -u "${NEXUS_USER}:${NEXUS_PASSWORD}" -X POST -d @"${FILE}" \
  --header 'Content-Type: text/plain' \
  "${NEXUS_ENDPOINT}"/service/rest/v1/script/nexusConfiguration/run
echo
