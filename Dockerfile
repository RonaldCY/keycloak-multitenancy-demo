# syntax = docker/dockerfile:experimental

########Maven build stage########
FROM maven:3.8.6-openjdk-18 as maven_build

WORKDIR /app

# copy spring boot application source code
COPY ./src src
COPY ./pom.xml pom.xml

# build the app and download dependencies only when these are new
RUN --mount=type=cache,target=/root/.m2 mvn -f ./pom.xml clean package -Dmaven.test.skip

# split the built app into multiple layers to improve layer rebuild
RUN mkdir -p target/docker-packaging && cd target/docker-packaging && jar -xf ../*.jar

########JRE run stage########
FROM openjdk:18.0.2.1-jdk
WORKDIR /app

# copy built app layer by layer
ARG DOCKER_PACKAGING_DIR=/app/target/docker-packaging
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/lib /app/lib
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/BOOT-INF/classes /app/classes
COPY --from=maven_build ${DOCKER_PACKAGING_DIR}/META-INF /app/META-INF

# copy keycloak realm json
COPY ./original_realm_json /app/realm_json

# run the app
CMD java -cp .:classes:lib/* \
    -Djava.security.egd=file:/dev/./urandom \
    com.czetsuyatech.SpringKeycloakMultiTenantApplication
