---
layout: default
title: Frontend Documentation
nav_order: 3
---

# Frontend Documentation

This document provides a comprehensive guide to using and developing the Orion Users frontend application.

## Overview

The Orion Users frontend is a Vue 3 application built with Vuetify that provides a user interface for all user management and authentication features of the Orion Users service. The frontend application is located in the `playground` directory and is served by the Quarkus backend at the `/test` URL path.

## Quick Start

### Running the Playground

The playground application is integrated with the Quarkus backend and is accessible at:

**`http://localhost:8080/test`**

To run the playground:

1. **Start the Quarkus backend** (this will serve the compiled frontend):
```bash
./mvnw compile quarkus:dev
```

2. **Access the application**:
   - Open your browser and navigate to: `http://localhost:8080/test`
   - The application will be available at this URL

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

**Note**: After making changes, rebuild the application (`npm run build`) for the changes to be available when accessing via the Quarkus backend at `/test`.

## Features

- ✅ User registration
- ✅ Simple login with email and password
- ✅ Social authentication (Google)
- ✅ Two-factor authentication (2FA)
- ✅ WebAuthn (biometric/security key authentication)
- ✅ Password recovery
- ✅ User profile management
- ✅ Email validation
- ✅ Debug tools for API testing

## Prerequisites

- **Node.js**: Version 18 or higher
- **npm** or **yarn**: Package manager
- **Backend API**: The Orion Users backend service running (default: `http://localhost:8080`)

## Location

The frontend playground application is located at:
```
src/main/resources/META-INF/resources/playground/
```

When built, the compiled files are generated in:
```
src/main/resources/META-INF/resources/test/
```

The application is served by the Quarkus backend at: **`http://localhost:8080/test`**

## Installation

1. Navigate to the playground directory:
```bash
cd src/main/resources/META-INF/resources/playground
```

2. Install dependencies:
```bash
npm install
```

## Configuration

### Environment Variables

Create a `.env` file in the `playground/` directory root:

```env
# Backend API URL
VITE_API_URL=http://localhost:8080

# Google OAuth2 Client ID (optional, for social login)
VITE_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

### Development Server Configuration

The development server is configured in `vite.config.js`:

- **Port**: 3000 (default)
- **Proxy**: Automatically proxies `/users` requests to the backend API
- **Hot Module Replacement**: Enabled for fast development

## Running the Application

### Development Mode

1. Start the Quarkus backend server:
```bash
./mvnw compile quarkus:dev
```

2. In a separate terminal, navigate to the playground directory and start the Vite development server:
```bash
cd src/main/resources/META-INF/resources/playground
npm run dev
```

The Vite development server will be available at `http://localhost:3000/test` (note the `/test` base path).

**Note**: In development mode, Vite runs on port 3000 with hot module replacement. The application is configured to use `/test` as the base path, and API requests to `/users` are automatically proxied to the Quarkus backend running on port 8080.

### Production Build

1. Navigate to the playground directory:
```bash
cd src/main/resources/META-INF/resources/playground
```

2. Build the application:
```bash
npm run build
```

The built files will be generated in `src/main/resources/META-INF/resources/test/` directory, which is automatically served by Quarkus.

3. Start or restart the Quarkus backend:
```bash
./mvnw compile quarkus:dev
```

4. Access the application at:
```
http://localhost:8080/test
```

**Important**: The application is configured to be served at the `/test` URL path. Make sure to access it at `http://localhost:8080/test` (not at the root `/`).

### Preview Production Build

To preview the production build locally with Vite:

```bash
npm run preview
```

This will serve the built files using Vite's preview server. However, for production-like testing, it's recommended to use the Quarkus backend as described above.

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
│       ├── WebAuthnView.vue       # WebAuthn registration and authentication
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

## Usage Guide

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

### Viewing API Request/Response Logs

The frontend includes a built-in debug tool that automatically logs all API requests and responses. This is very useful for debugging and understanding how the API works.

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

#### What Gets Logged

The debug tool automatically logs:
- All API requests made through the `userApi` service
- Request method, URL, data, and headers
- Response status, data, and headers
- Error information (if request fails)
- Timestamp for each request

**Example Use Cases:**

1. **Debugging Login Issues**: 
   - Check if the request is being sent correctly
   - See the exact error message from the API
   - Verify the request payload format

2. **Understanding API Responses**:
   - See the exact structure of API responses
   - Copy response data for testing
   - Understand error formats

3. **Testing API Endpoints**:
   - See what data is being sent
   - Verify authentication tokens
   - Check request headers

4. **Development**:
   - Monitor all API calls during development
   - Debug integration issues
   - Understand API behavior

**Note**: The debug logs are stored in browser memory and will be cleared when you refresh the page. They are not persisted to localStorage or sent to any server.

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

## API Service

The frontend uses a centralized API service located in `src/services/api.js`. All API calls are made through this service.

### Available Methods

