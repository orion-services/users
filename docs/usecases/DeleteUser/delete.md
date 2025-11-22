---
layout: default
title: Delete user
parent: Use Cases
nav_order: 5
---

## Normal flow

* A client sends a e-mail.
* The service validates the input data and verifies if the users exists in the
  system.
* If the users exists, delete the user.

## HTTPS endpoints

* /users/delete
  * Method: DELETE
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json
  * Examples:

    * Request:

        ```shell
            curl -X DELETE \
                'http://localhost:8080/users/delete' \
                --header 'Accept: */*' \
                --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
                --header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvcmlvbi11c2VycyIsInVwbi
                I6InJvZHJpZ28ucHJlc3Rlc0BnbWFpbC5jb20iLCJncm91cHMiOlsidXNlciJdLCJjX2hhc2giOiJmMjc5NjdlMy1lOTQ5LTQzZDctO
                GVlZi1lNWRlOTM3MjhhMGEiLCJlbWFpbCI6InJvZHJpZ28ucHJlc3Rlc0BnbWFpbC5jb20iLCJpYXQiOjE2ODAyMTEzODQsImV4cCI6M
                TY4MDIxMTY4NCwianRpIjoiMDg5ZjlmNjEtNThjMi00OGU2LWE3Y2MtMDU3MDJiMDhkMTM0In0.n5hsgY7xlsk3sYLgu628Z6sPGeJhx
                roGd_v-cQtSDvUVGBkZOODD9t_19ZOuTAEV5IcrO02HQBQR8fi5-94BejAh4rdBVNsWIyvtWMi2x3nvbnrWzkbYPv9WPrq4aiGcQIrLA
                Vz17cFZ-oN7gm8-7JAJ8pXseT6PpnzbpL4cIRvUHHeU2pc7zUubLb5S6lO0ly_bCINYW5E87S6JRe33nH6S2u9gdjFctQVNWp4b-EgKx
                8U9IDsv9a3NC2fWJzbMcOpmq6eGbdFalEf6nbxoLT2yzFKKHWrekXgiOrykI_R2zgnII5Kcezq5mEwU4qf_tPxYXCf0W0YLePJxeij3
                QA' \
                --header 'Content-Type: application/x-www-form-urlencoded' \
                --data-urlencode 'email=orion@test.com'
        ```

    * Response will be an HTTP 204 (Undocumented)

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
