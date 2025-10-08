# Order Service 🛒

A simple microservice for managing orders, built with **Spring Boot + Gradle**, using **PostgreSQL** as database and **RabbitMQ** as message broker.  
All services are orchestrated with **Docker Compose**.

---

## 🚀 Requirements
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- JDK 17 (only required if you want to run locally without Docker)
- Gradle (wrapper included with `gradlew`)

---

## ⚙️ Run the project

### 1. Build and start containers

Gradle must generate the JAR

`./gradlew clean build`

From the project root (where `Dockerfile` and `docker-compose.yml` are located):

`docker-compose up --build`

This will launch:
Postgres, RabbitMQ (UI at http://localhost:15672), 
Order-service at http://localhost:8080.



### 2. Test POST endpoint (create order)

Create a new order with (Example):

`curl -X POST http://localhost:8080/api/orders \`

    -H "Content-Type: application/json" \

    -d '{"customerId": 1,"items":[{"productId": 1001, "quantity": 2, "price": 25.5}]}' 

Response:
A JSON object containing the persisted order with fields such as id, status, createdAt, and items.

### 3. View messages in RabbitMQ GUI

Go to http://localhost:15672.

Navigate to Queues → orders.created.queue

Check enqueued and consumed messages.

When workers consume and publish events, you’ll see activity in Exchanges and Queues.

### 4. Test concurrency

Simulate multiple requests (200 in this example) with a loop:

`curl -s -X POST http://localhost:8080/api/orders \`

`for i in {1..200}; do`

    -H "Content-Type: application/json" \

    -d '{"customerId":'$i',"items":[{"productId":1001,"quantity":1,"price":10.0}]}'
    & done, wait

- Observation:

- Logs will show workers processing.
- Events are published.
- Queue acts as a buffer under load.