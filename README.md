# Flight Booking System – Microservices Architecture (Event-Driven)

A production‑grade **Event‑Driven Flight Booking System** built using **Java, Spring Boot, RabbitMQ**, and **Microservices Architecture**. This project demonstrates service decoupling, asynchronous workflows, choreography-style Saga, idempotency, resilience, and real-world booking workflow implementation.

---

# System Overview
This system follows an **event‑driven, asynchronous Saga-like workflow** for booking flights. The Booking Service orchestrates the process, while Inventory, Payment, and Notification services react to events.

All services use:
- **RabbitMQ** for inter-service communication
- **Idempotency** (via common-lib) to avoid duplicate event processing
- **Local databases** for state persistence
- **API Gateway** for routing external traffic
- **Feign Clients** for service-to-service synchronous calls

---

##  Tech Stack

### **Backend Technologies**
- **Java 17**
- **Spring Boot 3.x**
- **Spring Cloud** (API Gateway, OpenFeign, Eureka)
- **Spring Data JPA / Hibernate**
- **Spring AMQP (RabbitMQ Integration)**
- **Spring Mail (JavaMailSender)**
- **Gradle**
- **Lombok**

### **Infrastructure & Messaging**
- **RabbitMQ**
- **PostgreSQL** (per-service database)
- **Eureka Service Discovery**
- **API Gateway**
- **Docker**
- **Redis** (caching & rate limiter backing store)

---

##  Microservices Overview

### 1. **API Gateway**

Acts as the single entry point for clients. Key responsibilities:

- Routes incoming requests 
- Handles **Authentication** (JWT) and **Rate Limiting** (Redis)
- Public endpoints:
    - `GET /search` → Flight Search Service
    - `POST /book` → Booking Service

---

### 2. **Flight Search Service**

- Public APIs for direct searches.
- Also consumed by Booking Service via **Feign Client**.
- **Caching**: Read-optimized to reduce load.

---

### **3. Booking Service (Orchestrator)**
Responsible for:
- Validating user booking request
- Calling Flight Search Service via **Feign**
- Creating a booking in **PENDING** state
- Publishing `booking.created` event to **RabbitMQ**
- Consuming **Failure / Success Events** from other services
- Updating final booking state (`CONFIRMED` / `FAILED`)

---

### **4. Inventory Service**
Consuming `booking.created` event:
- Idempotency check
- Validate availability and reserve seats
- Publish events:
    - `inventory.success`
    - `inventory.failed`

---

### **5. Payment Service**
Consumes `inventory.success`:
- Idempotency check
- Perform dummy payment processing
- Publish events:
    - `payment.completed`
    - `payment.failed`

---

### 6. **Notification Service**

Listens to **all major events**:
- `booking.created`
- `inventory.success`
- `inventory.failed`
- `payment.completed`
- `payment.failed`

Uses JavaMailSender to send **real emails**.

---

### 7. **common-lib Module**

Shared across all microservices:

- Event DTOs (BookingEvent, InventoryEvent, PaymentEvent)
- Idempotency Layer (ProcessedEvent + IdempotencyService)

Pattern used by consumers:

```
if (idempotencyService.isProcessed(eventId)) return;
// process event
idempotencyService.markProcessed(eventId);
```

---

##  Event Routing

**Queues**:

```
booking-service-queue
inventory-service-queue
payment-service-queue
notification-service-queue
```

Each service declares its own queue(s) on startup and binds routing keys as needed.

---

##  Architecture ASCII Diagram (E2E)

