---
layout: default
title: Create and authenticate
parent: Use Cases
nav_order: 2
---

## Create and Authenticate

### Normal flow

* A client sends an name, e-mail and password.
* The service receives and validates the data. The name must be not empty,
  the e-mail must be unique and the format must be valid and,
  the password must be bigger than eight characters.
* The service encrypts the password and generates an identifier (hash) for the
  new user.
* The service creates the new user in the data repository.
* The service returns some user's data.

#### Alternative flow

* If the user already exists, the service just return a a JSON with the user
  and a signed JWT.

## HTTP(S) endpoints

* /api/users/createAuthenticate
  * HTTP method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json
  * Examples:

  * Example of request:

    ```shell
       curl -X 'POST' \
            'http://localhost:8080/api/users/createAuthenticate' \
            -H 'accept: application/json' \
            -H 'Content-Type: application/x-www-form-urlencoded' \
            -d 'name=Orion&email=orion%40test.com&password=12345678'
    ```

  * Example of response: User in JSON and signed Token.

    ```json
       {
            "user": {
                "hash": "7eba8ef2-426b-446a-9f05-4ab67e71383d",
                "name": "Orion",
                "email": "orion@test.com",
                "emailValid": false
            },
            "token": "eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ.UOLgr6fR0xoNj8gcLeQ1HssaCoPRvXRptzPoVMbd5VpTe-_OEy_BA04dRHRcY-jID4TEVUfSmWINhs5iLLtbp6SYZcqKH0vuFFiQ491UsjVzpy9QDGoWxJLeO4XytJnjnjVSPJ3G9mhANhWr2ylgh0Wnv3wQkFdEobSd9ysrnkKq1bF5OBP3olJyogfDtXGRul150ICYbS3KrZ5OBBMmqgah6vW0I1IO8Kz4uJ9LmfTbZbtHoVJqHwMY9ypVMF_MRKaTJ1lisZOE6F21cOjwcnBGGddQlw5jOstS_sZmixyxvE19GnhjmHlWHoXfwGgZ_TY_oeE1aBUcXi_fYifxWg.qp0YEBMzxjRBALxE.8YmjHAuyWbGbH6pqi4xJgqJ3Gu9kA9kYkwHCdqkczXBdn7YGRAE_78yQOyZMhmRX1X0yWv-R0i___Yv9BXNasbr44I_vvoL7VDPCxm2ln3lSnQwKdOA7xkJMUtyJDjXlnT0vw2LkNS1GvfkkaBXx_x5h8jANXWV5ne1PLr307XQQquPNd8If4rLgiEwjdYyK4Lhz3NffIOl380mRAmZCDH_zLJBVTmFvL0F6rcfUcd5tdhfe28DALr3rPMGahbr5KT9d0So9OoUhIU7XdSA_nkIh4GFx_A.Xqa0vqD_bM2HGN1aTR2QpQ"
        }
    ```

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
