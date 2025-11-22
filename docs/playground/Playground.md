---
layout: default
title: Playground Documentation
nav_order: 3
---

# Playground Documentation

The Playground is a Vue 3 application built with Vuetify that provides a user interface for testing and experimenting with all features of the Orion Users service. It is located in the `playground` directory and is served by the Quarkus backend at the `/test` URL path.

## Running the Playground

### Development Mode

For development with hot module replacement:

1. **Start the Quarkus backend**:
```bash
./mvnw compile quarkus:dev
```

2. **In a separate terminal, start the Vite dev server**:
```bash
cd src/main/resources/META-INF/resources/playground
npm install  # Only needed the first time
npm run dev
```

3. **Access the application**:
   - Development server: `http://localhost:3000/test`
   - The Vite dev server proxies API requests to the Quarkus backend

**Note**: In development mode, Vite runs on port 3000 with hot module replacement. The application is configured to use `/test` as the base path, and API requests to `/users` are automatically proxied to the Quarkus backend running on port 8080.

### Production Mode

To run the playground in production mode:

1. **Navigate to the playground directory**:
```bash
cd src/main/resources/META-INF/resources/playground
```

2. **Build the application**:
```bash
npm run build
```

The built files will be generated in `src/main/resources/META-INF/resources/test/` directory, which is automatically served by Quarkus.

3. **Start or restart the Quarkus backend**:
```bash
./mvnw compile quarkus:dev
```

4. **Access the application at**:
```
http://localhost:8080/test
```

**Important**: The application is configured to be served at the `/test` URL path. Make sure to access it at `http://localhost:8080/test` (not at the root `/`).

**Note**: After making changes in development mode, rebuild the application (`npm run build`) for the changes to be available when accessing via the Quarkus backend at `/test`.

## Social Login Configuration

### Environment Variables

Create a `.env` file in the `playground/` directory root:

**Location**: `src/main/resources/META-INF/resources/playground/.env`

```env
# Backend API URL
VITE_API_URL=http://localhost:8080

# Google OAuth2 Client ID (required for social login)
VITE_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

**Important**:
- The `VITE_` prefix is required for Vite to expose the variable to frontend code
- Do not use quotes around the value
- Do not add spaces before or after the `=`
- Replace `your-google-client-id.apps.googleusercontent.com` with the Client ID obtained from Google Cloud Console

### Google Client ID Setup

To configure Google OAuth2 for social login:

1. **Create a project in Google Cloud Console**:
   - Access [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select an existing one

2. **Configure OAuth Consent Screen**:
   - Navigate to **APIs & Services** > **OAuth consent screen**
   - Select **External** for public applications
   - Fill in app name, support email, and developer contact
   - Add scopes: `openid`, `.../auth/userinfo.email`, `.../auth/userinfo.profile`

3. **Create OAuth Client ID**:
   - Navigate to **APIs & Services** > **Credentials**
   - Click **+ Create Credentials** > **OAuth client ID**
   - Select **Web application**
   - Add authorized JavaScript origins:
     - Development: `http://localhost:3000`, `http://localhost:5173`
     - Production: `https://your-domain.com`
   - Add authorized redirect URIs (same as JavaScript origins)
   - Copy the **Client ID** (format: `xxxxx.apps.googleusercontent.com`)

4. **Configure in Playground**:
   - Add `VITE_GOOGLE_CLIENT_ID` to `.env` file
   - Restart the development server

### Backend Configuration

Add the Google OAuth2 configuration in the `src/main/resources/application.properties` file:

```properties
# Google OAuth2 Client ID
social.auth.google.client-id=your-client-id.apps.googleusercontent.com
```

**Notes**:
- Replace `your-client-id.apps.googleusercontent.com` with the Client ID obtained from Google Cloud Console
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

### Verify Google Identity Services Script

The Google Identity Services script should be included in the `index.html` file:

**Location**: `src/main/resources/META-INF/resources/playground/index.html`

```html
<script src="https://accounts.google.com/gsi/client" async defer></script>
```

**Checks**:
- The script should be in the `<head>` of the document
- The `async defer` attributes ensure the script does not block page loading

### Testing Social Login

1. Access the login page
2. Verify that the "Login with Google" button is visible
3. Click the "Login with Google" button
4. A Google popup should appear for account selection and authentication
5. After authenticating, you should be redirected to the dashboard

## System Overview

### Features

The Playground provides a comprehensive interface for testing all features of the Orion Users service:

