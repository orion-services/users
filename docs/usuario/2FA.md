---
layout: default
title: Autenticação em Dois Fatores (2FA)
parent: Documentação do Usuário
nav_order: 1
---

# Autenticação em Dois Fatores (2FA)

## O que é 2FA?

A Autenticação em Dois Fatores (2FA) adiciona uma camada extra de segurança à sua conta. Além da sua senha, você precisará fornecer um código de verificação gerado por um aplicativo autenticador no seu dispositivo móvel.

## Como Funciona?

Quando você ativa o 2FA, o sistema gera um código QR que você escaneia com um aplicativo autenticador (como Google Authenticator, Microsoft Authenticator, ou Authy). A partir desse momento, sempre que você fizer login, além da sua senha, você precisará informar o código de 6 dígitos gerado pelo aplicativo.

## Como Ativar o 2FA

### Passo 1: Fazer Login

Primeiro, faça login na sua conta usando seu email e senha normalmente.

### Passo 2: Gerar o Código QR

Envie uma requisição POST para o endpoint `/users/google/2FAuth/qrCode` com suas credenciais:

```bash
curl -X POST \
  'http://localhost:8080/users/google/2FAuth/qrCode' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com' \
  --data-urlencode 'password=suasenha'
```

A resposta será uma imagem PNG contendo o código QR.

### Passo 3: Escanear o Código QR

1. Abra o aplicativo autenticador no seu dispositivo móvel (Google Authenticator, Microsoft Authenticator, etc.)
2. Toque em "Adicionar conta" ou o botão "+"
3. Escolha "Escanear código QR"
4. Escaneie o código QR recebido na resposta da API
5. O aplicativo começará a gerar códigos de 6 dígitos que mudam a cada 30 segundos

### Passo 4: Validar a Configuração

Para confirmar que o 2FA está configurado corretamente, valide um código:

```bash
curl -X POST \
  'http://localhost:8080/users/google/2FAuth/validate' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com' \
  --data-urlencode 'code=123456'
```

Substitua `123456` pelo código atual exibido no seu aplicativo autenticador.

Se a validação for bem-sucedida, você receberá um token JWT, confirmando que o 2FA está ativo.

## Como Usar o 2FA no Login

### Login Normal (sem 2FA)

Se você não tiver 2FA ativado, o login funciona normalmente:

```bash
curl -X POST \
  'http://localhost:8080/users/login' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com' \
  --data-urlencode 'password=suasenha'
```

### Login com 2FA Ativado

Se você tiver 2FA ativado, o processo é em duas etapas:

**Etapa 1: Login Inicial**

```bash
curl -X POST \
  'http://localhost:8080/users/login' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com' \
  --data-urlencode 'password=suasenha'
```

A resposta indicará que o código 2FA é necessário:

```json
{
  "requires2FA": true,
  "message": "2FA code required"
}
```

**Etapa 2: Validar Código 2FA**

Use o endpoint `/users/login/2fa` para completar a autenticação:

```bash
curl -X POST \
  'http://localhost:8080/users/login/2fa' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com' \
  --data-urlencode 'code=123456'
```

Substitua `123456` pelo código atual do seu aplicativo autenticador.

Se o código estiver correto, você receberá o token JWT de autenticação.

## Como Desativar o 2FA

Atualmente, a desativação do 2FA requer contato com o suporte ou acesso direto ao banco de dados. Em versões futuras, será possível desativar através da interface do usuário.

## Solução de Problemas

### O código não está funcionando

1. **Verifique a hora do dispositivo**: Os códigos TOTP dependem do tempo sincronizado. Certifique-se de que o relógio do seu dispositivo está correto.

2. **Use o código mais recente**: Os códigos mudam a cada 30 segundos. Certifique-se de usar o código atual exibido no aplicativo.

3. **Verifique se o 2FA está ativado**: Confirme que você completou o processo de ativação corretamente.

### Perdi acesso ao aplicativo autenticador

Se você perdeu acesso ao aplicativo autenticador e não tem códigos de backup, entre em contato com o suporte para recuperar o acesso à sua conta.

### O QR Code não escaneia

1. Certifique-se de que a imagem está nítida
2. Tente aumentar o brilho da tela
3. Verifique se o aplicativo autenticador tem permissão para usar a câmera
4. Tente inserir manualmente a chave secreta (se disponível)

## Segurança

- **Mantenha seu dispositivo seguro**: O aplicativo autenticador deve estar protegido com senha ou biometria
- **Não compartilhe códigos**: Nunca compartilhe códigos 2FA com outras pessoas
- **Use códigos de backup**: Alguns aplicativos permitem gerar códigos de backup - guarde-os em local seguro
- **Notifique sobre atividade suspeita**: Se receber códigos 2FA sem ter solicitado login, sua conta pode estar comprometida

## Aplicativos Recomendados

- **Google Authenticator**: Disponível para iOS e Android
- **Microsoft Authenticator**: Disponível para iOS e Android
- **Authy**: Disponível para iOS, Android e Desktop
- **1Password**: Inclui autenticador integrado

