---
layout: default
title: Update e-mail
parent: Use Cases
nav_order: 7
---

## Normal flow

* A client sends the current e-mail the new e-mail and the access token.
* The service validates the access token and the current e-mail to check
  if the users exists in the service. If the users exists, updates the user's
  e-mail, returns the status of e-mail validation to false and sends a message
  with a code to the user validates the new e-mail and generates a new access
  token to the user.

## HTTPS endpoints

* /users/update/email
  * Method: PUT
  * Consumes: application/x-www-form-urlencoded
  * Produces: text/plain
  * Examples:

    * Example of request:

        ```shell
           curl -X PUT \
                'http://localhost:8080/users/update/email' \
                --header 'Accept: */*' \
                --header 'User-Agent: Thunder Client (https://www.thunderclient.com)' \
                --header 'Authorization: Bearer eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ.UbrbrSkwKUOPm12kdcrbwroXe8cwPRg9tLN0ovxEB89bm9LNPYTj6qATOAHrObG-jDf2CUK1m3C5cTZE5LAx-hFt7YgdjXC4qxx57GvyzNY6Dxg_lp-pM3fEvw3CBQujRD47Lr3KwjPilSrwhAvXv46mw78cPHkw3kuNerdNf00TyIBYFobKezFxqT-jTDAPmzFo4B2jwFDtzOKf61Jq30E8i6H8IIDQ7XohbGdotbqu6RdR5uzoaJqB8ylz1hNXGWaBFD3GrsyCeFX9G6zgs998BoIhceKOksEZYhR7TD9q8SIuZWQTeIcgtuLBNMeQ7PV04bvZAyNCcN45sD7QJw.rwzVRmyTL16f1s9i.JrGJwG-ANTLqKuaEFTk-IEO5sdhthG9Z5-DXcli39X3mJKPn2gYIZyO4yp6VlFVw_gRT7x_YwwnLM0UuTqfpdL7fgSCS20nhC1fJYd0fvZCPLHBtJUDdo7gkswmG6eb4M1QENDkK96biwRGq0sCG5dZPfkDp1PXJQyF63phEPEb9Bo6xVzoJjJl-9C25BQRRSBHrGzp9tzY3Bh2WMv1JGJGG2thhuh2L6ap4QvM1jdyS9ObeNL61al_4UIk8CesZ0a5enheICPaL5YfbMHpwCWd3PutmABr-o05jtw.7dE9ieDGe3qOooGWmR-jJg' \
                --header 'Content-Type: application/x-www-form-urlencoded' \
                --data-urlencode 'email=orion@test.com' \
                --data-urlencode 'newEmail=orion@xyzmail.com'
        ```

    * Example of response: A new signed JWT.

        ```txt
       eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkEyNTZHQ00ifQ.k3VZmBKoYebrmPQcV5vVrNG1d1s-Ee4Szjh--iUwHClWzOLZfHWBRNHAcp70IS7VZM6JcAtVqXmLHP9quaR3OxSpUAAcgxnG-zIt6ogkd9vxiCttgwNGAqnd4pWUZ9ie4AWi9S-subt5KXDQ41kEuMLMJ2ufHLc4yU7XmKm5rkEWwXTjmmJCfb-soreb1bUpZ-SfoQ3zVX9MWoHYInnjzyZYLUfQIq0JfZZhKx4v689aE27nCek5iol-42LsQzowOTa9kvzxbN9ZofP_mVSuuXNJk7lTTZqX8ZU-BlwA27_W0t0sDj3Ka8H2GYyqAIBbUcWc_MdeHDnUQWeAMF57Aw.LPYiVFh9FxVW2D57.JwHCxJsICElkF85gTBpgX1fOirjFohzWGFeozzfjuyrrC_PJJhzHIR1tsZ6lfQi7jrjHeCT-aRjOW2r-U-baEbkguEzCYyG68ynFjjU65kajeoKSgoI4SVgdByK_bnHGhv-CTUzv4d4gD0Jt0OYw9H9a5QvozA9r_RiRdF-WwEYoyYSlvIxzxx3hlL07tbYO6z_dcEcd_-Y3ylKooRSXsoG_FSd6IzuJqlD10Ixax1uL-bmap2rUEqMjpcnIcMiyL9nF_-PhAjC7FnhCWJUtkj9NGzxPxZqiak-Wc8c2SdXf0vRKaiL72MkIxRo.1IQPzuVpukQwyqBA9S0rZA
        ```

## Exceptions

In the use case layer, exceptions related with arguments will be
IllegalArgumentException. However, in the RESTful Web Service layer will be
transformed to Bad Request (HTTP 400).
