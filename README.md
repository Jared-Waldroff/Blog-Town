# Blog Town

A stateless blogging backend written in Kotlin + Ktor and designed for cloud-native, Kubernetes-based microservice stacks.

## Features
- **OAuth2** authentication  
  - 15-minute access tokens  
  - 7-day refresh tokens  
- **Blog module**  
  - Create, search (keyword / tag), and bookmark posts  
  - Full pagination & server-side validation  
- **Clean Architecture**  
  - Routes → Services → Repositories (interface-backed)  
  - Dependency Injection via **Koin**  
- **Coroutine-based non-blocking I/O** – handles thousands of concurrent requests with minimal threads  
- **Container-ready**: multi-stage Dockerfile, liveness/readiness probes, Helm-friendly manifests  

---

## Prerequisites
* **JDK 17 +**
* Gradle Wrapper (bundled)
* Docker 24 + (for container run)

---

## Quick Start

### 1 · Run locally (dev mode)
```bash
./gradlew run
```

### 2 · Execute tests
```bash
./gradlew test
```

### 3 · Build & Run Docker Image
```bash
docker build -t blog-town:latest .
docker run -p 8080:8080 blog-town:latest
```

----

## REST Endpoints

| Method | Path               | Auth? | Purpose                                   |
| ------ | ------------------ | ----- | ----------------------------------------- |
| POST   | /create-user       | ❌    | Register new user                         |
| POST   | /auth/login        | ❌    | Login → access + refresh tokens           |
| POST   | /auth/refresh      | ❌    | Refresh access token                      |
| POST   | /blogs             | ✅    | Create post                               |
| GET    | /blogs/search      | ❌    | Public search (pagination, tag filter)    |
| POST   | /blogs/{id}/save   | ✅    | Bookmark post                             |
| GET    | /users/{uid}/saved | ✅    | List bookmarks                            |


---

## Design Notes

- **Stateless API** – only tokens are persisted (currently in-memory; easily swapped for DB/Redis).  
- **Interface-backed layers** – swap mocks for real persistence without touching business logic.  
- **Coroutines** – non-blocking I/O keeps threads free, enabling high concurrency.  

