
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

## Admin Console

The project includes a Vue 3 admin console application built with Vuetify that provides an administrative interface for managing users in the Orion Users service.

**Access the admin console**: After starting the application, navigate to `http://localhost:8080/console`

**Authentication**: The admin console requires authentication with a JWT token that includes the `admin` role. Only users with admin privileges can access this interface.

The admin console includes:
- User authentication with admin role verification
- User listing with filters and search functionality
- User detail view with complete user information
- User creation (create new users)
- User editing (update email and password)
- User deletion (delete users with confirmation)

For detailed information about the admin console, including development setup and features, see the [Admin Console README](src/main/resources/META-INF/resources/admin/README.md).

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

The service provides the following endpoints:

### User Management

- `POST /users/create` - Create a new user. Returns user data in JSON format.
- `POST /users/createAuthenticate` - Create a new user and authenticate in a single request. Returns LoginResponseDTO with JWT token.
- `PUT /users/update` - Update user information (name, email and/or password). Requires JWT authentication (role: user). Returns LoginResponseDTO with updated token and user. Admins can update any user.
- `POST /users/delete` - Delete a user. Requires JWT authentication (role: admin).
- `GET /users/list` - List all users. Requires JWT authentication (role: admin).
- `GET /users/by-email` - Get user by email. Requires JWT authentication (role: admin).

### Authentication

- `POST /users/login` - Authenticate a user with email and password. Returns LoginResponseDTO. If user has 2FA enabled and `require2FAForBasicLogin` is true, returns `requires2FA: true` indicating that 2FA code is required.
- `POST /users/login/2fa` - Authenticate with 2FA code after initial login. Returns LoginResponseDTO with JWT token.
- `POST /users/authenticate` - **(Deprecated)** Authenticate a user. Returns JWT token as plain text. Use `/users/login` instead.

### Email Validation

- `GET /users/validateEmail` - Validate user email with validation code sent via email. Query parameters: `email` and `code`.

### Password Recovery

- `POST /users/recoverPassword` - Recover user password. Generates a new password and sends it via email. Returns HTTP 204 (No Content).

### Social Authentication

- `POST /users/login/google` - Authenticate with Google OAuth2. Accepts Google ID token or access token. Returns LoginResponseDTO. If user has 2FA enabled and `require2FAForSocialLogin` is true, returns `requires2FA: true`.
- `POST /users/login/google/2fa` - Complete social authentication with 2FA code. Returns LoginResponseDTO with JWT token.

### Two-Factor Authentication (2FA)

- `POST /users/google/2FAuth/qrCode` - Generate QR code for 2FA setup. Requires email and password. Returns PNG image.
- `POST /users/google/2FAuth/validate` - Validate 2FA TOTP code (standalone validation). Returns LoginResponseDTO with JWT token.
- `POST /users/2fa/settings` - Update 2FA settings for a user. Requires JWT authentication (role: user). Parameters: `email`, `require2FAForBasicLogin` (optional), `require2FAForSocialLogin` (optional).

### WebAuthn (Biometric/Security Key Authentication)

- `POST /users/webauthn/register/start` - Start WebAuthn registration process. Returns PublicKeyCredentialCreationOptions as JSON.
- `POST /users/webauthn/register/finish` - Finish WebAuthn registration. Saves credential for the user.
- `POST /users/webauthn/authenticate/start` - Start WebAuthn authentication process. Returns PublicKeyCredentialRequestOptions as JSON.
- `POST /users/webauthn/authenticate/finish` - Finish WebAuthn authentication. Returns LoginResponseDTO with JWT token.

For complete API documentation, see the [documentation site](https://users.orion-services.dev).

## Update User Endpoint

The `/users/update` endpoint allows updating user name, email and/or password in a single request:

- **Method**: PUT
- **Authentication**: Required (JWT token in Authorization header with role: user)
- **Parameters**:
  - `email` (required): Current user email
  - `name` (optional): New name
  - `newEmail` (optional): New email address
  - `password` (optional): Current password (required if updating password)
  - `newPassword` (optional): New password
- **Response**: LoginResponseDTO containing AuthenticationDTO with new JWT token and updated user information
- **Note**: At least one of `name`, `newEmail` or `newPassword` must be provided. Admins can update any user; regular users can only update their own account.

Example:
```bash
curl -X PUT 'http://localhost:8080/users/update' \
  --header 'Authorization: Bearer <token>' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=user@example.com' \
  --data-urlencode 'name=New Name' \
  --data-urlencode 'newEmail=newuser@example.com'
```

## Related Guides

- RESTEasy Reactive ([guide](https://quarkus.io/guides/resteasy-reactive)): A JAX-RS implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
