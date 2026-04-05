---
layout: default
title: Home
nav_order: 1
---

# Orion Users Documentation

[![Netlify Status](https://api.netlify.com/api/v1/badges/f305c108-5bfb-4aae-85f3-31d216ce2214/deploy-status)](https://app.netlify.com/sites/users-orion-services/deploys)
[![Discord](https://img.shields.io/discord/713516488601894922?style=flat&label=Discord&color=%23D8FCD3&link=https%3A%2F%2Fdiscord.com%2Finvite%2FXpyGTZPApN)](https://discord.com/invite/XpyGTZPApN)

Orion Users is a lightweight identity service intended for developers who want to start a prototype or project without the overhead of implementing basic features like user management and authentication from scratch.

Unlike heavy, feature-rich identity services like [Keycloak](https://www.keycloak.org), Orion Users is designed to provide a small, generic set of foundational features that developers can easily extend and customize.

## Technology Stack

Orion Users is written in **[Kotlin](https://kotlinlang.org/)** and **[Quarkus](https://quarkus.io)** using [reactive programming](https://quarkus.io/guides/getting-started-reactive). It is fully prepared to run with [native compilation](https://quarkus.io/guides/building-native-image).

This means it is fundamentally engineered for cloud environments, offering:
- **High availability**
- **Extremely low memory consumption** (allowing for high density in clusters)
- **Low latency and high throughput**

Furthermore, the service includes native support for **Docker** and **Docker Compose**, streamlining deployment both locally and in production environments.

## Embedded User Interfaces

To make testing and administration effortless from day one, Orion Users comes with two pre-installed Vue 3 applications:

- **[Playground](playground/Playground.md):** An interactive UI (`/test`) for experimenting with all authentication features, including Social Login (Google), 2FA, WebAuthn, and more.
- **[Admin Dashboard](admin/AdminDashboard.md):** A powerful administrative interface (`/dashboard`) that allows authorized admins to view, create, edit, and delete user accounts easily.
