
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

For comprehensive instructions on running the service in development, production with containers (JVM and native compilation), configuration, and troubleshooting, see the [Running the Service Documentation](docs/running/Running.md).

## Playground

The project includes a Vue 3 playground application that provides a user interface for testing and experimenting with all features of the Orion Users service.

**Access the playground**: After starting the application, navigate to `http://localhost:8080/test`

The playground includes:
- User registration and login
- Social authentication (Google)
- Two-factor authentication (2FA)
- WebAuthn (biometric/security key authentication)
- Password recovery
- User profile management

For detailed information about the playground, including how to run it in development and production modes, social login configuration, and user guide, see the [Playground Documentation](docs/playground/Playground.md).

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it's not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

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

You can then execute your native executable with: `./target/users-0.0.6-runner`

For detailed instructions on building and running containers (JVM and native), see the [Running the Service Documentation](docs/running/Running.md).

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## API Endpoints

The service provides the following main endpoints:

- `POST /users/create` - Create a new user
- `POST /users/login` - Authenticate a user (returns LoginResponseDTO)
- `PUT /users/update` - Update user information (email and/or password). Requires JWT authentication. Returns LoginResponseDTO with updated token and user.
- `POST /users/delete` - Delete a user (admin only)
- `GET /users/validateEmail` - Validate user email with code
- `POST /users/google/2FAuth/qrCode` - Generate 2FA QR code
- `POST /users/google/2FAuth/validate` - Validate 2FA code
- `POST /users/login/2fa` - Login with 2FA code
- `POST /users/login/google` - Social authentication with Google

For complete API documentation, see the [documentation site](https://users.orion-services.dev).

## Update User Endpoint

The `/users/update` endpoint allows updating user email and/or password in a single request:

- **Method**: PUT
- **Authentication**: Required (JWT token in Authorization header)
- **Parameters**:
  - `email` (required): Current user email
  - `newEmail` (optional): New email address
  - `password` (optional): Current password (required if updating password)
  - `newPassword` (optional): New password
- **Response**: LoginResponseDTO containing AuthenticationDTO with new JWT token and updated user information
- **Note**: At least one of `newEmail` or `newPassword` must be provided

Example:
```bash
curl -X PUT 'http://localhost:8080/users/update' \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=user@example.com' \
  --data-urlencode 'newEmail=newuser@example.com'
```

## Related Guides

- RESTEasy Reactive ([guide](https://quarkus.io/guides/resteasy-reactive)): A JAX-RS implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