```
                           ┌──────────────────────────┐
                           │        CLIENT / UI       │
                           └──────────────┬───────────┘
                                          │
          ┌───────────────────────────────┼──────────────────────────────┐
          │                               │                              │
          ▼                               ▼                              ▼
    ┌───────────────────┐        ┌──────────────────────────┐   (Other GET calls etc.)
    │  GET /search      │        │       API GATEWAY        │
    │ (Public endpoint) │        │ (Routing + Auth + Rate)  │
    └───────┬───────────┘        └───────────────┬──────────┘
            |                                    │
            ▼                                    │
    ┌──────────────────────────┐                 │
    │   FLIGHT SEARCH SERVICE  │ <───────────────┘
    └──────────────────────────┘
    
    
    
                       ┌──────────────────────────┐
                       │       API GATEWAY        │
                       │ (Routing + Auth + Rate)  │
                       └──────────────┬───────────┘
                                      │ POST /book
                                      ▼
                       ┌──────────────────────────────┐
                       │      BOOKING SERVICE         │
                       │------------------------------│
                       │ 1. Call SearchService        │
                       │    to validate flight        │
                       │    via Feign Client          │
                       │------------------------------│
                       │ If invalid → STOP (no events)│
                       │------------------------------│
                       │ 2. Create Booking=PENDING    │
                       │ 3. Publish booking.created   │
                       └──────────────┬───────────────┘
                                      │
                                      ▼
                         ┌──────────────────────────┐
                         │         RABBITMQ         │
                         └────────────┬─────────────┘
                                      │
                                      ▼
                         ┌──────────────────────────┐
                         │    INVENTORY SERVICE     │
                         │--------------------------│
                         │ Idempotency Check        │
                         │ Check seat availability  │
                         │ Reserve seat             │
                         │ Publish:                 │
                         │   • inventory.success    │
                         │   • inventory.failed     │
                         └──────────────┬───────────┘
                                        │
                    [inventory.success] │     [inventory.failed]
                          ┌─────────────┘─────────────┐
                          │                           │
                          ▼                           ▼
           ┌──────────────────────────┐      ┌──────────────────────────┐
           │     PAYMENT SERVICE      │      │    BOOKING SERVICE       │
           │--------------------------│      │ (Final Failure Update)   │
           │ Idempotency Check        │      │--------------------------│
           │ Dummy Charge             │      │ Booking = FAILED         │
           │ Update DB                │      └───────────┬──────────────┘
           │ Publish:                 │                  │  
           │  • payment.completed     │                  │   
           │  • payment.failed        │                  │  forwarded
           └──────────────────┬───────┘                  │  to notification
                              │──────────────────────────┘  [inventory.failed]
                              │            
         [payment.completed]  │    [payment.failed]
                ┌─────────────┘───────────────────┐                                     
                │                                 │
                │                                 │    
                ▼                                 ▼   
    ┌──────────────────────────┐   ┌──────────────────────────────┐                  
    │  NOTIFICATION SERVICE    │   │   BOOKING SERVICE            │                  
    │--------------------------│   │ (Final Status Update)        │
    │ Idempotency Check        │   │------------------------------│
    │ Sends Emails for:        │   │ payment.completed → CONFIRMED│
    │  • booking.created       │   │ payment.failed    → FAILED   │
    │  • booking.failed        │   │ inventory.failed  → FAILED   │
    │  • inventory.success     │   └──────────────────────────────┘ 
    │  • inventory.failed      │   
    │  • payment.completed     │                               
    └──────────────────────────┘                                
                                
```

---

# **End-to-End Flow (E2E Logic)**

## **1️. API Gateway → Booking Service**
User calls:
```
POST /book
```
Booking Service:
1. Calls Flight Search Service via Feign
2. If invalid → return error (NO events published)
3. Creates a booking with status **PENDING**
4. Publishes `booking.created`

---

## **2️. Booking → Inventory (via RabbitMQ)**
Inventory Service consumes `booking.created`:
- Performs an idempotency check
- Validates seats
- Reserves seats
- Publishes:
    - `inventory.success`
    - `inventory.failed`

---

## **3️. Inventory → Payment**
Payment Service consumes `inventory.success`:
- Idempotency check
- Performs dummy payment
- Publishes:
    - `payment.completed`
    - `payment.failed`

---

## **4️. Payment → Notification**
Notification Service consumes all events and sends proper emails for:
- Booking confirmation
- Payment success
- Inventory failure
- Payment failure

---

## **5️. Notification → Booking (Final State Update)**
Booking Service consumes:
- `inventory.failed` → Booking = **FAILED**
- `payment.failed` → Booking = **FAILED**
- `payment.completed` → Booking = **CONFIRMED**

Booking Service = **Source of truth**.

---
