---
layout: default
title: WebAuthn (Autenticação sem Senha)
parent: Documentação do Usuário
nav_order: 2
---

# WebAuthn (Autenticação sem Senha)

## O que é WebAuthn?

WebAuthn é um padrão de autenticação que permite fazer login sem usar senhas tradicionais. Em vez disso, você usa dispositivos de segurança físicos (como chaves de segurança USB) ou recursos biométricos do seu dispositivo (como impressão digital ou reconhecimento facial).

## Dispositivos Suportados

### Chaves de Segurança FIDO2

- **YubiKey**: Chaves de segurança USB e NFC
- **Google Titan**: Chaves de segurança USB e Bluetooth
- **Feitian**: Várias opções de chaves de segurança
- Qualquer dispositivo compatível com FIDO2/WebAuthn

### Biometria

- **Windows Hello**: Impressão digital e reconhecimento facial no Windows
- **Touch ID**: Impressão digital em dispositivos Apple
- **Face ID**: Reconhecimento facial em dispositivos Apple
- **Android Biometric**: Impressão digital e reconhecimento facial em Android

## Como Registrar um Dispositivo WebAuthn

### Passo 1: Iniciar o Registro

Envie uma requisição POST para iniciar o processo de registro:

```bash
curl -X POST \
  'http://localhost:8080/users/webauthn/register/start' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com'
```

A resposta contém as opções de registro (PublicKeyCredentialCreationOptions) que serão usadas pelo navegador para criar a credencial.

### Passo 2: Criar a Credencial no Navegador

No seu aplicativo frontend, use a API WebAuthn do navegador para criar a credencial:

```javascript
// Parse as opções recebidas do servidor
const options = JSON.parse(response.options);

// Converter challenge de base64url para ArrayBuffer
const challenge = base64urlToArrayBuffer(options.challenge);

// Criar a credencial
const credential = await navigator.credentials.create({
  publicKey: {
    ...options,
    challenge: challenge
  }
});
```

### Passo 3: Finalizar o Registro

Envie a resposta da credencial para o servidor:

```bash
curl -X POST \
  'http://localhost:8080/users/webauthn/register/finish' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com' \
  --data-urlencode 'response=<credencial_json>' \
  --data-urlencode 'deviceName=Meu Dispositivo'
```

O `deviceName` é opcional e ajuda você a identificar o dispositivo registrado.

## Como Autenticar com WebAuthn

### Passo 1: Iniciar a Autenticação

Envie uma requisição POST para iniciar o processo de autenticação:

```bash
curl -X POST \
  'http://localhost:8080/users/webauthn/authenticate/start' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com'
```

A resposta contém as opções de autenticação (PublicKeyCredentialRequestOptions).

### Passo 2: Autenticar no Navegador

No seu aplicativo frontend, use a API WebAuthn do navegador:

```javascript
// Parse as opções recebidas do servidor
const options = JSON.parse(response.options);

// Converter challenge de base64url para ArrayBuffer
const challenge = base64urlToArrayBuffer(options.challenge);

// Obter a credencial
const assertion = await navigator.credentials.get({
  publicKey: {
    ...options,
    challenge: challenge
  }
});
```

### Passo 3: Finalizar a Autenticação

Envie a resposta da autenticação para o servidor:

```bash
curl -X POST \
  'http://localhost:8080/users/webauthn/authenticate/finish' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'email=seu@email.com' \
  --data-urlencode 'response=<assertion_json>'
```

Se a autenticação for bem-sucedida, você receberá um token JWT.

## Como Remover um Dispositivo

Atualmente, a remoção de dispositivos WebAuthn requer contato com o suporte ou acesso direto ao banco de dados. Em versões futuras, será possível gerenciar dispositivos através da interface do usuário.

## Requisitos Técnicos

### Navegadores Suportados

- **Chrome**: Versão 67+
- **Firefox**: Versão 60+
- **Safari**: Versão 13+
- **Edge**: Versão 18+

### HTTPS Obrigatório

O WebAuthn requer conexão HTTPS para funcionar. Em desenvolvimento local, você pode usar `https://localhost` com certificado auto-assinado.

### Domínio Configurado

