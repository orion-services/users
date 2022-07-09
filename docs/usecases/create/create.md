---
layout: default
title: Create User
parent: Use Cases
nav_order: 1
---

# Create User

## Normal flow

* A client sends a name, e-mail and password
* The service receives and validades the data. The name must be not empty, the e-mail must be unique in the server and the format must be valid and, the password must be bigger than eight characters.
* The server generates an identifier (hash) of the user
* The server encrypt the password
* The server stores the new user
* The server returns to the client the name, e-mail and hash as an object.

## Exception flow

* A client sends an invalid name or e-mail or password.
* The service validade the arguments. The arguments can not be empty/null and the password need to have at least eight characters. In all of these cases the service will throw an exception.

## Sequence of normal flow

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/feature/architecture/docs/usecases/create/sequence.puml">
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/feature/architecture/docs/usecases/create/sequence.puml" alt="Sequence" width="70%" height="70%"/>
    </a>
</center>

# Technical specifications

## Exceptions

In the use case layer, exceptions related with arguments will be IllegalArgumentException. However, in the RESTful Web Service layer will be transformed to Bad Request (HTTP 400).