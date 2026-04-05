---
layout: default
title: Admin Dashboard
nav_order: 4
---

# Admin Dashboard

The Admin Dashboard is a Vue 3 application built with Vuetify that provides an administrative interface for managing users in the Orion Users service. 

## Features

- **Authentication**: Secure login with `admin` role verification.
- **User List**: A comprehensive table displaying all users, with filtering and search capabilities.
- **User Details View**: Complete profile information for individual users.
- **Create User**: An administrative form to register new users directly.
- **Edit User**: Capabilities to update a user's email address and password.
- **Delete User**: Ability to securely remove users with confirmation.

## Quick Start (Docker)

The fastest way to test and use the Admin Dashboard is via Docker Compose:

```bash
docker compose up --build
```
Once deployed, the dashboard will be available at: `http://localhost:8080/dashboard`

## Development Mode

If you wish to modify the Vue 3 dashboard source code:

1. **Navigate to the admin folder**:
```bash
cd src/main/resources/META-INF/resources/admin
```

2. **Install dependencies** (first time only):
```bash
npm install
```

3. **Start the development server**:
```bash
npm run dev
```

The Vite dev server will run on port `3001` and expose the application at `http://localhost:3001/dashboard/`. It will automatically proxy API requests to the Quarkus backend running on `localhost:8080`.

## Production Build

To build the dashboard for production:

```bash
npm run build
```

The optimized files will be generated in `target/classes/META-INF/resources/dashboard/` and are automatically served by the Quarkus server under the `/dashboard` path. (This build task is also executed automatically via `mvn generate-resources` during the standard Maven build.)

## Authentication Mechanism

The dashboard requires authentication. The user must possess a valid JWT token that contains the `admin` role. 
The token is stored in the browser's `localStorage` and is automatically attached to all outbound requests via an Axios interceptor.

## Endpoints Consumed

The dashboard consumes the following Orion Users API endpoints:
- `GET /users/list` - Retrieves all registered users (admin only)
- `GET /users/by-email?email={email}` - Searches for a user by email (admin only)
- `POST /users/create` - Creates a new user
- `PUT /users/update` - Updates existing user details
- `POST /users/delete` - Deletes a user (admin only)
- `POST /users/login` - Authenticates and issues a new JWT