- ✅ **User Registration** - Create new user accounts
- ✅ **Simple Login** - Email and password authentication
- ✅ **Social Authentication** - Google OAuth2 login
- ✅ **Two-Factor Authentication (2FA)** - TOTP-based 2FA setup and validation
- ✅ **WebAuthn** - Biometric and security key authentication
- ✅ **Password Recovery** - Reset forgotten passwords
- ✅ **User Profile Management** - Update email and password
- ✅ **Email Validation** - Verify email addresses
- ✅ **Debug Tools** - API request/response logging for testing

### Prerequisites

- **Node.js**: Version 18 or higher
- **npm** or **yarn**: Package manager
- **Backend API**: The Orion Users backend service running (default: `http://localhost:8080`)

### Location

The playground application is located at:
```
src/main/resources/META-INF/resources/playground/
```

When built, the compiled files are generated in:
```
src/main/resources/META-INF/resources/test/
```

The application is served by the Quarkus backend at: **`http://localhost:8080/test`**

## Project Structure

```
src/main/resources/META-INF/resources/playground/
├── src/
│   ├── main.js                    # Vue app initialization and Vuetify setup
│   ├── App.vue                    # Root component
│   ├── components/                # Reusable components
│   │   ├── DebugModal.vue         # API debug modal
│   │   ├── LogList.vue            # Request/response log viewer
│   │   └── PasswordStrengthIndicator.vue  # Password strength meter
│   ├── router/
│   │   └── index.js               # Vue Router configuration (base: /test)
│   ├── services/
│   │   └── api.js                 # Axios HTTP client and API methods
│   ├── stores/                    # Pinia state management
│   │   ├── auth.js                # Authentication state
│   │   └── debug.js               # Debug logs state
│   ├── utils/
│   │   └── passwordValidation.js  # Password validation rules
│   └── views/                     # Page components
│       ├── LoginView.vue          # Login and registration page
│       ├── DashboardView.vue      # User dashboard
│       ├── TwoFactorView.vue      # 2FA setup and validation
│       ├── WebAuthnView.vue       # WebAuthn device registration and authentication
│       └── RecoverPasswordView.vue # Password recovery
├── index.html                     # HTML template
├── package.json                   # Dependencies and scripts
└── vite.config.js                # Vite configuration (base: /test/)

# Build output directory (served by Quarkus at /test)
src/main/resources/META-INF/resources/test/
├── index.html                     # Compiled HTML
├── assets/                        # Compiled JavaScript and CSS
└── ...
```

## User Guide

### User Registration

1. Navigate to the home page (`/`)
2. Click on the **Register** tab
3. Fill in:
   - **Name**: Your full name
   - **Email**: A valid email address
   - **Password**: Minimum 8 characters with at least one uppercase, one lowercase, one digit, and one special character
4. The password strength indicator will show the password strength in real-time
5. Click **Register**
6. You will be automatically logged in after successful registration
7. Check your email for a validation code

### Simple Login

1. Navigate to the home page (`/`)
2. Click on the **Login** tab
3. Enter your email and password
4. Click **Login**
5. If 2FA is enabled, you will be redirected to the 2FA validation page
6. Otherwise, you will be redirected to the dashboard

### Social Authentication

#### Google Login

1. Click the **Login with Google** button
2. A Google sign-in popup will appear
3. Select your Google account
4. Grant permissions if requested
5. You will be automatically logged in

**Note**: Requires `VITE_GOOGLE_CLIENT_ID` to be configured in `.env`

### Two-Factor Authentication (2FA)

#### Setting Up 2FA

1. Navigate to `/2fa`
2. Enter your email and password
3. Click **Generate QR Code**
4. Scan the QR code with an authenticator app:
   - Google Authenticator
   - Microsoft Authenticator
   - Authy
   - Any TOTP-compatible app
5. Enter the 6-digit code from your authenticator app
6. Click **Validate Code**
7. 2FA is now enabled for your account

#### Logging In with 2FA

1. Log in with your email and password
2. You will be automatically redirected to the 2FA validation page
3. Enter the 6-digit code from your authenticator app
4. Click **Validate Code**
5. You will be logged in and redirected to the dashboard

### WebAuthn Authentication

#### Registering a Device

1. Navigate to `/webauthn`
2. Click on the **Register Device** tab
3. Enter your email
4. (Optional) Enter a device name (e.g., "My Laptop", "iPhone 13")
5. Click **Register Device**
6. Follow your browser's prompts:
   - **Biometric**: Use fingerprint or face recognition
   - **Security Key**: Insert and activate your FIDO2 security key
   - **Platform Authenticator**: Use Windows Hello, Touch ID, or Face ID
