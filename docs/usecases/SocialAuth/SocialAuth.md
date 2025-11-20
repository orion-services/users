---
layout: default
title: Social Authentication
parent: Use Cases
nav_order: 10
---

## Social Authentication

This use case is responsible for authenticating users via social providers (Google and Apple) using OAuth2.

### Normal flow

* A user clicks "Login with Google" or "Login with Apple" in the frontend
* The frontend initiates OAuth2 flow with the selected provider
* The provider authenticates the user and returns an ID token (JWT)
* The frontend sends the ID token to the backend
* The backend validates the token and extracts user information (email, name)
* The backend searches for the user by email
* If the user doesn't exist, the backend creates it automatically
* The backend generates a JWT token for the system
* The backend returns AuthenticationDTO with user and token

### Automatic User Creation

When a user authenticates via a social provider for the first time:
* A new user account is automatically created
* The user's email is marked as validated (since it's verified by the provider)
* A random password is generated (not used, but required by the database schema)
* The user is assigned the default "user" role

## HTTPS endpoints

### Google Login

* `/users/login/google`
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json

* Request:

```shell
curl -X POST \
  'http://localhost:8080/users/login/google' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=GOOGLE_ID_TOKEN'
```

* Response:

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
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9..."
}
```

### Apple Login

* `/users/login/apple`
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json

* Request:

```shell
curl -X POST \
  'http://localhost:8080/users/login/apple' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=APPLE_ID_TOKEN'
```

* Response:

```json
{
  "user": {
    "hash": "53012a1a-b8ec-40f4-a81e-bc8b97ddab75",
    "name": "Jane Doe",
    "email": "jane.doe@icloud.com",
    "emailValid": true,
    "secret2FA": null,
    "using2FA": false
  },
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9..."
}
```

## Frontend Integration

### Google Sign-In

The frontend uses Google Identity Services to handle OAuth2 flow:

1. Load the Google Identity Services script
2. Initialize the OAuth2 client with your Google Client ID
3. Request access token with scopes: `openid profile email`
4. Send the ID token to the backend endpoint `/users/login/google`

### Apple Sign-In

The frontend uses Apple's Sign In JavaScript API:

1. Load the Apple Sign In script
2. Initialize with your Apple Client ID
3. Request sign-in with scopes: `name email`
4. Send the ID token to the backend endpoint `/users/login/apple`

## Configuration

### Google OAuth2 Configuration

#### 1. Create Credentials in Google Cloud Console

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Navigate to **APIs & Services** > **Credentials**
4. Click **Create Credentials** > **OAuth client ID**
5. If prompted, configure the OAuth consent screen:
   - Choose **External** (for testing) or **Internal** (for organization)
   - Fill in the required information
   - Add the scopes: `openid`, `profile`, `email`
6. Configure the application type:
   - **Application type**: Web application
   - **Name**: Your application name
   - **Authorized JavaScript origins**: 
     - `http://localhost:5173` (development)
     - `https://your-domain.com` (production)
   - **Authorized redirect URIs**:
     - `http://localhost:5173` (development)
     - `https://your-domain.com` (production)
7. Click **Create**
8. Copy the generated **Client ID**

#### 2. Configure Backend

Add to the file `src/main/resources/application.properties`:

```properties
# Google OAuth2 Client ID
social.auth.google.client-id=YOUR_GOOGLE_CLIENT_ID_HERE
```

#### 3. Configure Frontend

Create or edit the `.env` file in the frontend project root:

```env
VITE_GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID_HERE
```

**Example:**
```env
VITE_GOOGLE_CLIENT_ID=307391126869-5c1f7q3vl6hdqv1elvq4humtrc8tvfef.apps.googleusercontent.com
```

#### 4. Verify Configuration

- The Google Identity Services script is already included in `index.html`
- The "Login with Google" button should appear on the login screen
- When clicked, the Google popup should open for authentication

### Apple Sign-In Configuration

#### 1. Create Services ID in Apple Developer Portal

