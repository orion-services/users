---
layout: default
title: Update
parent: Use Cases
nav_order: 3
---

# Update email

## Normal flow

* A client sends two email addresses, the actual and the new.
* The service validates the input data and verifies if the users exists in the system, so updates the user email.
* If the users exists, update the user's email.

# Technical specifications

## HTTP endpoints

* /api/users/update/email
    * Method: PUT
    * Consume: application/x-www-form-urlencoded
    * Produce: application/json
    * Examples:

        * Request:
        ```shell
            curl -X PUT \
            'http://localhost:8080/api/users/update/email' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com' \
            --data-urlencode 'newEmail=orionOrion@test.com'
        ```
        * Response:
        ```json
        {
            "hash": "49819fac-58d9-4a09-9ee0-1eb1c7141eda",
            "name": "Orion",
            "email": "orionOrion@test.com"
        }
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be IllegalArgumentException. However, in the RESTful Web Service layer will be transformed to Bad Request (HTTP 400).

# Update password

## Normal flow

* A client sends user's email address, the actual and the new password.
* The service validates the input data and verifies if the users exists in the system and if the given password is correct, so updates the user password.
* If the users exists, update the user's password.

# Technical specifications

## HTTP endpoints

* /api/users/update/password
    * Method: PUT
    * Consume: application/x-www-form-urlencoded
    * Produce: application/json
    * Examples:

        * Request:
        ```shell
            curl -X PUT \
            'http://localhost:8080/api/users/update/password' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com' \
            --data-urlencode 'password=12345678' \
            --data-urlencode 'newPassword=87654321'
        ```
        * Response:
        ```json
        {
            "hash": "49819fac-58d9-4a09-9ee0-1eb1c7141eda",
            "name": "Orion",
            "email": "orion@test.com"
        }
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be IllegalArgumentException. However, in the RESTful Web Service layer will be transformed to Bad Request (HTTP 400).

# Recover password

## Normal flow

* A client sends user's email address.
* The service validates the input data and verifies if the users exists in the system, so send a email to the user containing the new auto generated password.
* If the user exists, send the new auto generated password by email.

# Technical specifications

## HTTP endpoints

* /api/users/recoverPassword
    * Method: POST
    * Consume: application/x-www-form-urlencoded
    * Examples:

        * Request:
        ```shell
            curl -X POST \
            'http://localhost:8080/api/users/update/recoverPassword' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com' \
           ```
        * No Response (204):


## Exceptions

In the use case layer, exceptions related with arguments will be IllegalArgumentException. However, in the RESTful Web Service layer will be transformed to Bad Request (HTTP 400).
