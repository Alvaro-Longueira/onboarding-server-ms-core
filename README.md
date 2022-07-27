# Claim Microservice

[![Maintainability](https://api.codeclimate.com/v1/badges/04591832845e3f90b647/maintainability)](https://codeclimate.com/repos/6047562cf74d620db200c8c9/maintainability) [![Test Coverage](https://api.codeclimate.com/v1/badges/04591832845e3f90b647/test_coverage)](https://codeclimate.com/repos/6047562cf74d620db200c8c9/test_coverage)
## :computer: Build project
Setup environment
```shell
export NEXUS_USERNAME=wefox-server
export NEXUS_PASSWORD=<<password>>
```

### Project specifics
- Maven wrapper is used by Dockerfile

### Maven build
```shell
./mvnw --settings settings.xml clean package
```
Produces a spring boot application located at `main/target/main.jar`

### Docker Image build

Compile and then build the image
```shell
# compile
./mvnw --settings settings.xml clean package
# build image
docker build . --build-arg JAR_FILE=main/target/main.jar -t onboarding-server-ms-core
```

Compile in a multi-stage docker build
```shell
docker build . -f Dockerfile.compile --build-arg NEXUS_USERNAME=${NEXUS_USERNAME} --build-arg NEXUS_PASSWORD=${NEXUS_PASSWORD} -t onboarding-server-ms-core
```

## :running_man: Run locally

### Environment
List of environment variables that may be available during runtime

| Variable | default | Description |
| --- | --- | --- |
| APP_SERVER_PORT | 8080 | server port |
| APP_POSTGRES_HOST | localhost | - |
| APP_POSTGRES_PORT | 5432 | - |
| APP_POSTGRES_DB | claim | - |
| APP_POSTGRES_SCHEMA | public | - |
| APP_POSTGRES_USER | user | - |
| APP_POSTGRES_PASSWORD | password | - |
| APP_KAFKA_BROKERS | localhost:9092 | - |
| APP_KAFKA_REPLICATION_FACTOR | 1 | should be increased on production |
| APP_KAFKA_SCHEMA_REGISTRY_URL | localhost:8081 | - |
| APP_KAFKA_SECURITY_PROTOCOL | - | For secure communication use: SSL or SASL_SSL |
| APP_KAFKA_USER | alice | set when APP_KAFKA_SECURITY_PROTOCOL = SASL_SSL |
| APP_KAFKA_PASSWORD | alice | set when APP_KAFKA_SECURITY_PROTOCOL = SASL_SSL |
| APP_KAFKA_JAAS_ENABLED | false | set when APP_KAFKA_SECURITY_PROTOCOL = SASL_SSL |
| APP_KAFKA_SASL | SCRAM-SHA-512 | default value should be fine when APP_KAFKA_SECURITY_PROTOCOL = SASL_SSL |
| APP_KAFKA_TRUSTSTORE | - | For secure communication use: /application/kafka.client.truststore.jks |
| APP_TOPIC_CLAIM_CHANGED_EVENT | topic-claim-changed-event | Claim Changes topic => "data.claims.claim" |
| APP_TOPIC_CLAIM_STATUS_CHANGED_EVENT | topic-claim-status-changed-event | Claim Changes on the status topic => "event.claims.status-changed" |
| APP_ACTIVE_PROFILE | - | Possible values: "" or "logstash" |
| APP_LOGSTASH_HOST | localhost:5000 | Not used unless "logstash" is spring profile is active |
| APP_OAUTH_SERVER | https://id-dev.wefox.com/auth/realms/wefox | - |
| APP_OAUTH_SERVER_CERTS | https://id-dev.wefox.com/auth/realms/wefox/protocol/openid-connect/certs | - |
| APP_OAUTH_SCOPE | onboarding-server-ms-core | - |
| APP_CONF_MARKET_ID | de | Id of the market (country) for apps with multiple instances |
| APP_CONF_SYMASS_SKIP | it | Comma-separated list of markets (countries) to skip Symass validation |
| APP_CONF_NO_SEND_EVENTS | it | Comma-separated list of markets (countries) that no send kafka events |
| APP_MS_ACCOUNT_URL | https://symassdev.one-wefoxgroup.cloud/ | symass dev endpoint (partners) |
| APP_MS_ACCOUNT_SECURITY | CLIENT_CREDENTIALS | CLIENT_CREDENTIALS or NONE |
| APP_MS_ACCOUNT_TOKEN | https://one-dev-symassapp.auth.eu-central-1.amazoncognito.com/oauth2/token | - |
| APP_MS_ACCOUNT_CLIENT_ID | 6cm7opq7dm8snc7mr5dq4k407h | client-id used in the client credentials flow |
| APP_MS_ACCOUNT_CLIENT_SECRET | 3gahgs8ric8bmaevmphtvb592vbcuci20d9kljmffjamtm3cdqu | secret |
| APP_MS_CONTRACT_URL | https://symassdev.one-wefoxgroup.cloud/ | symass dev endpoint (contracts) |
| APP_MS_CONTRACT_SECURITY | CLIENT_CREDENTIALS | CLIENT_CREDENTIALS or NONE |
| APP_MS_CONTRACT_TOKEN | https://one-dev-symassapp.auth.eu-central-1.amazoncognito.com/oauth2/token | - |
| APP_MS_CONTRACT_CLIENT_ID | 6cm7opq7dm8snc7mr5dq4k407h | client-id used in the client credentials flow |
| APP_MS_CONTRACT_CLIENT_SECRET | 3gahgs8ric8bmaevmphtvb592vbcuci20d9kljmffjamtm3cdqu | secret |

List of environment variables needed to run the post-deployment module -> e2e 

| Variable | default | Description |
| --- | --- | --- |
| QA_E2E_URL | http://localhost:8080 | Target url for the e2e tests. |
| QA_API_DOC_PATH | http://localhost:8080/api-docs | Open API url in the target ms. |
| QA_API_SPEC_VALIDATION_METHOD | none | Possible values: none, git-local-file, stoplight, github |
| QA_API_SPEC_VALIDATION_FILE | api-docs.yaml | file name to compare |
| QA_API_SPEC_VALIDATION_LOCATION | src/test/resources/ | file name to compare |
| QA_OAUTH_SERVER | https://id-dev.wefox.com/auth/realms/wefox/protocol/openid-connect/token | - |
| QA_OAUTH_CLIENT_ID | wefox-test-client | client-id used in the client credentials flow |
| QA_OAUTH_CLIENT_SECRET | 490fbdcc-954d-4988-8331-a8a5dc38eefa | secret |
| QA_KAFKA_TRUSTSTORE | - | Truststore to run the post-deployment module |

### Docker compose
1. `./mvnw clean package`
1. `docker-compose -f docker-compose.yml build`
1. `docker-compose -f docker-compose.yml up -d`
1. Navigate to OpenApi definition http://localhost:8101/v3/api-docs/
1. OpenAPI YAML file http://localhost:8101/v3/api-docs.yaml
1. Swagger specification http://localhost:8101/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
1. Stop local environment `docker-compose down`
1. Delete all containers `docker rm -f $(docker ps -a -q)`
1. Delete all volums (Optional) `docker volume rm $(docker volume ls -q)`

Test

```shell

curl --request GET -vsL \
     --url 'http://localhost:8101/health'
```

### Maven
1. `docker-compose -f docker-compose.yml up -d postgres` -> Start the database locally
1. `docker-compose -f docker-compose.yml up -d zookeeper kafka schema-registry` -> Start kafka locally
1. `./mvnw clean install`
1. `./mvnw -pl main spring-boot:run`

Test

```shell

curl --request GET -vsL \
     --url 'http://localhost:8080/health'
```

## Init Database
```shell
docker cp ./main/src/test/resources/sql/initial_claim_type_settings.sql onboarding-server-ms-core-db:/initial_claim_type_settings.sql
docker exec -u postgres onboarding-server-ms-core-db psql claim user -f /initial_claim_type_settings.sql
```

## Kafka Commands
```shell
# kafka-topics.sh
docker exec onboarding-server-ms-core-kafka kafka-topics \
--bootstrap-server kafka:29092 \
--list

docker exec onboarding-server-ms-core-kafka kafka-topics \
--bootstrap-server kafka:29092 \
--topic topic-claim-changed-event \
--describe 

# kafka-console-consumer.sh
docker exec onboarding-server-ms-core-kafka kafka-console-consumer \
--bootstrap-server kafka:29092 \
--topic topic-claim-changed-event \
--value-deserializer org.apache.kafka.common.serialization.StringDeserializer \
--from-beginning

```

## :white_check_mark: Testing options
### Launch Unit & Contract Tests
```shell
./mvnw clean test
```

### Launch Integration Tests
```shell
./mvnw clean verify
```

### E2E Test
By default, local environment will be tested. to change this behaviour you should set the environment
variables (check [here](post-deployment/README.md) for more information).
```shell
./mvnw clean install && ./mvnw -P post-deployment -pl post-deployment clean verify
```

### Load  Tests
```shell
./mvnw -P post-deployment -pl post-deployment clean package gatling:test
```

### Static code analysis
```shell
# create sonarqube container
docker run -d --name sonarqube -p 9000:9000 sonarqube:8.6.0-community
# run container 
docker start sonarqube

# run sonar analysis
./mvnw clean verify sonar:sonar \
-Dsonar.host.url="http://localhost:9000" \
-Dsonar.login="admin" \
-Dsonar.password="admin"
```

## :open_book: Published Endpoints
- SwaggerUI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/health
- Info: http://localhost:8080/info
### K8s
In kubernetes, this two endpoints are exposed, ready to setup the liveness & readiness proves.
- http://localhost:8080/health/liveness
- http://localhost:8080/health/readiness

Thiw is a sample K8s configuration:
```yaml

        startupProbe:
          httpGet:
            path: /health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 15
          failureThreshold: 30
        livenessProbe:
          httpGet:
            path: /health/liveness
            port: 8080
          periodSeconds: 5
          failureThreshold: 1
        readinessProbe:
          httpGet:
            path: /health/readiness
            port: 8080
          periodSeconds: 5
          failureThreshold: 1
```

## :jigsaw: Naming and modules

### Modules
The project is divided in the following modules:

![modules](doc/modules.png "Modules")

### Domain Class Diagram
These are the main entities in the ms-domain:

![modules](doc/domain-diagram/domain_class_diagram.png "Modules")

## :memo: Useful links

### Database
* Optimistic locking [link](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#r2dbc.optimistic-locking)

### Build Docker Image
* Spring Boot Maven Plugin [link](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/)
* Spring Boot Build Docker Images: [link](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-container-images-docker) 

### Documentation
* PlantUML: Official Documentation [link](https://plantuml.com/)

### Cli
* AWS CLI commands: [cli](doc/AWS-CLI.md)
* Kafka CLI commands: [cli](doc/KAFKA-CLI.md)
* Kubernetes CLI commands: [cli](doc/K8S-CLI.md)
* Local ELK setup: [cli](doc/ELK.md)
* OIDC: [cli](doc/OIDC.md)
