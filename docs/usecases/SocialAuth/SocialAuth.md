---
layout: default
title: Social Authentication
parent: Use Cases
nav_order: 10
---

## Social Authentication

This use case is responsible for authenticating users via social providers (Google) using OAuth2. The endpoint validates the Google token, creates the user automatically if they don't exist, and returns an `AuthenticationDTO` with user data and a JWT token.

### Normal flow

* A client sends a Google ID token (JWT) or access token.
* The service validates the token and extracts user information (email, name) from it.
* The service searches for the user by email in the system.
* If the user does not exist, the service automatically creates a new user account with:
  * Email marked as validated (already verified by the provider)
  * A random password generated (not used, but required by database schema)
  * Default role "user"
* The service generates a system JWT token and returns an `AuthenticationDTO` containing the user data and token.

### Automatic User Creation

When a user authenticates via social provider for the first time:
- A new user account is automatically created
- The user's email is marked as validated (already verified by the provider)
- A random password is generated (not used, but required by database schema)
- The user receives the default role "user"

### Supported Token Types

The system supports two types of Google tokens:

1. **ID Token (JWT)**: Identity token signed by Google that contains user information directly in the payload. Preferred and more secure method.

2. **Access Token**: OAuth2 token that allows fetching user information through Google's API. Used as fallback when ID token is not available.

### Frontend Authentication Methods

The frontend implements two authentication methods:

- **Google One Tap**: Appears automatically on the page when the user is already logged into Google. Tries to use this method first.
- **OAuth2 Token Client**: Fallback that opens a popup for authentication when One Tap is not available.

## HTTPS endpoints

* /users/login/google
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json

### Request Example

```shell
curl -X POST \
  'http://localhost:8080/users/login/google' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzQ1NiJ9...'
```

### Response Example

When authentication is successful, the response contains the user data and JWT token:

```json
{
  "user": {
    "hash": "53012a1a-b8ec-40f4-a81e-bc8b97ddab75",
    "name": "John Doe",
    "email": "john.doe@gmail.com",
    "emailValid": true,
    "secret2FA": null,
    "using2FA": false
  },
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvcmlvbi11c2VycyIsInVwbiI6ImpvaG4uZG9lQGdtYWlsLmNvbSIsImdyb3VwcyI6WyJ1c2VyIl0sImNfaGFzaCI6IjUzMDEyYTFhLWI4ZWMtNDBmNC1hODFlLWJjOGI5N2RkYWI3NSIsImVtYWlsIjoiam9obi5kb2VAZ21haWwuY29tIiwiaWF0IjoxNzE1Mzk0NzA0LCJleHAiOjE3MTUzOTUwMDQsImp0aSI6ImMzYjZkZmFkLTAyMDAtNDc3YS05MDJmLTU0ZDg5YjdiMTUzYyJ9..."
}
```

**Response Fields:**

- `user` (UserEntity): The authenticated user object containing:
  - `hash`: Unique identifier for the user
  - `name`: User's display name (from Google profile)
  - `email`: User's email address (from Google account)
  - `emailValid`: Always `true` for social authentication (verified by Google)
  - `secret2FA`: The 2FA secret (null if not configured)
  - `using2FA`: Whether 2FA is enabled for this user
- `token` (string): A signed JWT token that can be used for authenticated requests

## Exceptions

RESTful Web Service layer will return:
- HTTP 401 (Unauthorized) if:
  - The token is invalid or expired
  - The token is empty or malformed
  - The token cannot be validated
- HTTP 400 (Bad Request) if:
  - The request is malformed
  - Required parameters are missing

## Configuration

### How to Generate Client ID in Google Cloud Console

#### 1. Create or Select a Project

