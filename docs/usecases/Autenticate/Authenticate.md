---
layout: default
title: Authenticate
parent: Use Cases
nav_order: 1
---

## Authenticate

### Normal flow

* A client sends a e-mail and password
* The service validates the input data and verifies if the users exists in the
  system
* If the users exists, authenticate the user and return a signed JWT

## HTTP(S) endpoints

* /api/users/authenticate
  * HTTP method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json
  * Examples:

        * Example of request:
        ```shell
            curl -X POST \
            'http://localhost:8080/api/users/authenticate' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com' \
            --data-urlencode 'password=12345678'
        ```
        * Example of response: an signed JWT:
        ```txt
        eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJ1cG4iOiJyb2RyaWdvQHRlc3RlLmNvbSIsImdyb3VwcyI6WyJ1c2VyIl0sImNfaGFzaCI6Ijc5NjBjMjk1LWQ0NmEtNGI2NC1hNGZiLTE2ZWQxNGYzZTk1NSIsImlhdCI6MTY1NzgzNzY1MCwiZXhwIjoxNjU3ODM3OTUwLCJqdGkiOiIzZjdlOThhMy1hMTAwLTQxOTQtODM0Ny0yMWQwZjRjNDJhYTgifQ.rsHHrOZ5LStCYXREGw0iN7_y7geraKtMYin2OGVchrFF0iX2Stu6m4KGRXVmd3vx_vU3l7RyBN9aFjAO0mm1ScJ-wzP8DQPsuSm1pgw2RBKtTitvi4M7XjsP9bZyuyzP-hWbB6KPhB3oZSzh91nyqqWTQUJrUDsXnuNP3XUX6YAwlXZd5SrxQeNfvcaJ9N2Cj85hw8L5Nm-20P7dt3yj4IZE5QvZ1JYLyNzWZWkYWyr9ffR9v1q83dbxJMaABL8R1sSFZjBTwsQSQOBNSwkCF1U_x2tqj0aZW1w4cqQnpHYAY32AtgmrDHVfdjyQld1g7Qx42C2AoP_ZTWpxZ9vwDg
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
