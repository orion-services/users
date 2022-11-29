---
layout: default
title: Delete
parent: Use Cases
nav_order: 2
---

# Delete User

## Normal flow

* A client sends a e-mail.
* The service validates the input data and verifies if the users exists in the system.
* If the users exists, delete the user.

# Technical specifications

## HTTP endpoints

* /api/user/delete
    * Method: DELETE
    * Consume: application/x-www-form-urlencoded
    * Produces: application/json
    * Examples:

        * Request:
        ```shell
            curl -X DELETE \
            'http://localhost:8080/api/user/authenticate' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com'
        ```
        * Response:
        ```1
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be IllegalArgumentException. However, in the RESTful Web Service layer will be transformed to Bad Request (HTTP 400).
