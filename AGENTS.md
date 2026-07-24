# AGENTS

## Scope

Instructions for AI coding agents working in this repository.

## Stack

- Java 25
- Spring Boot
- RabbitMQ
- Gradle Wrapper

## Fast Start

- Start app: `./gradlew.bat bootRun`
- Run tests + coverage: `./gradlew.bat test jacocoTestReport`

## Local Prerequisites

- RabbitMQ broker required.
- Example local Docker command is documented in README.

## Conventions

- Use environment variables for broker host/port when needed.
- Preserve message flow patterns already in `src/main` before introducing new abstractions.

## References

- [README.md](README.md)
- [build.gradle](build.gradle)
- [src](src)
