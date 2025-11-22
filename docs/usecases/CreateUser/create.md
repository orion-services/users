---
layout: default
title: Create User
parent: Use Cases
nav_order: 3
---

## Create User

This use case is responsible for creating a new user in the system. The endpoint validates the input data, creates the user account, sends an email validation code to the user, and returns the created user data.

### Normal flow

* A client sends a name, e-mail and password.
* The service receives and validates the data:
  * The name must not be empty
  * The e-mail must be unique in the server and the format must be valid
  * The password must be bigger than eight characters
* The service encrypts the password and generates a unique identifier (hash) for the new user.
* The service creates the new user in the data repository and sends an e-mail to the user with the validation code.
* The service returns the created user's data in JSON format.

### Sequence diagram of the normal flow

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/CreateUser/sequence.puml">
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/CreateUser/sequence.puml" alt="Sequence" width="70%" height="70%"/>
    </a>
</center>

## HTTPS endpoints

* /users/create
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json

### Request Example

```shell
curl -X POST \
  'http://localhost:8080/users/create' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'name=Orion' \
  --data-urlencode 'email=orion@services.dev' \
  --data-urlencode 'password=12345678'
```

### Response Example

When the user is successfully created, the response contains the user data:

```json
{
  "hash": "08f9c3ca-e22e-457f-822a-2d8efafbc720",
  "name": "Orion",
  "email": "orion@services.dev",
  "emailValid": false,
  "secret2FA": null,
  "using2FA": false
}
```

**Response Fields:**

- `hash` (string): Unique identifier for the user
- `name` (string): User's display name
- `email` (string): User's email address
- `emailValid` (boolean): Whether the email has been validated (initially `false`)
- `secret2FA` (string | null): The 2FA secret (null if not configured)
- `using2FA` (boolean): Whether 2FA is enabled for this user (initially `false`)

**Note:** After user creation, an email validation code is sent to the user's email address. The user must validate their email using the `/users/validateEmail` endpoint before `emailValid` becomes `true`.

## Exceptions

RESTful Web Service layer will return a HTTP 400 (Bad Request) if:
- The name is empty
- The email format is invalid
- The email already exists in the system
- The password does not meet the requirements (must be bigger than eight characters)
- Any required parameter is missing

In the use case layer, exceptions related with arguments will be `IllegalArgumentException`. However, in the RESTful Web Service layer these will be transformed to Bad Request (HTTP 400).