1. Access [Google Cloud Console](https://console.cloud.google.com/)
2. Log in with your Google account
3. At the top of the page, click the project selector
4. Click **New Project** to create a new project, or select an existing project
5. If creating a new project:
   - **Project name**: Enter a descriptive name (e.g., "Orion Users")
   - Click **Create**

#### 2. Configure OAuth Consent Screen

1. In the side menu, navigate to **APIs & Services** > **OAuth consent screen**
2. Select the user type:
   - **External**: For users outside your organization (recommended for public applications)
   - **Internal**: Only for users in your organization (requires Google Workspace)
3. Fill in the required information:
   - **App name**: Your application name
   - **User support email**: Your support email
   - **Developer contact information**: Your email
4. Click **Save and Continue**
5. In the **Scopes** section, add the necessary scopes:
   - Click **Add or Remove Scopes**
   - Select: `openid`, `.../auth/userinfo.email`, `.../auth/userinfo.profile`
   - Click **Update** and then **Save and Continue**
6. In the **Test users** section (only for apps in test mode):
   - Add emails of users who can test the application
   - Click **Save and Continue**
7. Review the information and click **Back to Dashboard**

**Important**: Apps in test mode can only be used by up to 100 test users. For production, you will need to submit the application for Google verification.

#### 3. Create OAuth Client ID

1. In the side menu, navigate to **APIs & Services** > **Credentials**
2. Click **+ Create Credentials** > **OAuth client ID**
3. If prompted, select **Web application** as the application type
4. Configure the credentials:

   **Name**: 
   - Enter a descriptive name (e.g., "Orion Users Web Client")
   
   **Authorized JavaScript origins**:
   - Add the authorized JavaScript origins:
     - For development: `http://localhost:3000`, `http://localhost:5173`
     - For production: `https://your-domain.com`
   
   **Authorized redirect URIs**:
   - Add the authorized redirect URIs (same values as JavaScript origins)
   - For development: `http://localhost:3000`, `http://localhost:5173`
   - For production: `https://your-domain.com`

5. Click **Create**
6. A popup window will appear with your credentials:
   - **Client ID**: Copy this value (format: `xxxxx.apps.googleusercontent.com`)
   - **Client secret**: Not required for Google Identity Services (can be ignored)

**Important**: Keep the Client ID secure. You will need it to configure the frontend and backend.

### Configure Backend

Add the Google OAuth2 configuration in the `src/main/resources/application.properties` file:

```properties
# Google OAuth2 Client ID
social.auth.google.client-id=seu-client-id.apps.googleusercontent.com
```

**Notes**:
- Replace `seu-client-id.apps.googleusercontent.com` with the Client ID obtained from Google Cloud Console
- The Client ID must be in the format `xxxxx.apps.googleusercontent.com`
- Do not include spaces or extra characters
- Restart the backend server after modifying the file

**Configuration by Environment**:

For different environments, you can use Quarkus profiles:

```properties
# Development
%dev.social.auth.google.client-id=dev-client-id.apps.googleusercontent.com

# Test
%test.social.auth.google.client-id=test-client-id.apps.googleusercontent.com

# Production
%prod.social.auth.google.client-id=prod-client-id.apps.googleusercontent.com
```

### Configure Playground Frontend

#### 1. Create Environment Variables File

Create or edit the `.env` file in the playground directory:

**Location**: `src/main/resources/META-INF/resources/playground/.env`

```env
VITE_GOOGLE_CLIENT_ID=seu-client-id.apps.googleusercontent.com
```

**Important**:
- The `VITE_` prefix is required for Vite to expose the variable to frontend code
- Do not use quotes around the value
- Do not add spaces before or after the `=`
- Replace `seu-client-id.apps.googleusercontent.com` with the Client ID obtained from Google Cloud Console

#### 2. Verify Google Identity Services Script

The Google Identity Services script should be included in the `index.html` file:

**Location**: `src/main/resources/META-INF/resources/playground/index.html`

```html
<script src="https://accounts.google.com/gsi/client" async defer></script>
```

**Checks**:
- The script should be in the `<head>` of the document
- The `async defer` attributes ensure the script does not block page loading

#### 3. Restart Development Server

After adding or modifying environment variables:

1. Stop the development server (if running)
2. Restart the server:
   ```bash
   cd src/main/resources/META-INF/resources/playground
   npm run dev
   ```

#### 4. Test Configuration

1. Access the login page
2. Verify that the "Login with Google" button is visible
3. Click the "Login with Google" button
4. A Google popup should appear for account selection and authentication
5. After authenticating, you should be redirected to the dashboard

## Implementation Details

### Backend Token Validation Process

1. **Normalization**: Removes whitespace from the token

2. **JWT Validation Attempt**:
   - Verifies if the token has JWT format (3 parts separated by dots)
   - Decodes the payload (base64url)
   - Extracts email and name from the payload

3. **Fallback to Access Token**:
   - If not a valid JWT, assumes it's an access token
   - Makes a call to Google API: `GET https://www.googleapis.com/oauth2/v2/userinfo`
   - Extracts email and name from the response

4. **User Creation/Search**:
   - Searches for the user by email
   - If it doesn't exist, creates automatically with validated email and default role

5. **System JWT Generation**:
   - Generates a system JWT
   - Returns AuthenticationDTO with user and token

## Troubleshooting

### Common Problems

| Problem | Solution |
|---------|----------|
| "Login with Google" button doesn't appear | Check script in `index.html` and Client ID in `.env` |
| Google popup doesn't open | Disable popup blocker and verify authorized domains in Google Cloud Console |
| "Invalid Google token" error | Log in again to get a new token |
| 401 error | Verify Client ID and authorized domains |
| CORS error | Check CORS configuration in backend |
| Email not found | Verify requested scopes (`email`) |
| HTTPS required | Configure HTTPS in production (except localhost) |
| Token expired | Log in again |

### Basic Checks

1. **Browser Console** (F12):
   - Check for errors related to Google Identity Services
   - Verify that the `google` object is available: `console.log(typeof google)`

2. **Environment Variables**:
   - Verify that `VITE_GOOGLE_CLIENT_ID` is configured in `.env`
   - Restart server after adding/modifying variables

3. **Google Cloud Console**:
   - Verify that domains are in **Authorized JavaScript origins**
   - Verify that domains are in **Authorized redirect URIs**
   - Development: `http://localhost:3000`, `http://localhost:5173`
   - Production: `https://your-domain.com`

4. **Backend**:
   - Verify that `social.auth.google.client-id` is configured in `application.properties`
   - Check backend logs for validation errors

## Security

### Production Recommendations

The current implementation provides basic validation adequate for development. For production, it is recommended to implement:

1. **JWT Signature Validation**:
   - Download Google's public keys from `https://www.googleapis.com/oauth2/v3/certs`
   - Validate the token signature using the corresponding public key

2. **Expiration Validation**:
   - Check the `exp` (expiration) field of the JWT
   - Reject expired tokens

3. **Issuer Validation**:
   - Verify that the `iss` (issuer) field is `https://accounts.google.com`

4. **Audience Validation**:
   - Verify that the `aud` (audience) field matches the configured Client ID

5. **Rate Limiting**:
   - Implement rate limiting to prevent endpoint abuse

### Best Practices

- **Always use HTTPS in production** - Google requires HTTPS (except localhost)
- **Use environment variables** - Never hardcode credentials in code
- **Monitor authentication attempts** - Log failed attempts to detect abuse
- **Keep dependencies updated** - Security libraries should be up to date
- **Validate all inputs** - Don't trust client data without validation
- **Never expose Client Secrets** - Client secrets should never be in frontend code
