---
layout: default
title: Documentação do Usuário
nav_order: 1
---

# Documentação do Usuário

Bem-vindo à documentação do usuário do Orion Users! Esta seção contém guias detalhados sobre como usar as funcionalidades de segurança avançada disponíveis no serviço.

## Funcionalidades Disponíveis

### Autenticação em Dois Fatores (2FA)

A autenticação em dois fatores adiciona uma camada extra de segurança à sua conta usando códigos TOTP gerados por aplicativos autenticadores.

[**Guia Completo de 2FA →**](2FA.md)

**Recursos:**
- Geração de código QR para configuração
- Suporte a aplicativos autenticadores populares (Google Authenticator, Microsoft Authenticator, etc.)
- Integração com o fluxo de login existente
- Validação de códigos TOTP

### WebAuthn (Autenticação sem Senha)

O WebAuthn permite autenticação sem senhas usando chaves de segurança físicas ou biometria do dispositivo.

[**Guia Completo de WebAuthn →**](WebAuthn.md)

**Recursos:**
- Suporte a chaves de segurança FIDO2
- Autenticação biométrica (impressão digital, reconhecimento facial)
- Registro e gerenciamento de múltiplos dispositivos
- Autenticação rápida e segura

## Início Rápido

### Ativar 2FA

1. Faça login na sua conta
2. Gere um código QR através do endpoint `/users/google/2FAuth/qrCode`
3. Escaneie o código com um aplicativo autenticador
4. Valide a configuração com um código TOTP

### Registrar Dispositivo WebAuthn

1. Inicie o registro através do endpoint `/users/webauthn/register/start`
2. Use a API WebAuthn do navegador para criar a credencial
3. Finalize o registro através do endpoint `/users/webauthn/register/finish`

## Segurança

Ambas as funcionalidades (2FA e WebAuthn) foram projetadas para aumentar significativamente a segurança da sua conta:

- **2FA**: Adiciona uma segunda camada de autenticação usando algo que você possui (seu dispositivo móvel)
- **WebAuthn**: Elimina a necessidade de senhas, usando criptografia de chave pública e dispositivos físicos ou biometria

## Suporte

Se você encontrar problemas ou tiver dúvidas:

1. Consulte a seção "Solução de Problemas" em cada guia
2. Verifique os requisitos técnicos
3. Entre em contato com o suporte se necessário

## Próximos Passos

- [Configurar 2FA](2FA.md)
- [Configurar WebAuthn](WebAuthn.md)
- [Documentação da API](../usecases/usecases.md)

