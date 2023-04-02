
# Orion users

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=orion-services_users&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=orion-services_users)

Orion users is a small identity service intended for those who want to start a
prototype or project without the need to implement basic features like
managing and authenticating users.

Unlike feature-rich identity services like [keycloak](https://www.keycloak.org),
Orion Users is intended to provide a small and generic set of features that
developers can extends and customize freely.

Orion Users is written in [Java/Quarkus](https://quarkus.io) through [reactive
programming](https://quarkus.io/guides/getting-started-reactive) and prepared to
run with [native compilation](https://quarkus.io/guides/building-native-image),
in other words, it is a code developed to run in cloud services with high
availability, low memory consumption (high density in clusters) and low
throughput.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/users-0.0.1-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- RESTEasy Reactive ([guide](https://quarkus.io/guides/resteasy-reactive)): A JAX-RS implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
