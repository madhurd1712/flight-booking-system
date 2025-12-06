# Flight Booking System

## Architecture
1. **Search Service (8081)**: Postgres DB.
2. **Booking Service (8082)**: Postgres DB. Resilience4j (Rate Limit/Circuit Breaker).
3. **Payment Service (8083)**: Postgres DB. RabbitMQ.

## Setup
1. Start Infra: `docker-compose up -d`
2. Run Services: `./gradlew bootRun` in each folder.
