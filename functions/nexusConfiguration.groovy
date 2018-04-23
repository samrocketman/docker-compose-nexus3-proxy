/**
 * Copyright (c) 2018 Sam Gleske - https://github.com/samrocketman/docker-compose-local-nexus3-proxy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
    https://github.com/sonatype/nexus-public/blob/master/components/nexus-core/src/main/java/org/sonatype/nexus/internal/provisioning/BlobStoreApiImpl.groovy
    blobStore methods
    createFileBlobStore createS3BlobStore equals getBlobStoreManager getClass
    getMetaClass getProperty hashCode invokeMethod notify notifyAll
    setBlobStoreManager setMetaClass setProperty toString wait

    https://github.com/sonatype/nexus-public/blob/master/components/nexus-repository/src/main/java/org/sonatype/nexus/repository/internal/blobstore/BlobStoreManagerImpl.java
    blobStoreManager methods
    CGLIB$SET_STATIC_CALLBACKS CGLIB$SET_THREAD_CALLBACKS CGLIB$findMethodProxy
    browse create delete equals exists get getClass getStateGuard hashCode
    notify notifyAll on start stop toString wait
*/

/**
  A custom exception class to limit unnecessary text in the JSON result of the
  Nexus REST API.
 */
class MyException extends Exception {
    String message
    MyException(String message) {
        this.message = message
    }
    String toString() {
        this.message
    }
}

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

blobStoreManager = blobStore.blobStoreManager
repositoryManager = repository.repositoryManager

void checkForEmptyValidation(String message, List<String> bad_values) {
    if(bad_values) {
        throw new MyException("Found invalid ${message}: ${bad_values.join(', ')}")
    }
}

List<String> getKnownDesiredBlobStores(Map json) {
    json['repositories'].collect { provider_key, provider ->
        provider.collect { repo_type_key, repo_type ->
            repo_type.collect { repo_name_key, repo_name ->
                if(!repo_name['blobstore']?.get('name')) {
                    throw new MyException("Blobstore configuration required: ${[provider_key, repo_type_key, repo_name_key].join(' -> ')}")
                }
                repo_name['blobstore']?.get('name')
            }
        }
    }.flatten().sort().unique()
}

void validateConfiguration(def json) {
    List<String> supported_root_keys = ['repositories', 'blobstores']
    List<String> supported_blobstores = ['file']
    List<String> supported_repository_providers = ['bower', 'docker', 'gitlfs', 'maven2', 'npm', 'nuget', 'pypi', 'raw', 'rubygems']
    List<String> supported_repository_types = ['proxied', 'hosted', 'group']
    String valid_name = '^[-a-zA-Z]+$'
    if(!(json in Map)) {
        throw new MyException("Configuration is not valid.  It must be a JSON object.  Instead, found a JSON array.")
    }
    checkForEmptyValidation('root keys', ((json.keySet() as List) - supported_root_keys))
    checkForEmptyValidation('blobstore types', ((json['blobstores']?.keySet() as List) - supported_blobstores))
    if(!(json['blobstores']?.get('file') in List) || false in json['blobstores']?.get('file').collect { it in String }) {
        throw new MyException('blobstore file type must contain a list of Strings.')
    }
    checkForEmptyValidation('repository providers', ((json['repositories']?.keySet() as List) - supported_repository_providers))
    checkForEmptyValidation('repository types', (json['repositories'].collect { k, v -> v.keySet() as List }.flatten().sort().unique() - supported_repository_types))
    checkForEmptyValidation('blobstores defined in repositories.  The following must be listed in the blobstores',
            (getKnownDesiredBlobStores(json) - json['blobstores']['file']))
}

try {
    config = (new JsonSlurper()).parseText(args)
}
catch(Exception e) {
    throw new MyException("Configuration is not valid.  It must be a valid JSON object.")
}
validateConfiguration(config)
//we've come this far so it is probably good?

//create blob stores first
config['blobstores']['file'].each { String store ->
    if(!blobStoreManager.get(store)) {
        blobStore.createFileBlobStore(store, store)
    }
}

//create non-group repositories second

//create repository groups last

//.metaClass.methods*.name.sort().unique().join(' ')
'success'
