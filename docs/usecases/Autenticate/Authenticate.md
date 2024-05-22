---
layout: default
title: Authenticate
parent: Use Cases
nav_order: 1
---

## Authenticate

This use case is responsible for authenticate a user in the system.

### Normal flow

* A client sends a e-mail and password.
* The service validates the input data and verifies if the users exists in the
  system. If the users exists, the service returns a JSON with the user data
  and a signed JWT.

## HTTPS endpoints

* /users/login
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json

* Request:

```shell
curl  -X POST \
  'http://localhost:8080/users/login' \
  --header 'Accept: */*' \
  --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@services.dev' \
  --data-urlencode 'password=12345678'
```

* Response:

```json
{
"user": {
  "hash": "53012a1a-b8ec-40f4-a81e-bc8b97ddab75",
  "name": "Orion",
  "email": "orion@services.dev",
  "emailValid": false,
  "secret2FA": null,
  "using2FA": false
},
"token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvcmlvbi11c2VycyIsInVwbiI6Im9yaW9uQHNlcnZpY2VzLmRldiIsImdyb3VwcyI6WyJ1c2VyIl0sImNfaGFzaCI6IjUzMDEyYTFhLWI4ZWMtNDBmNC1hODFlLWJjOGI5N2RkYWI3NSIsImVtYWlsIjoib3Jpb25Ac2VydmljZXMuZGV2IiwiaWF0IjoxNzE1Mzk0NzA0LCJleHAiOjE3MTUzOTUwMDQsImp0aSI6ImMzYjZkZmFkLTAyMDAtNDc3YS05MDJmLTU0ZDg5YjdiMTUzYyJ9.I93SpcxIm31wfMQeiFLuUuuWuwlG-C0aGascSEDseRueILn9Tf5shEyNDMLQr6QRNhQbNjRjnCwe_quenVfjBEF_BLgtDDq7maoqpzDdrnDoKxtxex0dIXmRg2ABZoktB-jBo8yJcflandp1FUe7hG1VduE2E8D6WqvUQiNrhhCiiEZ4d5Moc1H11S3YGg3X1U-QnWUGx70FYQG4Qo-1Ini7T6miC0xCxSJRxumXKKtBRLYMDizp5qPIVoVIatJUu4WgoVZWliStmE7wBu6X_La7z4rAddgIlGRiqLZPkaSruzO2PP3i_T1Ezupcw9ol6LP_nlPaOQHeAjJ7aSQMyA"
}
```

## Exceptions

RESTful Web Service layer will return a HTTP 401 (Unauthorized) if the user
does not exist or the password is incorrect. If the request is invalid, for
example, without the required parameters, the service will return a HTTP 400
(Bad Request).