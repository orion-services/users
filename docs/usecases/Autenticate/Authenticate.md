---
layout: default
title: Authenticate
parent: Use Cases
nav_order: 2
---

# Authenticate

## Normal flow

* A client sends a e-mail and password
* The service validates the input data and verifies if the users exists in the system
* If the users exists, authenticate the user

# Technical specifications

## HTTP endpoints

* /api/users/authenticate
    * Method: POST
    * Consume: application/x-www-form-urlencoded
    * Produce: application/json
    * Examples:

        * Request:
        ```shell
            curl -X POST \
            'http://localhost:8080/api/users/authenticate' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com' \
            --data-urlencode 'password=12345678'
        ```
        * Response JWT:
        ```txt
        eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJ1cG4iOiJyb2RyaWdvQHRlc3RlLmNvbSIsImdyb3VwcyI6WyJ1c2VyIl0sImNfaGFzaCI6Ijc5NjBjMjk1LWQ0NmEtNGI2NC1hNGZiLTE2ZWQxNGYzZTk1NSIsImlhdCI6MTY1NzgzNzY1MCwiZXhwIjoxNjU3ODM3OTUwLCJqdGkiOiIzZjdlOThhMy1hMTAwLTQxOTQtODM0Ny0yMWQwZjRjNDJhYTgifQ.rsHHrOZ5LStCYXREGw0iN7_y7geraKtMYin2OGVchrFF0iX2Stu6m4KGRXVmd3vx_vU3l7RyBN9aFjAO0mm1ScJ-wzP8DQPsuSm1pgw2RBKtTitvi4M7XjsP9bZyuyzP-hWbB6KPhB3oZSzh91nyqqWTQUJrUDsXnuNP3XUX6YAwlXZd5SrxQeNfvcaJ9N2Cj85hw8L5Nm-20P7dt3yj4IZE5QvZ1JYLyNzWZWkYWyr9ffR9v1q83dbxJMaABL8R1sSFZjBTwsQSQOBNSwkCF1U_x2tqj0aZW1w4cqQnpHYAY32AtgmrDHVfdjyQld1g7Qx42C2AoP_ZTWpxZ9vwDg
        ```

* /api/users/createAuthenticate
    * Method: POST
    * Consume: application/x-www-form-urlencoded
    * Produce: application/json
    * Examples:

    * Request:
    ```shell
        curl -X POST \
        'http://localhost:8080/api/users/createAuthenticate' \
        --header 'Accept: */*' \
        --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --data-urlencode 'name=Orion' \
        --data-urlencode 'email=OrionOrion@teste.com' \
        --data-urlencode 'password=12345678'
    ```
    * Response JSON:
    ```json
        {
        "user": {
            "hash": "015444c1-23a9-4db0-91af-494cbcbfb38b",
            "name": "Orion",
            "email": "OrionOrion@teste.com"
        },
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.     eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJ1cG4iOiJPcmlvbk9yaW9uQHRlc3RlLmNvbSIsImdyb3VwcyI6WyJ1c2VyIl0sImNfaGFzaCI6IjAxNTQ0NGMxLTIzYTktNGRiMC05MWFmLTQ5NGNiY2JmYjM4YiIsImlhdCI6MTY1NzgzNzgxMiwiZXhwIjoxNjU3ODM4MTEyLCJqdGkiOiJjNTI5ZDNhYi1jOGMxLTQwNDUtODVmZC1kOGU0MDE2N2M3ZDMifQ.afP1x_WogWcbKLXQW6H9Ina3dIB7f-lhQpE6eoX5nQFEePFe_zFmF5iRlHvE_Bf5VcPSuBlcBmJtotggVgmy9SUSLdVoDzGYV-UHRTsmRdwnmTY62ixiueJT44-hOR_K2lNXpmpsQibHd9GgCZR7wT3OTbX39TbvcVWm0stKWNlbdA7d-qayYRLCaM8MOuZ3spMIQyxm2rRVKf9HbM7Mp93yEI4yx5dQwxJJrKcRTIreEI5i9KlEf69eYSGmIUEbcLg8rRVQ44bQgVZLF-TvZfPdHENdCRsurVW_ZRv1hLRucd6TPrGCWZbhtDs5vpH4GlKuV8_HlAav_T8YW7i9KA"
        }
    ```

## Exceptions

In the use case layer, exceptions related with arguments will be IllegalArgumentException. However, in the RESTful Web Service layer will be transformed to Bad Request (HTTP 400).
