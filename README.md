# RabbitMQ Pub/Sub with Spring Boot

This repository is a focused Spring Boot sample for the RabbitMQ fanout publish/subscribe pattern. A single HTTP endpoint accepts an employee payload, creates an `EmployeeCreatedEvent`, and broadcasts that event to two independent subscribers: one notification consumer and one audit consumer.

The application is intentionally lightweight. It does not persist employees to a database. The REST layer validates the request, publishes the event, and returns the submitted employee payload with `201 Created`.

## What This Sample Covers

- Fanout exchange setup with two durable queues
- JSON message publishing with Spring AMQP
- Request validation with Jakarta Bean Validation
- Decoupled consumers that react to the same event independently
- Focused tests for the controller, publisher, consumers, and application context

## Message Flow

```text
POST /api/employees
        |
        v
 EmployeeController
        |
        v
 EmployeeEventPublisher
        |
        v
 employee.events.fanout
      /               \
     v                 v
employee.notification.queue   employee.audit.queue
     |                 |
     v                 v
NotificationConsumer   AuditConsumer
```

## RabbitMQ Topology

| Resource | Type | Purpose |
|----------|------|---------|
| `employee.events.fanout` | Fanout exchange | Broadcasts each event to all bound queues |
| `employee.notification.queue` | Durable queue | Receives events for notification handling |
| `employee.audit.queue` | Durable queue | Receives events for audit handling |

Both queues are declared as durable. The publisher sends to the fanout exchange with an empty routing key because fanout exchanges ignore routing keys.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 25 |
| Framework | Spring Boot 4.0.5 |
| Messaging | Spring AMQP with RabbitMQ |
| Validation | Jakarta Validation |
| API Docs | SpringDoc OpenAPI UI |
| Build | Gradle Wrapper |
| Testing | JUnit 5, Mockito, Spring MVC Test |
| Coverage | JaCoCo |

## Prerequisites

- Java 25
- RabbitMQ 3.x or newer
- Docker optional, for local broker startup

## Quick Start

### 1. Start RabbitMQ

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

RabbitMQ management UI: `http://localhost:15672`

Default credentials:

- Username: `guest`
- Password: `guest`

### 2. Run the application

Windows:

```powershell
./gradlew.bat bootRun
```

macOS or Linux:

```bash
./gradlew bootRun
```

The service starts on `http://localhost:8084`.

### 3. Open Swagger UI

```text
http://localhost:8084/swagger-ui.html
```

## Configuration

RabbitMQ connection settings are defined in `src/main/resources/application.yml` and can be overridden with environment variables.

| Variable | Default | Description |
|----------|---------|-------------|
| `RABBITMQ_HOST` | `localhost` | RabbitMQ broker host |
| `RABBITMQ_PORT` | `5672` | AMQP port |
| `RABBITMQ_USERNAME` | `guest` | Broker username |
| `RABBITMQ_PASSWORD` | `guest` | Broker password |

Example override on Windows:

```powershell
$env:RABBITMQ_HOST = "my-broker"
$env:RABBITMQ_PORT = "5673"
./gradlew.bat bootRun
```

### Listener Retry Settings

The configured listener retry policy is:

| Setting | Value |
|---------|-------|
| Enabled | `true` |
| Max attempts | `3` |
| Initial interval | `1000 ms` |
| Multiplier | `2.0` |
| Max interval | `10000 ms` |

This repository enables retries, but it does not define an explicit dead-letter exchange or dead-letter queue.

## API

### Create employee

```http
POST http://localhost:8084/api/employees
Content-Type: application/json
```

Request body:

```json
{
  "id": "emp-001",
  "name": "Alice Smith",
  "email": "alice@example.com",
  "department": "Engineering"
}
```

Validation rules:

- `id` must not be blank
- `name` must not be blank
- `email` must not be blank and must be a valid email address
- `department` must not be blank

Behavior:

- Generates an event with a random `eventId`
- Sets `eventType` to `EMPLOYEE_CREATED`
- Adds the current UTC timestamp as `occurredAt`
- Publishes the event to `employee.events.fanout`
- Returns the submitted employee payload with `201 Created`

Successful response:

```json
{
  "id": "emp-001",
  "name": "Alice Smith",
  "email": "alice@example.com",
  "department": "Engineering"
}
```

Invalid input returns `400 Bad Request`.

## Consumer Behavior

- `NotificationConsumer` listens on `employee.notification.queue` and simulates sending a welcome email.
- `AuditConsumer` listens on `employee.audit.queue` and simulates writing an audit record.

Both consumers currently log their work and use stub methods instead of external integrations. That keeps the sample focused on RabbitMQ wiring rather than downstream infrastructure.

## Project Structure

```text
src/main/java/com/cleancoders/rabbitmqpubsub/
├── RabbitmqPubsubApplication.java
├── config/
│   └── RabbitMQConfig.java
├── consumer/
│   ├── AuditConsumer.java
│   └── NotificationConsumer.java
├── controller/
│   └── EmployeeController.java
├── dto/
│   ├── Employee.java
│   └── EmployeeCreatedEvent.java
└── publisher/
    └── EmployeeEventPublisher.java
```

Test coverage lives under `src/test/java/com/cleancoders/rabbitmqpubsub/` and includes:

- Web layer tests for the employee endpoint
- Unit tests for the publisher
- Unit tests for both consumers
- A Spring Boot context smoke test

## Running Tests

Windows:

```powershell
./gradlew.bat test
./gradlew.bat jacocoTestReport
```

macOS or Linux:

```bash
./gradlew test
./gradlew jacocoTestReport
```

The HTML coverage report is generated at `build/reports/jacoco/test/html/index.html`.

## Notes

- The sample is event-driven only; there is no database or employee repository in the current implementation.
- Message payloads are serialized as JSON through Spring AMQP's Jackson message converter.
- Logging for `com.cleancoders.rabbitmqpubsub` is set to `DEBUG` in the application configuration.

## License

This project is provided as a learning sample and carries no specific license.
