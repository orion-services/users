---
layout: default
title: Update e-mail
parent: Use Cases
nav_order: 7
---

## Normal flow

* A client sends two email addresses, the actual and the new.
* The service validates the input data and verifies if the users exists in the
  system, so updates the user email.
* If the users exists, update the user's email.

## HTTP(S) endpoints

* /api/users/update/email
  * HTTP method: PUT
  * Consume: application/x-www-form-urlencoded
  * Produce: application/json
  * Examples:

    * Example of request:

        ```shell
            curl -X PUT \
            'http://localhost:8080/api/users/update/email' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com' \
            --data-urlencode 'newEmail=orionOrion@test.com'
        ```

    * Example of response:

        ```json
        {
            "hash": "49819fac-58d9-4a09-9ee0-1eb1c7141eda",
            "name": "Orion",
            "email": "orionOrion@test.com"
        }
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
