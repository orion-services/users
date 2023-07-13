---
layout: default
title: Validade E-mail
parent: Use Cases
nav_order: 4
---

## Validade E-mail

### Normal flow

* A client sends the validation code and e-mail.
* The service validates the code to the e-mail.
* If the validation code is correct, the service returns just a string true.

## HTTP(S) endpoints

* /users/validateEmail
  * HTTP method: GET
  * Consumes: text/plain
  * Produces: text/plain
  * Examples:

    * Example of request:

        ```shell
           curl -X 'GET' \
                'http://localhost:8080/users/validateEmail?code=d32c2a8e-ea4b-4260-b4d7-b3e62d8488e1&email=orion%40test.com' \
                -H 'accept: application/json'
        ```

    * Example of response:

        ```txt
            true
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