O domínio deve estar configurado corretamente no servidor. Para desenvolvimento local, use `localhost`.

## Solução de Problemas

### "NotSupportedError" no navegador

1. **Verifique o navegador**: Certifique-se de estar usando um navegador compatível com WebAuthn
2. **Verifique HTTPS**: WebAuthn só funciona em HTTPS (ou localhost)
3. **Verifique o dispositivo**: Certifique-se de que seu dispositivo suporta WebAuthn

### A chave de segurança não é reconhecida

1. **Verifique a conexão**: Certifique-se de que a chave está conectada corretamente
2. **Tente outra porta USB**: Algumas chaves funcionam melhor em portas USB 2.0
3. **Verifique os drivers**: No Windows, pode ser necessário instalar drivers específicos
4. **Teste em outro navegador**: Alguns navegadores têm melhor suporte que outros

### Biometria não funciona

1. **Verifique as configurações**: Certifique-se de que a biometria está configurada no dispositivo
2. **Verifique as permissões**: O navegador precisa ter permissão para acessar a biometria
3. **Tente outro método**: Se Face ID não funcionar, tente Touch ID ou vice-versa

### "Invalid credential" durante autenticação

1. **Verifique o email**: Certifique-se de estar usando o mesmo email usado no registro
2. **Verifique o dispositivo**: Use o mesmo dispositivo ou chave de segurança usada no registro
3. **Verifique se o dispositivo está registrado**: Confirme que você completou o processo de registro

## Segurança

- **Proteja seu dispositivo**: Mantenha seu dispositivo físico seguro
- **Use chaves de segurança**: Chaves de segurança físicas são mais seguras que biometria
- **Tenha dispositivos de backup**: Registre múltiplos dispositivos para evitar perda de acesso
- **Notifique sobre atividade suspeita**: Se receber solicitações de autenticação WebAuthn sem ter solicitado, sua conta pode estar comprometida

## Vantagens do WebAuthn

- **Sem senhas**: Não precisa lembrar ou gerenciar senhas
- **Mais seguro**: Resistant a phishing e ataques de força bruta
- **Mais rápido**: Autenticação rápida com biometria ou chave de segurança
- **Padrão aberto**: Suportado por todos os principais navegadores e plataformas

## Exemplo Completo (JavaScript)

```javascript
// Função auxiliar para converter base64url para ArrayBuffer
function base64urlToArrayBuffer(base64url) {
  const base64 = base64url.replace(/-/g, '+').replace(/_/g, '/');
  const binary = atob(base64);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
}

// Função auxiliar para converter ArrayBuffer para base64url
function arrayBufferToBase64url(buffer) {
  const bytes = new Uint8Array(buffer);
  let binary = '';
  for (let i = 0; i < bytes.length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  const base64 = btoa(binary);
  return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}

// Registrar dispositivo
async function registerWebAuthn(email) {
  // 1. Iniciar registro
  const startResponse = await fetch('/users/webauthn/register/start', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({ email })
  });
  const startData = await startResponse.json();
  const options = JSON.parse(startData.options);
  
  // 2. Criar credencial
  const publicKey = {
    ...options,
    challenge: base64urlToArrayBuffer(options.challenge)
  };
  const credential = await navigator.credentials.create({ publicKey });
  
  // 3. Finalizar registro
  const finishResponse = await fetch('/users/webauthn/register/finish', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      email,
      response: JSON.stringify(credential),
      deviceName: 'Meu Dispositivo'
    })
  });
  
  return await finishResponse.json();
}

// Autenticar com WebAuthn
async function authenticateWebAuthn(email) {
  // 1. Iniciar autenticação
  const startResponse = await fetch('/users/webauthn/authenticate/start', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({ email })
  });
  const startData = await startResponse.json();
  const options = JSON.parse(startData.options);
  
  // 2. Obter credencial
  const publicKey = {
    ...options,
    challenge: base64urlToArrayBuffer(options.challenge)
  };
  const assertion = await navigator.credentials.get({ publicKey });
  
  // 3. Finalizar autenticação
  const finishResponse = await fetch('/users/webauthn/authenticate/finish', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams({
      email,
      response: JSON.stringify(assertion)
    })
  });
  
  return await finishResponse.json();
}
```

