---
layout: default
title: Validate Email
parent: Use Cases
nav_order: 4
---

## Validate Email

This use case is responsible for validating a user's email address using a validation code sent to the user's email. The endpoint verifies the code and marks the email as validated in the system.

### Normal flow

* A client sends the validation code and e-mail as query parameters.
* The service validates the input data and verifies if the user exists in the system.
* The service checks if the validation code matches the code sent to the user's email.
* If the validation code is correct and the user exists, the service marks the email as validated (`emailValid` set to `true`).
* The service returns a boolean `true` value indicating successful validation.

## HTTPS endpoints

* /users/validateEmail
  * Method: GET
  * Consumes: text/plain
  * Produces: text/plain

### Request Example

```shell
curl -X GET \
  'http://localhost:8080/users/validateEmail?code=d32c2a8e-ea4b-4260-b4d7-b3e62d8488e1&email=orion%40test.com' \
  --header 'Accept: text/plain'
```

### Response Example

When the email is successfully validated, the response contains:

```txt
true
```

**Note:** The endpoint returns HTTP 200 (OK) with a plain text response containing the boolean value `true` indicating successful validation.

## Exceptions

RESTful Web Service layer will return a HTTP 400 (Bad Request) if:
- The email format is invalid
- The email parameter is missing
- The code parameter is missing
- The validation code is incorrect or expired
- The user does not exist in the system
- The email has already been validated

In the use case layer, exceptions related with arguments will be `IllegalArgumentException`. However, in the RESTful Web Service layer these will be transformed to Bad Request (HTTP 400).
