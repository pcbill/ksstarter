# ksstarter
The project template of Kotlin Spring Boot

- Kotlin 
- Spring Boot

# Dependencies
- Spring Boot DevTools
- Spring Web Starter
- Spring Security
- Spring Data JPA
- H2 Database
- PostgreSQL Driver
- Liquibase Migration
- Spring Boot Actuator

# Command

### Local (develop) environment

```
$ gradlew -Pwithout_security bootRun
```

db viewer /h2-console

### Product environment

```
$ gradlew -Pprod bootRun
```
