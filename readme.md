# URL Shortener Service

A **high-performance URL shortening service** built with Java, Spring Boot, MySQL, and Redis. It generates short URLs, supports millions of requests per day, provides caching for fast redirects, and implements collision-safe short code generation.

---

## Features

* Shorten long URLs with a **unique Base62 code**
* **Redirect** short URLs to original long URLs
* **Caching** with Redis for high-throughput reads
* **Rate limiting** support (100 request/min)
* **Collision-safe** short code generation
* Fully **RESTful API** design
* Handles invalid or missing URL schemes

---

## Tech Stack

| Layer      | Technology                                      |
| ---------- | ----------------------------------------------- |
| Backend    | Java 17, Spring Boot                            |
| Database   | MySQL                                           |
| Caching    | Redis                                           |
| Build Tool | Maven                                           |
| Validation | Jakarta Bean Validation (`@Valid`, `@NotBlank`) |
| Utilities  | Lombok (`@Data`, `@AllArgsConstructor`)         |
| Logging    | Spring Boot Default (Logback)                   |

---

## Project Structure

```
src/main/java/com/deepak/urlshortener
├── config
│   ├── CacheConfig.java
│   ├── RedisConfig.java
│   └── RateLimitConfig.java 
│
├── constant
│   └── AppConstants.java
│
├── controller
│   └── UrlShortenerController.java 
│
├── domain
│   └── ShortUrl.java
│
├── dto
│   ├── CreateShortUrlRequest.java
│   └── CreateShortUrlResponse.java
│
├── exception
│   ├── UrlNotFoundException.java
│   └── GlobalExceptionHandler.java
│
├── ratelimit                         
│   ├── RateLimiter.java             
│   └── RedisRateLimiter.java 
│
├── repository
│   └── ShortUrlRepository.java
│
├── service
│   ├── UrlShortenerService.java
│   └── impl
│       └── UrlShortenerServiceImpl.java
│
├── util
│   └── Base62Encoder.java
│
└── UrlShortenerApplication.java

```

---

## Database Setup

### 1. Create Database

```sql
CREATE DATABASE url_shortener;
```

### 2. Create Table (Optional, JPA auto-creates it)

```sql
USE url_shortener;

CREATE TABLE short_url (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    short_code VARCHAR(20) NOT NULL UNIQUE,
    long_url TEXT NOT NULL,
    created_at DATETIME NOT NULL
);
```

**Columns:**

| Column       | Type     | Description                              |
| ------------ | -------- | ---------------------------------------- |
| `id`         | BIGINT   | Primary key, auto-increment              |
| `short_code` | VARCHAR  | Unique code generated for each short URL |
| `long_url`   | TEXT     | Original long URL                        |
| `created_at` | DATETIME | Timestamp when the short URL was created |

**Optional:** Grant privileges to your MySQL user:

```sql
GRANT ALL PRIVILEGES ON url_shortener.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

> Note: If using `spring.jpa.hibernate.ddl-auto: update`, the table will be auto-created by JPA.

---

## Configuration

### 1. MySQL

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/url_shortener
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
```

### 2. Redis

```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

* Used for caching short → long URL mapping
* TTL: 24 hours

---

## Running the Application

1. Make sure **MySQL** and **Redis** are running.
2. Build & run:

```bash
mvn clean package
mvn spring-boot:run
```

3. App runs on:

```
http://localhost:8080
```

---

## API Endpoints

### 1. Create Short URL

**POST** `/api/v1/shorten`

* **Headers:**
  `Content-Type: application/json`

* **Request Body:**

```json
{
  "longUrl": "https://www.leetcode.com"
}
```

* **Response:**

```json
{
  "shortCode": "2PCo9kaH",
  "shortUrl": "http://localhost:8080/2PCo9kaH"
}
```

> The `shortCode` is generated server-side; clients don’t provide it.

---

### 2. Redirect Short URL

**GET** `/{shortCode}`

* Example:

```
GET http://localhost:8080/2PCo9kaH
```

* **Behavior:**

    * Returns **HTTP 302**
    * Redirects to the original URL (`longUrl`)
    * Prepends `https://` if missing in DB

* **Postman / Browser:**

    * Browser automatically follows redirect
    * Postman: disable "follow redirects" to see `302` response

---

## Redis Caching Verification

* Open CLI:

```bash
redis-cli
```

* Check all keys:

```bash
KEYS *
```

* Inspect cached URL:

```bash
GET short_url:2PCo9kaH
```

* Cache only populates **after first GET request**.

---

## Testing in Postman

1. POST `/api/v1/shorten` → get `shortCode`
2. GET `/{shortCode}` → redirect to `longUrl`
3. Optional: GET multiple times → verify Redis caching
4. Optional: Send invalid URL → verify 400 or exception

---

## Design Notes

* **Single controller** for API + redirect → avoids folder restructuring
* **Cacheable service layer** → ensures fast redirects without hitting DB every request
* **Collision-safe shortCode** using Base62 + `System.nanoTime()` loop
* **Prepend `https://`** ensures redirect works even for missing schemes

---

## Design Diagram

                           +----------------+
                           |     Client     |
                           | (Browser/API)  |
                           +----------------+
                                      |
                               HTTP / HTTPS
                                      |
                         +-----------------------+
                         |     Rate Limiter      |
                         |   (Redis-backed)     |
                         +-----------------------+
                                      |
                                      v
                    +-----------------------------------+
                    |      UrlShortenerController       |
                    +-----------------------------------+
                                      |
                                      v
                    +-----------------------------------+
                    |       UrlShortenerService         |
                    +-----------------------------------+
                          |                       |
                          |                       |
                 +----------------+       +-------------------+
                 |     Redis      |       |      MySQL        |
                 |  (URL Cache)   |       | (Source of Truth) |
                 +----------------+       +-------------------+
                          |                       |
                          v                       v
                    +-----------------------------------+
                    | Redirect / Short URL Response    |
                    +-----------------------------------+
                                      |
                                      v
                                 +---------+
                                 | Client  |
                                 +---------+
---
