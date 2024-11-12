# Tracking Number Generator

## Overview

This project is a Spring Boot application that provides an API for generating unique tracking numbers for parcels. It uses a combination of inputs to produce a SHA-256 hash, which is then stored in Redis to ensure uniqueness.

## Features

- Generate unique tracking numbers based on multiple input parameters.
- Validate input parameters using Jakarta Bean Validation.
- Uses Redis for storing and checking the existence of tracking numbers in distributed systems (multiple instances).
- Provides a RESTful API with Swagger documentation.

## Technologies Used

- Java 17
- Spring Boot
- Redis
- Maven
- Swagger

## Getting Started

### Prerequisites

- Java 17
- Maven
- Redis Server

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/vkmattaparthi/valuelabs-test.git
   cd valuelabs-test
   ```

2. Build the project using Maven:
   ```bash
   ./mvn clean install
   ```

3. Run the application:
   ```bash
   ./mvn spring-boot:run
   ```

### Running Tests

To run tests, use the following command:
```bash
./mvn test
```

## Usage

Once the application is running, access the Swagger UI at `http://localhost:8080/swagger-ui.html` to explore the available endpoints.

### API Endpoints

- **POST /generate-tracking-number**: Generate a new tracking number with required parameters like `origin_country_id`, `destination_country_id`, `weight`, `created_at`, `customerId`, etc.
