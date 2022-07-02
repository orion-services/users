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

## Sequence

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/feature/architecture/docs/usecases/create/sequence.puml">
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/feature/architecture/docs/usecases/create/sequence.puml" alt="Sequence" width="50%" height="50%"/>
    </a>
</center>