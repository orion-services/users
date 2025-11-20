---
layout: default
title: Update password
parent: Use Cases
nav_order: 8
---

## Normal flow

* A client sends user's e-mail, the current and the new password along with an access token.
* The service validates the access token and the current e-mail to check
  if the user exists in the service. If the user exists, validates the current password
  and if it matches, updates the user's password and returns a User in JSON.

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
            --header 'Authorization: Bearer eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ.UbrbrSkwKUOPm12kdcrbwroXe8cwPRg9tLN0ovxEB89bm9LNPYTj6qATOAHrObG-jDf2CUK1m3C5cTZE5LAx-hFt7YgdjXC4qxx57GvyzNY6Dxg_lp-pM3fEvw3CBQujRD47Lr3KwjPilSrwhAvXv46mw78cPHkw3kuNerdNf00TyIBYFobKezFxqT-jTDAPmzFo4B2jwFDtzOKf61Jq30E8i6H8IIDQ7XohbGdotbqu6RdR5uzoaJqB8ylz1hNXGWaBFD3GrsyCeFX9G6zgs998BoIhceKOksEZYhR7TD9q8SIuZWQTeIcgtuLBNMeQ7PV04bvZAyNCcN45sD7QJw.rwzVRmyTL16f1s9i.JrGJwG-ANTLqKuaEFTk-IEO5sdhthG9Z5-DXcli39X3mJKPn2gYIZyO4yp6VlFVw_gRT7x_YwwnLM0UuTqfpdL7fgSCS20nhC1fJYd0fvZCPLHBtJUDdo7gkswmG6eb4M1QENDkK96biwRGq0sCG5dZPfkDp1PXJQyF63phEPEb9Bo6xVzoJjJl-9C25BQRRSBHrGzp9tzY3Bh2WMv1JGJGG2thhuh2L6ap4QvM1jdyS9ObeNL61al_4UIk8CesZ0a5enheICPaL5YfbMHpwCWd3PutmABr-o05jtw.7dE9ieDGe3qOooGWmR-jJg' \
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
transformed to Bad Request (HTTP 400). If the access token is invalid or missing,
the service will return Unauthorized (HTTP 401).
