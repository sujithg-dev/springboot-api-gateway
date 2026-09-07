# Rate Limiter API Gateway


## Overview

A Spring Boot API Gateway providing:

- JWT authentication
- Token Bucket rate limiting
- API request logging
- MongoDB integration
- Application monitoring
- Cloud deployment using Render



## Tech Stack

- Java
- Spring Boot
- Spring Security
- JWT
- MongoDB
- Maven
- Docker
- Render



## Setup Instructions

1. Clone the project 
   - git clone https://github.com/sujithg-dev/springboot-api-gateway cd SpringBootApiGateway

2. Configure environment variables 
   - PORT=8080
   - MONGODB_URI=mongodb+srv://username:password@apigateway.ocgkv6r.mongodb.net/databasename
   - RATE_LIMIT_CAPACITY=5 
   - RATE_LIMIT_REQUESTS_PER_MINUTE=5

3. Build the project
   - mvn clean package

4. Run the application
   - mvn spring-boot:run



## API Documentation

The application runs on:
http://localhost:8080

### Register User

POST /auth/register

{
"username": "user",
"password": "password"
}

### Login

POST /auth/login

{
"username": "user",
"password": "password"
}

Returns a JWT token.


### User Profile

GET /api/user/profile

Header:

Authorization: Bearer <JWT_TOKEN>

#### Rate Limit

When the request limit is exceeded:

HTTP 429 Too Many Requests



## Design Decisions & Tradeoffs

### Token Bucket

Token Bucket was selected because it is simple, efficient, and allows controlled bursts of requests.

### MongoDB

MongoDB provides flexible document storage and is suitable for storing users and API logs.

### Distributed Scaling

The current implementation is suitable for a single gateway instance. For multiple instances, Redis can be used as shared rate-limit storage to maintain consistent limits across instances.



## Deployment URL

### Base URL:

* https://springbootapigateway-1.onrender.com

### Register

* https://springbootapigateway-1.onrender.com/auth/register

### Login

* https://springbootapigateway-1.onrender.com/auth/login

### User Profile

* https://springbootapigateway-1.onrender.com/api/user/profile

### Health Check

* https://springbootapigateway-1.onrender.com/actuator/health