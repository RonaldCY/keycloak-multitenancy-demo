
## Usage

- [Spring Boot 2.4.0](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [KeyCloak 18.0.0](https://www.keycloak.org/documentation)

## Setup Environment
- Java 11
- Docker

## Build
### Implement KeyCloak server on docker
1. Run docker container
```
docker run -d -p 8080:8080 --name kc -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:18.0.0 start-dev
```
2. Go to http://localhost:8080/admin/ and log in to the Keycloak Admin Console.
3. Mouse over the **Master**, then click `Add relm`, select files `.\keycloak-realms\{branch1 | branch2 | carrier}-export.json`, then click `Create` for each
4. Click the `Manage/Users`, then add user
    - `user` with ROLE **USER** and, 
   
      `admin` with ROLE **ADMIN** on branch1 and branch2
    - `maintainer` with ROLE **MAINTAINER** on carrier

### Run Spring Boot with API server
1. `mvn clean install`
2. `mvn spring-boot:run`