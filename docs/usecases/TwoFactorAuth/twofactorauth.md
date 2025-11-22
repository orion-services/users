---
layout: default
title: Two Factor Authentication
parent: Use Cases
nav_order: 9
---

## Two Factor Authentication

This use case is responsible for two-factor authentication (2FA) using TOTP (Time-based One-Time Password) codes. The system supports generating QR codes for authenticator app setup and validating TOTP codes for authentication.

### Sequence Diagrams

#### Generate QR Code Flow

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/TwoFactorAuth/sequenceGenerateQrCode.puml">
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/TwoFactorAuth/sequenceGenerateQrCode.puml" alt="Sequence" width="70%" height="70%"/>
    </a>
</center>

#### Validate Code Flow

<center>
    <a href="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/TwoFactorAuth/sequenceValidateCode.puml">
    <img src="http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/orion-services/users/dev/docs/usecases/TwoFactorAuth/sequenceValidateCode.puml" alt="Sequence" width="70%" height="70%"/>
    </a>
</center>

### Normal flow - Generate QR Code

* A client sends an e-mail and password.
* The service validates the input data and verifies if the user exists in the system.
* The service authenticates the user with the provided credentials.
* If the user exists and credentials are valid, the service:
  * Generates a secret key for 2FA
  * Sets `using2FA` to `true` and stores the secret key
  * Updates the user in the repository
  * Generates a QR code containing the secret key in Google Authenticator format
* The service returns the QR code as a PNG image that can be scanned by authenticator apps (Google Authenticator, Microsoft Authenticator, Authy, etc.).

### Normal flow - Validate Code

* A client sends an e-mail and TOTP code (typically 6 digits from an authenticator app).
* The service validates the input data and verifies if the user exists in the system.
* The service checks if 2FA is enabled for the user.
* If the user exists and 2FA is enabled, the service:
  * Retrieves the user's 2FA secret key
  * Generates the expected TOTP code using the secret key
  * Compares the provided code with the expected code
* If the codes match, the service generates a JWT token and returns a `LoginResponseDTO` containing the user data and token.

### Normal flow - Login with 2FA

When a user has 2FA enabled and `require2FAForBasicLogin` is set to `true`:

* The user attempts to login via `/users/login` with email and password.
* The service validates credentials and checks if 2FA is required.
* If 2FA is required, the service returns a `LoginResponseDTO` with `requires2FA: true` and `authentication: null`.
* The client prompts the user for their 2FA code.
* The client sends the email and 2FA code to `/users/login/2fa`.
* The service validates the code and returns a `LoginResponseDTO` with `authentication` containing the user data and JWT token.

## HTTPS endpoints

### Generate QR Code

* /users/google/2FAuth/qrCode
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: image/png

### Validate Code (Setup/Standalone)

* /users/google/2FAuth/validate
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json

### Login with 2FA Code

* /users/login/2fa
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json

### Request Examples

#### Example 1: Generate QR Code

```shell
curl -X POST \
  'http://localhost:8080/users/google/2FAuth/qrCode' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com' \
  --data-urlencode 'password=12345678' \
  --output qrcode.png
```

#### Example 2: Validate Code (Standalone)

```shell
curl -X POST \
  'http://localhost:8080/users/google/2FAuth/validate' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com' \
  --data-urlencode 'code=123456'
```

#### Example 3: Login with 2FA Code

```shell
curl -X POST \
  'http://localhost:8080/users/login/2fa' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com' \
  --data-urlencode 'code=123456'
```

### Response Examples

#### Example 1: QR Code Response

When the QR code is successfully generated, the response is a PNG image:

```
[Binary PNG image data]
```

The QR code can be scanned with any TOTP-compatible authenticator app.

#### Example 2: Validate Code Response

When the code is successfully validated, the response contains a `LoginResponseDTO`:

```json
{
  "authentication": {
    "user": {
      "hash": "53012a1a-b8ec-40f4-a81e-bc8b97ddab75",
      "name": "Orion",
      "email": "orion@test.com",
      "emailValid": false,
      "secret2FA": "JBSWY3DPEHPK3PXP",
      "using2FA": true
    },
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvcmlvbi11c2VycyIsInVwbiI6Im9yaW9uQHRlc3QuY29tIiwiZ3JvdXBzIjpbInVzZXIiXSwiY19oYXNoIjoiNTMwMTJhMWEtYjhlYy00MGY0LWE4MWUtYmM4Yjk3ZGRhYjc1IiwiZW1haWwiOiJvcmlvblRlc3QuY29tIiwiaWF0IjoxNzE1Mzk0NzA0LCJleHAiOjE3MTUzOTUwMDQsImp0aSI6ImMzYjZkZmFkLTAyMDAtNDc3YS05MDJmLTU0ZDg5YjdiMTUzYyJ9..."
  },
  "requires2FA": false,
  "message": null
}
```

**Response Fields:**

- `authentication` (AuthenticationDTO): Contains the user data and JWT token
- `user` (UserEntity): The authenticated user object
  - `secret2FA`: The 2FA secret key (stored for future validations)
  - `using2FA`: Always `true` when 2FA is enabled
- `token` (string): A signed JWT token for authenticated requests
- `requires2FA` (boolean): Always `false` after successful 2FA validation
- `message` (string | null): Typically `null` for successful validation

## Exceptions

RESTful Web Service layer will return:
- HTTP 401 (Unauthorized) if:
  - The user does not exist
  - The password is incorrect (for QR code generation)
  - The TOTP code is invalid or incorrect
  - 2FA is not enabled for the user
  - The 2FA secret is not found
- HTTP 400 (Bad Request) if:
  - The email format is invalid
  - The email parameter is missing
  - The password parameter is missing (for QR code generation)
  - The code parameter is missing (for validation)
  - The code format is invalid

In the use case layer, exceptions related with arguments will be `IllegalArgumentException`. However, in the RESTful Web Service layer these will be transformed to Bad Request (HTTP 400) or Unauthorized (HTTP 401) as appropriate.

## Using 2FA with Login

When a user has 2FA enabled and `require2FAForBasicLogin` is set to `true`:

1. **Initial Login**: User attempts login via `/users/login` with email and password
2. **2FA Required**: Service returns `LoginResponseDTO` with `requires2FA: true`
3. **Code Entry**: Client prompts user for 2FA code from authenticator app
4. **2FA Validation**: Client sends email and code to `/users/login/2fa`
5. **Success**: Service validates code and returns `LoginResponseDTO` with JWT token

**Example Flow:**

```shell
# Step 1: Initial login
curl -X POST \
  'http://localhost:8080/users/login' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com' \
  --data-urlencode 'password=12345678'

# Response indicates 2FA is required:
# {
#   "authentication": null,
#   "requires2FA": true,
#   "message": "2FA code required"
# }

# Step 2: Validate 2FA code
curl -X POST \
  'http://localhost:8080/users/login/2fa' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=orion@test.com' \
  --data-urlencode 'code=123456'

# Response contains JWT token:
# {
#   "authentication": {
#     "user": {...},
#     "token": "eyJ..."
#   },
#   "requires2FA": false,
#   "message": null
# }
```

## Supported Authenticator Apps

The generated QR codes are compatible with any TOTP-compatible authenticator app, including:

- Google Authenticator
- Microsoft Authenticator
- Authy
- 1Password
- LastPass Authenticator
- Any other TOTP-compatible app
