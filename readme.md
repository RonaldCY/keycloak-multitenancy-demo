
## Usage

- [Spring Boot 2.7.x](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [KeyCloak 20.x.x](https://www.keycloak.org/documentation)

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
3. Mouse over the **Master**, then click `Create Realm`, drag files `.\keycloak-realms\{branch1 | branch2 | carrier}-keycloak.json` to the **Resource file** column, then click `Create` for each
4. Click the `Manage/Users`, then add user
    - `user1` with ROLE **USER** and, 
   
      `administrator` with ROLE **ADMIN** on branch1 and branch2
    - `maintainer` with ROLE **MAINTAINER** on carrier

### Run Spring Boot with API server
1. `mvn clean install`
2. `mvn spring-boot:run`