1. Go to [Apple Developer Portal](https://developer.apple.com/)
2. Navigate to **Certificates, Identifiers & Profiles**
3. Click **Identifiers** > **+** (add new)
4. Select **Services IDs** and click **Continue**
5. Fill in:
   - **Description**: Descriptive name (e.g., "Orion Users Login")
   - **Identifier**: A unique identifier (e.g., `com.yourcompany.orionusers`)
6. Check **Sign In with Apple** and click **Configure**
7. Configure Sign In with Apple:
   - **Primary App ID**: Select your primary App ID
   - **Website URLs**:
     - **Domains and Subdomains**: `localhost` (dev) and your domain (prod)
     - **Return URLs**: 
       - `http://localhost:5173` (development)
       - `https://your-domain.com` (production)
8. Click **Save** and then **Continue** > **Register**

#### 2. Create Key for Sign In with Apple

1. In Apple Developer Portal, go to **Keys**
2. Click **+** (add new key)
3. Fill in:
   - **Key Name**: Descriptive name (e.g., "Orion Users Sign In Key")
   - Check **Sign In with Apple**
4. Click **Configure** next to "Sign In with Apple"
5. Select the **Primary App ID** created previously
6. Click **Save** > **Continue** > **Register**
7. **IMPORTANT**: Download the key (.p8) - it can only be downloaded once
8. Note the generated **Key ID**

#### 3. Get Team ID

1. In Apple Developer Portal, go to **Membership**
2. Copy the **Team ID** (format: `ABC123DEF4`)

#### 4. Configure Backend

Add to the file `src/main/resources/application.properties`:

```properties
# Apple OAuth2 Configuration
social.auth.apple.client-id=com.yourcompany.orionusers
social.auth.apple.team-id=ABC123DEF4
social.auth.apple.key-id=XYZ789ABC1
```

**Where:**
- `client-id`: The Services ID created in step 1
- `team-id`: The Team ID obtained in step 3
- `key-id`: The Key ID obtained in step 2

#### 5. Configure Frontend

Create or edit the `.env` file in the frontend project root:

```env
VITE_APPLE_CLIENT_ID=com.yourcompany.orionusers
```

**Example:**
```env
VITE_APPLE_CLIENT_ID=com.example.orionusers
```

#### 6. Verify Configuration

- The Apple Sign In script is already included in `index.html`
- The "Login with Apple" button should appear on the login screen
- When clicked, the Apple popup should open for authentication

### Environment Variables

#### Development

Create a `.env` file in the frontend project root:

```env
VITE_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
VITE_APPLE_CLIENT_ID=com.yourcompany.orionusers
```

#### Production

Configure environment variables on your server/hosting:

- **Vercel/Netlify**: Configure in the project's environment variables
- **Docker**: Add to `docker-compose.yml` or `.env`
- **Traditional server**: Configure on the web server (nginx/apache)

### Troubleshooting

#### Google Sign-In not working

- Verify that the Client ID is correct in `.env`
- Confirm that domains are authorized in Google Cloud Console
- Check the browser console for errors
- Make sure you're using HTTPS in production (or localhost in development)

#### Apple Sign-In not working

- Verify that the Services ID is correct
- Confirm that Sign In with Apple is enabled in the Services ID
- Check that Return URLs are configured correctly
- Make sure you're using HTTPS in production (Apple requires HTTPS)
- Verify that Team ID and Key ID are correct

#### Error "clientId should be a string"

- Verify that the `VITE_APPLE_CLIENT_ID` variable is defined in `.env`
- Restart the development server after adding environment variables
- Make sure there are no extra spaces in the variable value

## Token Validation

The backend validates social provider tokens by:
1. Decoding the JWT token
2. Extracting user information (email, name) from the token payload
3. Validating token format and structure

**Note**: In production, you should implement full token signature validation using the provider's public keys. The current implementation provides basic validation for development purposes.

## Exceptions

* HTTP 401 (Unauthorized): If the token is invalid or expired
* HTTP 400 (Bad Request): If the request is malformed or missing required parameters

## Security Considerations

* Always validate token signatures in production
* Verify token expiration
* Check token issuer matches the expected provider
* Use HTTPS for all OAuth2 flows
* Store client secrets securely (never in client-side code)

