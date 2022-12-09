---
layout: default
title: Recover Password
parent: Use Cases
nav_order: 6
---

## Normal flow

* A client sends the e-mail.
* If the e-mail exists, the service generates and sends a new password to the
  user.

## HTTP(S) endpoints

* api/users/recoverPassword
  * HTTP method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: HTTP 204 (Undocumented)
  * Examples:

    * Example of request:

        ```shell
           curl -X 'POST' \
                'http://localhost:8080/api/users/recoverPassword' \
                -H 'accept: */*' \
                -H 'Content-Type: application/x-www-form-urlencoded' \
                -d 'email=orion%40test.com'
        ```

    * Example of response:

        ```
            204 Undocumented
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
