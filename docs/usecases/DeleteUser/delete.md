---
layout: default
title: Delete User
parent: Use Cases
nav_order: 5
---

## Delete User

This use case is responsible for deleting a user from the system. The endpoint requires admin role authentication and permanently removes the user account.

### Normal flow

* A client sends an e-mail along with a valid JWT token with admin role.
* The service validates the JWT token and verifies that the requester has admin role.
* The service validates the input data and verifies if the user exists in the system.
* If the user exists, the service deletes the user from the data repository.
* The service returns a success response indicating the user was deleted.

## HTTPS endpoints

* /users/delete
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json
  * Requires: JWT token with admin role in Authorization header

### Request Example

```shell
curl -X POST \
  'http://localhost:8080/users/delete' \
  --header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9...' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com'
```

### Response Example

When the user is successfully deleted, the response contains:

```json
true
```

**Note:** The endpoint returns HTTP 200 (OK) with a boolean `true` value indicating successful deletion.

## Exceptions

RESTful Web Service layer will return:
- HTTP 401 (Unauthorized) if:
  - The JWT token is missing or invalid
  - The JWT token does not have admin role
- HTTP 400 (Bad Request) if:
  - The email format is invalid
  - The email parameter is missing
  - The user does not exist in the system

In the use case layer, exceptions related with arguments will be `IllegalArgumentException`. However, in the RESTful Web Service layer these will be transformed to Bad Request (HTTP 400) or Unauthorized (HTTP 401) as appropriate.
