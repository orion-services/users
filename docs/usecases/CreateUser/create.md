---
layout: default
title: Create User
parent: Use Cases
nav_order: 3
---

## Create User

### Normal flow

* A client sends a name, e-mail and password.
* The service receives and validates the data. The name must be not empty,
  the e-mail must be unique in the server and the format must be valid and,
  the password must be bigger than eight characters.
* The service encrypts the password and generates an identifier (hash) of the
  new user.
* The service creates the new user in the data repository and sends an e-mail to
  the user with the validation code.
* The service returns some user's data.

### Sequence diagram of the normal flow

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/CreateUser/sequence.puml">
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/CreateUser/sequence.puml" alt="Sequence" width="70%" height="70%"/>
    </a>
</center>

### HTTP(S) endpoints

* /api/users/create
* HTTP method: POST
* Consumes: application/x-www-form-urlencoded
* Produces: application/json
* Examples:

  * Example of request:

    ```shell
            curl -X 'POST' \
            'http://localhost:8080/api/users/create' \
            -H 'accept: application/json' \
            -H 'Content-Type: application/x-www-form-urlencoded' \
            -d 'name=Orion&email=orion%40test.com&password=12345678'
    ```

  * Example of response: User in JSON.

    ```json
        {
            "hash": "7eba8ef2-426b-446a-9f05-4ab67e71383d",
            "name": "Orion",
            "email": "orion@test.com",
            "emailValid": false
        }
    ```

### Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
