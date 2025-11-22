---
layout: default
title: Update User
parent: Use Cases
nav_order: 7
---

## Update User

This use case is responsible for updating user information (email and/or password) in the system. The endpoint requires JWT authentication and returns a `LoginResponseDTO` containing the updated user data and a new JWT token.

### Normal flow

* A client sends the current e-mail, optionally the new e-mail and/or the current and new password along with a valid JWT token.
* The service validates the JWT token and extracts the user's email from the token.
* The service validates the access token and the current e-mail to check if the user exists in the service.
* **If updating email:**
  * If the user exists, the service updates the user's e-mail
  * Sets the status of e-mail validation to `false`
  * Sends a message with a code to validate the new e-mail
  * Generates a new access token
* **If updating password:**
  * If the user exists, the service validates the current password
  * If the current password matches, updates the user's password
  * Generates a new access token
* The service returns a `LoginResponseDTO` containing an `AuthenticationDTO` with the new JWT token and updated user information.

### LoginResponseDTO

The `/users/update` endpoint returns a `LoginResponseDTO` object containing the updated user data and a new JWT token:

**Structure:**

```json
{
  "authentication": {
    "user": {
      "hash": "string",
      "name": "string",
      "email": "string",
      "emailValid": boolean,
      "secret2FA": "string | null",
      "using2FA": boolean
    },
    "token": "string (JWT)"
  },
  "requires2FA": boolean,
  "message": "string | null"
}
```

**Fields:**

- `authentication` (AuthenticationDTO): Contains the updated user data and new JWT token
- `requires2FA` (boolean): Always `false` for update operations
- `message` (string | null): Typically `null` for successful updates

**AuthenticationDTO Structure:**

- `user` (UserEntity): The updated user object containing:
  - `hash`: Unique identifier for the user
  - `name`: User's display name
  - `email`: User's email address (updated if `newEmail` was provided)
  - `emailValid`: Whether the email has been validated (set to `false` if email was updated)
  - `secret2FA`: The 2FA secret (null if not configured)
  - `using2FA`: Whether 2FA is enabled for this user
- `token` (string): A new signed JWT token that should be used for subsequent authenticated requests

## HTTPS endpoints

* /users/update
  * Method: PUT
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json
  * Requires: JWT token in Authorization header

### Request Examples

#### Example 1: Update Email Only

```shell
curl -X PUT \
  'http://localhost:8080/users/update' \
  --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com' \
  --data-urlencode 'newEmail=orion@xyzmail.com'
```

#### Example 2: Update Password Only

```shell
curl -X PUT \
  'http://localhost:8080/users/update' \
  --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com' \
  --data-urlencode 'password=12345678' \
  --data-urlencode 'newPassword=87654321'
```

#### Example 3: Update Both Email and Password

```shell
curl -X PUT \
  'http://localhost:8080/users/update' \
  --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com' \
  --data-urlencode 'newEmail=orion@xyzmail.com' \
  --data-urlencode 'password=12345678' \
  --data-urlencode 'newPassword=87654321'
```

### Response Example

When the user is successfully updated, the response contains the updated user data and a new JWT token:

```json
{
  "authentication": {
    "token": "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ...",
    "user": {
      "hash": "7eba8ef2-426b-446a-9f05-4ab67e71383d",
      "name": "Orion",
      "email": "orion@xyzmail.com",
      "emailValid": false,
      "secret2FA": null,
      "using2FA": false
    }
  },
  "requires2FA": false,
  "message": null
}
```

**Note:** After updating the email, `emailValid` is set to `false` and a validation email is sent to the new email address. The user must validate the new email using the `/users/validateEmail` endpoint.

## Parameters

* `email` (required): The current email of the user (must match the email in the JWT token)
* `newEmail` (optional): The new email address. If provided, the email will be updated and a validation email will be sent.
* `password` (optional): The current password. Required if `newPassword` is provided.
* `newPassword` (optional): The new password. Must meet password strength requirements (minimum 8 characters).

**Note:** At least one of `newEmail` or `newPassword` must be provided.

## Exceptions

RESTful Web Service layer will return:
- HTTP 401 (Unauthorized) if:
  - The JWT token is missing or invalid
  - The email in the request does not match the email in the JWT token
  - The current password is incorrect (when updating password)
- HTTP 400 (Bad Request) if:
  - The email format is invalid
  - The new email already exists in the system
  - The password does not meet the requirements
  - Both `newEmail` and `newPassword` are missing
  - `newPassword` is provided but `password` is missing
  - Any required parameter is missing

In the use case layer, exceptions related with arguments will be `IllegalArgumentException`. However, in the RESTful Web Service layer these will be transformed to Bad Request (HTTP 400) or Unauthorized (HTTP 401) as appropriate.