```javascript
import { userApi } from '@/services/api'

// Registration
userApi.createUser(name, email, password)
userApi.createAndAuthenticate(name, email, password)

// Authentication
userApi.login(email, password)
userApi.loginWithGoogle(idToken)

// Two-Factor Authentication
userApi.generate2FAQRCode(email, password)
userApi.validate2FACode(email, code)
userApi.loginWith2FA(email, code)

// WebAuthn
userApi.startWebAuthnRegistration(email, origin)
userApi.finishWebAuthnRegistration(email, response, origin, deviceName)
userApi.startWebAuthnAuthentication(email)
userApi.finishWebAuthnAuthentication(email, response)

// Email Validation
userApi.validateEmail(email, code)

// Password Recovery
userApi.recoverPassword(email)

// User Management
userApi.updateUser(email, newEmail, password, newPassword)
```

### Request/Response Interceptors

The API service includes interceptors that:
- Automatically add JWT tokens to authenticated requests
- Handle 401 errors by clearing authentication state
- Log all requests and responses for debugging

## State Management

The application uses Pinia for state management.

### Auth Store

Located in `src/stores/auth.js`:

```javascript
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

// Check if user is authenticated
authStore.isAuthenticated

// Get current user
authStore.user

// Get auth token
authStore.token

// Set authentication
authStore.setAuth(token, user)

// Logout
authStore.logout()
```

### Debug Store

Located in `src/stores/debug.js`:

```javascript
import { useDebugStore } from '@/stores/debug'

const debugStore = useDebugStore()

// View logs
debugStore.logs

// Clear logs
debugStore.clearLogs()
```

## Routing

The application uses Vue Router for navigation. Routes are defined in `src/router/index.js`.

### Available Routes

All routes are prefixed with `/test` when served by Quarkus:

- `/test/` - Login and registration page
- `/test/dashboard` - User dashboard (requires authentication)
- `/test/2fa` - Two-factor authentication setup and validation
- `/test/webauthn` - WebAuthn device registration and authentication
- `/test/recover-password` - Password recovery

**Note**: The Vue Router is configured with base path `/test`, so internal navigation will automatically include this prefix.

### Route Guards

Routes with `meta: { requiresAuth: true }` are protected and will redirect unauthenticated users to the login page.

## Components

### PasswordStrengthIndicator

Displays password strength in real-time based on validation rules.

**Props:**
- `password` (String): The password to evaluate

**Usage:**
```vue
<PasswordStrengthIndicator :password="password" />
```

### DebugModal

Modal component for viewing API request/response logs.

**Usage:**
```vue
<DebugModal />
```

## Utilities

### Password Validation

Located in `src/utils/passwordValidation.js`:

```javascript
import { getPasswordRules } from '@/utils/passwordValidation'

const rules = getPasswordRules()
// Returns Vuetify validation rules for password fields
```

## Browser Support

- **Chrome**: 90+
- **Firefox**: 88+
- **Safari**: 14+
- **Edge**: 90+

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

### WebAuthn Not Working

- Ensure you're using HTTPS in production (or localhost in development)
- Check browser compatibility
- Verify the device supports WebAuthn
- Check browser console for errors

### 2FA QR Code Not Displaying

- Verify your email and password are correct
- Check browser console for errors
- Ensure the backend is running and accessible

## Development

### Adding New Features

1. Create components in `src/components/`
2. Create views in `src/views/`
3. Add API methods in `src/services/api.js`
4. Add routes in `src/router/index.js`
5. Add state management in `src/stores/` if needed

### Code Style

- Use Vue 3 Composition API with `<script setup>`
- Follow Vuetify design guidelines
- Use TypeScript-style JSDoc comments for better IDE support
- Keep components small and focused

### Testing

Currently, the application relies on manual testing. Consider adding:
- Unit tests with Vitest
- Component tests with Vue Test Utils
- E2E tests with Playwright or Cypress

## Deployment

### Building for Production

1. Navigate to the playground directory:
```bash
cd src/main/resources/META-INF/resources/playground
```

2. Build the application:
```bash
npm run build
```

3. The built files will be in `src/main/resources/META-INF/resources/test/`

4. Package the Quarkus application:
```bash
./mvnw package
```

5. The frontend will be included in the Quarkus JAR and served at `/test` when the application runs.

### Environment Variables for Production

Since the frontend is bundled with the Quarkus backend, environment variables should be configured in the Quarkus `application.properties` file or as system properties when running the application.

### Deployment

The frontend is deployed together with the Quarkus backend as a single application. When you deploy the Quarkus application, the frontend will be automatically available at the `/test` path.

**Important**: Ensure that your deployment environment allows serving static files from `META-INF/resources/` and that the `/test` path is accessible.

## Security Considerations

- Never commit `.env` files to version control
- Use HTTPS in production
- Validate all user inputs
- Sanitize data before displaying
- Keep dependencies updated
- Use environment variables for sensitive configuration

## Additional Resources

- [Vue 3 Documentation](https://vuejs.org/)
- [Vuetify Documentation](https://vuetifyjs.com/)
- [Vue Router Documentation](https://router.vuejs.org/)
- [Pinia Documentation](https://pinia.vuejs.org/)
- [Vite Documentation](https://vitejs.dev/)

