# Shortify - URL Shortener API

### Overview
This is a URL Shortener API built with Kotlin and Spring Boot. 
It provides a RESTful interface to shorten long URLs and retrieve the original URLs.

### Features
* Shorten URLs: Generate a short URL from a long URL.

* Retrieve URLs: Resolve a short URL to its original form.

* Base62 Encoding: Efficient and readable URL identifiers.

Validation: Ensures input URLs are valid before processing.

### API Endpoints
1. Shorten a URL

Request Body: 
```
{
  "longUrl": "https://www.dkb.de/fragen-antworten/ich-kann-mich-nicht-im-banking-anmelden-was-nun"
}
```

Response Body:
```
{
  "shortUrl": "http://localhost:8080/a1B3c"
}
```

2. Expand a Short URL

Endpoint: GET /{short_id}

### Running the Project Locally

First run docker compose to create the Database and Redis
```
docker compose up
```
Then run bootRun

```
./gradlew bootRun
```
The API will be available at http://localhost:8080

### Executing E2E Tests

```
./gradlew test
```