7. The device will be registered and ready for authentication

#### Authenticating with WebAuthn

1. Navigate to `/webauthn`
2. Click on the **Authenticate** tab
3. Enter your email
4. Click **Authenticate with WebAuthn**
5. Follow your browser's prompts to authenticate
6. You will be logged in and redirected to the dashboard

**Note**: WebAuthn requires HTTPS in production. For local development, some browsers allow WebAuthn on `localhost` without HTTPS.

### Password Recovery

1. Navigate to `/recover-password`
2. Enter your email address
3. Click **Recover Password**
4. Check your email for the new password
5. Use the new password to log in
6. Consider changing your password after logging in

### User Dashboard

After logging in, you will be redirected to the dashboard (`/dashboard`) where you can:

- View your user profile information
- Update your email address
- Change your password
- Log out

#### Updating Email

1. Go to the dashboard
2. Enter your new email address in the **Update Email** section
3. Click **Update Email**
4. Check your new email for a validation code
5. Click the validation link in the email

#### Changing Password

1. Go to the dashboard
2. Enter your current password
3. Enter your new password (must meet strength requirements)
4. Confirm your new password
5. Click **Change Password**

### Debug Tools

The Playground includes a built-in debug tool that automatically logs all API requests and responses. This is very useful for debugging and understanding how the API works.

#### Opening the Debug Modal

1. Look for the **bug icon** (🐛) in the top-right corner of the application bar
2. Click the bug icon to open the Debug modal
3. The modal will display all API requests and responses

#### Using the Debug Logs

The debug modal provides several features:

**Tabs:**
- **All**: Shows all API requests (successful and failed)
- **Success**: Shows only successful requests (status 200-299)
- **Errors**: Shows only failed requests (status 400+ or network errors)

**Log Details:**

Each log entry shows:
- **Status**: HTTP status code and status text
- **Method**: HTTP method (GET, POST, PUT, DELETE)
- **URL**: Full request URL
- **Timestamp**: When the request was made

**Expanding Logs:**

Click on any log entry to expand and view detailed information:

- **Request Tab**: 
  - HTTP method
  - Full URL (base URL + endpoint)
  - Request data (body/payload)
  - Request headers (including Authorization token)
  
- **Response Tab** (for successful requests):
  - HTTP status code and status text
  - Response data (JSON response from API)
  - Response headers

- **Error Tab** (for failed requests):
  - Error message
  - HTTP status code (if available)
  - Error response data (if available)

**Copying Data:**

- Click the **copy icon** (📋) next to any data field to copy it to your clipboard
- Useful for sharing error messages or request/response data

**Clearing Logs:**

- Click the **trash icon** (🗑️) in the modal header to clear all logs
- Logs are automatically limited to the last 50 entries

**Note**: The debug logs are stored in browser memory and will be cleared when you refresh the page. They are not persisted to localStorage or sent to any server.

## Troubleshooting

### Application Not Loading at /test

- Ensure the application has been built: `npm run build` in the playground directory
- Verify the built files exist in `src/main/resources/META-INF/resources/test/`
- Make sure you're accessing the application at `http://localhost:8080/test` (not `/`)
- Check that the Quarkus backend is running and serving static files from `META-INF/resources/`
- Verify the `base: '/test/'` configuration in `vite.config.js`

### API Connection Issues

- Verify the backend is running on `http://localhost:8080`
- Check the `VITE_API_URL` in `.env` (if using environment variables)
- In development mode, verify the proxy configuration in `vite.config.js` is correctly forwarding `/users` requests
- Check browser console for CORS errors
- Verify network connectivity

### Social Login Not Working

- Ensure environment variables are set in `.env`
- Restart the development server after adding environment variables
- Check browser console for errors
- Verify OAuth credentials are correct
- Verify Google Identity Services script is loaded in `index.html`
- Check Google Cloud Console for authorized domains

### WebAuthn Not Working

- Ensure you're using HTTPS in production (or localhost in development)
- Check browser compatibility
- Verify the device supports WebAuthn
- Check browser console for errors

### 2FA QR Code Not Displaying

- Verify your email and password are correct
- Check browser console for errors
- Ensure the backend is running and accessible

## Additional Resources

- [Vue 3 Documentation](https://vuejs.org/)
- [Vuetify Documentation](https://vuetifyjs.com/)
- [Vue Router Documentation](https://router.vuejs.org/)
- [Pinia Documentation](https://pinia.vuejs.org/)
- [Vite Documentation](https://vitejs.dev/)
