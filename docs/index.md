---
layout: default
title: Home
nav_order: 1
---

# Orion users documentation

[![Netlify Status](https://api.netlify.com/api/v1/badges/f305c108-5bfb-4aae-85f3-31d216ce2214/deploy-status)](https://app.netlify.com/sites/users-orion-services/deploys)
[![Discord](https://img.shields.io/discord/713516488601894922?style=flat&label=Discord&color=%23D8FCD3&link=https%3A%2F%2Fdiscord.com%2Finvite%2FXpyGTZPApN)](https://discord.com/invite/XpyGTZPApN)

Orion users is a small identity service intended for those who want to start a
prototype or project without the need to implement basic features like
managing and authenticating users.

Unlike feature-rich identity services like [keycloak](https://www.keycloak.org),
Orion Users is intended to provide a small and generic set of features that
developers can extends and customize freely.

Orion Users is written in [Quarkus](https://quarkus.io) through [reactive
programming](https://quarkus.io/guides/getting-started-reactive) and prepared to
run with [native compilation](https://quarkus.io/guides/building-native-image),
in other words, it is a code developed to run in cloud services with high
availability, low memory consumption (high density in clusters) and low
throughput.
