# Discount Service

A Spring Boot RESTful service for managing discounts and users, and applying discount logic to bills. The service uses MongoDB for data storage and supports secure authentication (see below for details).


## Prerequisites

- Docker installed on your machine. [Download link](https://www.docker.com/get-started).
- JDK 21 or higher [Download link](https://www.oracle.com/java/technologies/downloads/#java21).
- HTTP client such as Postman [Download link](https://www.postman.com/downloads/).
- Maven 3.8 or higher — [Download link](https://maven.apache.org/download.cgi).

## Project Documentation

All project documentation is located in the `docs/` directory. This includes:
- **Class Diagram:** Visual representation of the main classes and their relationships.
- **High Level Diagram:** Overview of the system architecture and main components.
- **Swagger API Documentation:** OpenAPI specification for all REST endpoints.
- **Postman Collection:** Example requests for testing the API with Postman.

## Features
- Manage discounts (CRUD)
- Manage users (CRUD)
- Apply discount logic to bills
- RESTful API endpoints
- Docker support for easy deployment

## API Endpoints

### 1. `/v1/api/discounts`
- **Method:** GET
- **Description:** Returns a list of all discounts

### 2. `/v1/api/discounts/{id}`
- **Method:** GET
- **Description:** Returns a discount by ID

### 3. `/v1/api/discounts`
- **Method:** POST
- **Description:** Create a new discount

### 4. `/v1/api/discounts/{id}`
- **Method:** PUT
- **Description:** Update an existing discount

### 5. `/v1/api/discounts/{id}`
- **Method:** DELETE
- **Description:** Delete a discount by ID

### 6. `/v1/api/discounts/apply`
- **Method:** POST
- **Description:** Apply discount logic to a bill (send bill data in request body)

### 7. `/v1/api/users`
- **Method:** GET
- **Description:** Returns a list of all users

### 8. `/v1/api/users/{id}`
- **Method:** GET
- **Description:** Returns a user by ID

### 9. `/v1/api/users`
- **Method:** POST
- **Description:** Create a new user

### 10. `/v1/api/users/{id}`
- **Method:** PUT
- **Description:** Update an existing user

### 11. `/v1/api/users/{id}`
- **Method:** DELETE
- **Description:** Delete a user by ID

## Quick Start

1. **Clone the repository**
2. **Configure MongoDB** (see `application.yml` for connection details)
3. **Build the project:**
   ```sh
   ./mvnw clean install
   ```
4. **Run with Docker Compose:**
   ```sh
   docker-compose up --build
   ```
5. **The service will be available at** `http://localhost:8080`

## Authentication
- If authentication is enabled, obtain a token from the authentication provider (e.g., Keycloak) and include it as a Bearer token in your requests.

## Author
- **Author:** Saleh Alshaikhi
- **Contact:** shaikhisaleh@gmail.com

Thank you for using this application!