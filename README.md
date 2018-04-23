# Local Nexus 3 Proxy

A local Nexus 3 proxy for my laptop whose purpose is to cache dependencies for
docker containers and locally provisioned servers.  I do a lot of provisioning
of infrastructure locally on my laptop.  This Nexus 3 instance is to help
alleviate the need for me to download a lot of duplicate dependencies from the
internet across the local servers I provision.

Different ways I provision locally include:

- Docker
- Docker Compose
- Vagrant

# Provisioning Nexus

Start and automatically configure Nexus.

    docker-compose up -d

The [`docker-compose.yml`](docker-compose.yml) can use docker health checking to
delay configuring Nexus.  Repository settings can be found in
[`./settings/repositories.json`](./settings/repositories.json).

# Configure Nexus

Delete default repositories and blob stores.

    ./scripts/upload_function.py --delete -rf ./functions/deleteAllConfigurations.groovy

Configure new repositories and blob stores.

    ./scripts/upload_function.py --delete -rf ./functions/nexusConfiguration.groovy -d ./settings/repositories.json

# License

[ASL v2](LICENSE)

```
Copyright (c) 2018 Sam Gleske - https://github.com/samrocketman/docker-compose-local-nexus3-proxy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
