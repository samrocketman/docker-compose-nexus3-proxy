#!/bin/bash
#Created by Sam Gleske
#Ubuntu 16.04.4 LTS
#Linux 4.13.0-39-generic x86_64
#GNU bash, version 4.3.48(1)-release (x86_64-pc-linux-gnu)
#sed (GNU sed) 4.2.2

for x in laptop-config/*; do
  \sed -i.bak -re "s/localhost/${1}/g" "${x}"
  \rm "${x}.bak"
done
