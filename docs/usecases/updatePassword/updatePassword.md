---
layout: default
title: Update password
parent: Use Cases
nav_order: 8
---

## Normal flow

* A client sends user's e-mail, the current and the new password.
* The service check to see if the user's e-mail exists and if the given password
  follow the password rules. Thus, update the user's password and return a
  User in JSON.

## HTTPS endpoints

* /users/update/password
  * Method: PUT
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json
  * Examples:

    * Example of request:

        ```shell
            curl -X PUT \
            'http://localhost:8080/users/update/password' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com' \
            --data-urlencode 'password=12345678' \
            --data-urlencode 'newPassword=87654321'
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

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
