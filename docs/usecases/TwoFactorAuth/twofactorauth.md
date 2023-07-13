---
layout: default
title: Two Factor Authenticate
parent: Use Cases
nav_order: 9
---

## Two Factor Autheticate

## Normal flow generate qrcode

* A client sends a e-mail and password
* The service validates the input data and verifies if the users exists in the
  system
* If the users exists, generate the qrCode to be vinculated do google authenticator

### Sequence diagram of generate qrcode

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/TwoFactorAuth/sequenceGenerateQrCode.puml">
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/TwoFactorAuth/sequenceGenerateQrCode.puml" alt="Sequence" width="70%" height="70%"/>
    </a>
</center>

## Normal flow validate code

* A client sends a e-mail and google auth code
* The service validates the input data and verifies if the users exists in the
  system
* If the users exists, authenticate the user and return a signed JWT

### Sequence diagram of validate code

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/TwoFactorAuth/sequenceValidateCode.puml">
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/TwoFactorAuth/sequenceValidateCode.puml" alt="Sequence" width="70%" height="70%"/>
    </a>
</center>

## HTTP(S) endpoints

* /users/google/2FAuth/qrCode
  * HTTP method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: image/png
  * Examples:

    * Request:

    ```shell
        curl -X POST \
        'http://localhost:8080/users/google/2FAuth/qrCode' \
        --header 'Accept: */*' \
        --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
        --header 'Content-Type: application/x-www-form-urlencoded' \
        --data-urlencode 'email=orion@test.com' \
        --data-urlencode 'password=12345678'
    ```

    * Response:

    ```md
        ![QRCODE](https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcSh-wrQu254qFaRcoYktJ5QmUhmuUedlbeMaQeaozAVD4lh4ICsGdBNubZ8UlMvWjKC)
    ```

* /users/google/2FAuth/validate
  * HTTP method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: image/png
  * Examples:

    * Request:

        ```shell
            curl -X POST \
            'http://localhost:8080/users/google/2FAuth/qrCode' \
            --header 'Accept: */*' \
            --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
            --header 'Content-Type: application/x-www-form-urlencoded' \
            --data-urlencode 'email=orion@test.com' \
            --data-urlencode 'code=123456'
        ```

    * Response:

    ```txt
    eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJ1cG4iOiJyb2RyaWdvQHRlc3RlLmNvbSIsImdyb3VwcyI6WyJ1c2VyIl0sImNfaGFzaCI6Ijc5NjBjMjk1LWQ0NmEtNGI2NC1hNGZiLTE2ZWQxNGYzZTk1NSIsImlhdCI6MTY1NzgzNzY1MCwiZXhwIjoxNjU3ODM3OTUwLCJqdGkiOiIzZjdlOThhMy1hMTAwLTQxOTQtODM0Ny0yMWQwZjRjNDJhYTgifQ.rsHHrOZ5LStCYXREGw0iN7_y7geraKtMYin2OGVchrFF0iX2Stu6m4KGRXVmd3vx_vU3l7RyBN9aFjAO0mm1ScJ-wzP8DQPsuSm1pgw2RBKtTitvi4M7XjsP9bZyuyzP-hWbB6KPhB3oZSzh91nyqqWTQUJrUDsXnuNP3XUX6YAwlXZd5SrxQeNfvcaJ9N2Cj85hw8L5Nm-20P7dt3yj4IZE5QvZ1JYLyNzWZWkYWyr9ffR9v1q83dbxJMaABL8R1sSFZjBTwsQSQOBNSwkCF1U_x2tqj0aZW1w4cqQnpHYAY32AtgmrDHVfdjyQld1g7Qx42C2AoP_ZTWpxZ9vwDg
    ```