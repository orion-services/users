---
layout: default
title: Login
parent: Use Cases
nav_order: 1
---

## Login

This use case is responsible for authenticating a user in the system. The endpoint returns a `LoginResponseDTO` that may contain authentication credentials or indicate that two-factor authentication (2FA) is required.

### Normal flow

* A client sends an e-mail and password.
* The service validates the input data and verifies if the user exists in the system.
* If the user exists and has 2FA enabled with `require2FAForBasicLogin` set to `true`, the service returns a `LoginResponseDTO` indicating that 2FA is required.
* If the user exists and 2FA is not required, the service returns a `LoginResponseDTO` containing the user data and a signed JWT token.

### LoginResponseDTO

The `/users/login` endpoint returns a `LoginResponseDTO` object that can represent different authentication states:

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

- `authentication` (AuthenticationDTO | null): Contains the user data and JWT token when login is successful. This field is `null` when 2FA is required.
- `requires2FA` (boolean): Indicates whether two-factor authentication is required to complete the login process.
- `message` (string | null): Optional message providing additional information, typically "2FA code required" when 2FA is needed.

**AuthenticationDTO Structure:**

When `authentication` is present, it contains:

- `user` (UserEntity): The authenticated user object containing:
  - `hash`: Unique identifier for the user
  - `name`: User's display name
  - `email`: User's email address
  - `emailValid`: Whether the email has been validated
  - `secret2FA`: The 2FA secret (null if not configured)
  - `using2FA`: Whether 2FA is enabled for this user
- `token` (string): A signed JWT token that can be used for authenticated requests

## HTTPS endpoints

* /users/login
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json

### Request Example

```shell
curl -X POST \
  'http://localhost:8080/users/login' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@services.dev' \
  --data-urlencode 'password=12345678'
```

### Response Examples

#### Example 1: Successful Login (without 2FA requirement)

When the user exists and 2FA is not required, the response contains the authentication data:

```json
{
  "authentication": {
    "user": {
      "hash": "53012a1a-b8ec-40f4-a81e-bc8b97ddab75",
      "name": "Orion",
      "email": "orion@services.dev",
      "emailValid": false,
      "secret2FA": null,
      "using2FA": false
    },
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvcmlvbi11c2VycyIsInVwbiI6Im9yaW9uQHNlcnZpY2VzLmRldiIsImdyb3VwcyI6WyJ1c2VyIl0sImNfaGFzaCI6IjUzMDEyYTFhLWI4ZWMtNDBmNC1hODFlLWJjOGI5N2RkYWI3NSIsImVtYWlsIjoib3Jpb25Ac2VydmljZXMuZGV2IiwiaWF0IjoxNzE1Mzk0NzA0LCJleHAiOjE3MTUzOTUwMDQsImp0aSI6ImMzYjZkZmFkLTAyMDAtNDc3YS05MDJmLTU0ZDg5YjdiMTUzYyJ9.I93SpcxIm31wfMQeiFLuUuuWuwlG-C0aGascSEDseRueILn9Tf5shEyNDMLQr6QRNhQbNjRjnCwe_quenVfjBEF_BLgtDDq7maoqpzDdrnDoKxtxex0dIXmRg2ABZoktB-jBo8yJcflandp1FUe7hG1VduE2E8D6WqvUQiNrhhCiiEZ4d5Moc1H11S3YGg3X1U-QnWUGx70FYQG4Qo-1Ini7T6miC0xCxSJRxumXKKtBRLYMDizp5qPIVoVIatJUu4WgoVZWliStmE7wBu6X_La7z4rAddgIlGRiqLZPkaSruzO2PP3i_T1Ezupcw9ol6LP_nlPaOQHeAjJ7aSQMyA"
  },
  "requires2FA": false,
  "message": null
}
```

#### Example 2: Login Requiring 2FA

When the user has 2FA enabled and `require2FAForBasicLogin` is set to `true`, the response indicates that a 2FA code is required:

```json
{
  "authentication": null,
  "requires2FA": true,
  "message": "2FA code required"
}
```

In this case, the client should:
1. Extract the 2FA code from the user (typically from an authenticator app)
2. Make a subsequent request to `/users/login/2fa` with the email and 2FA code
3. The 2FA endpoint will return a complete `LoginResponseDTO` with authentication data upon successful validation

### Handling the Response

**When `requires2FA` is `false`:**
- Use the `token` from `authentication.token` for subsequent authenticated requests
- Store the token securely (e.g., in localStorage or httpOnly cookie)
- Include the token in the `Authorization` header as `Bearer {token}`

**When `requires2FA` is `true`:**
- Prompt the user for their 2FA code
- Make a POST request to `/users/login/2fa` with:
  - `email`: The user's email
  - `code`: The 2FA code from the authenticator app
- The 2FA endpoint will return a `LoginResponseDTO` with `authentication` populated upon success

## Exceptions

RESTful Web Service layer will return a HTTP 401 (Unauthorized) if the user does not exist or the password is incorrect. If the request is invalid, for example, without the required parameters, the service will return a HTTP 400 (Bad Request).

