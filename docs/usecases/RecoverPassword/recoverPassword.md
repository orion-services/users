---
layout: default
title: Recover Password
parent: Use Cases
nav_order: 6
---

## Recover Password

This use case is responsible for recovering a user's password by generating a new secure password and sending it via email.

### Normal flow

* A client sends the user's e-mail.
* The service validates the e-mail format and checks if the e-mail exists in the system.
* If the e-mail exists, the service generates a new secure password (8 characters with uppercase, lowercase, digits, and special characters).
* The service encrypts the new password and updates it in the database.
* The service sends an email to the user with the new password.
* The service returns HTTP 204 (No Content) indicating success.

## HTTPS endpoints

* /users/recoverPassword
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: text/plain
  * Examples:

    * Example of request:

        ```shell
        curl -X POST \
          'http://localhost:8080/users/recoverPassword' \
          --header 'Accept: */*' \
          --header 'Content-Type: application/x-www-form-urlencoded' \
          --data-urlencode 'email=orion@test.com'
        ```

    * Example of response:

        ```
        HTTP 204 No Content
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).

If the e-mail does not exist in the system, the service will return HTTP 400
(Bad Request) with an error message indicating that the e-mail was not found.
