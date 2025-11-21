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

## Arquitetura e Decisões de Design

### Suporte a Múltiplos Tipos de Tokens

O sistema foi projetado para suportar dois tipos de tokens do Google, oferecendo flexibilidade na integração:

1. **ID Token (JWT)**: Token de identidade assinado pelo Google que contém informações do usuário diretamente no payload. Este é o método preferido e mais seguro.

2. **Access Token**: Token de acesso OAuth2 que permite buscar informações do usuário através da API do Google. O sistema usa este método como fallback quando o ID token não está disponível.

### Benefícios da Abordagem Flexível

- **Compatibilidade**: Funciona com diferentes implementações do Google Identity Services
- **Facilidade de Integração**: Desenvolvedores podem usar tanto One Tap quanto OAuth2 Token Client
- **Resiliência**: Se um método falhar, o sistema tenta automaticamente o outro

### Limitações e Considerações de Segurança

- **Validação de Assinatura**: A validação completa da assinatura do token não está implementada na versão atual (recomendado para produção)
- **Expiração**: Tokens expirados podem ser rejeitados pelo Google API
- **HTTPS**: Sempre use HTTPS em produção para proteger os tokens durante a transmissão

### Recomendações para Produção

1. Implementar validação completa da assinatura do token usando as chaves públicas do Google
2. Validar o campo `exp` (expiration) do JWT
3. Verificar o campo `iss` (issuer) para garantir que o token veio do Google
4. Implementar rate limiting para prevenir abuso
5. Logar tentativas de autenticação falhadas para monitoramento

## Google Login - Detalhado

### Visão Geral do Fluxo OAuth2

O Google Login utiliza o protocolo OAuth2 com OpenID Connect para autenticar usuários. O fluxo completo envolve:

1. **Frontend**: Inicializa o Google Identity Services
2. **Google**: Autentica o usuário e retorna um token
3. **Frontend**: Envia o token para o backend
4. **Backend**: Valida o token e extrai informações do usuário
5. **Backend**: Cria ou encontra o usuário no sistema
6. **Backend**: Gera um JWT do sistema e retorna para o frontend

### Métodos de Autenticação Suportados

O frontend implementa dois métodos de autenticação do Google:

#### 1. Google One Tap

O Google One Tap é uma experiência de login sem fricção que aparece automaticamente quando o usuário visita a página. O sistema tenta usar este método primeiro.

**Características:**
- Aparece automaticamente na página
- Não requer clique em botão
- Experiência mais rápida para o usuário
- Requer que o usuário tenha feito login recentemente no Google

#### 2. OAuth2 Token Client (Fallback)

Se o One Tap não estiver disponível ou não for exibido, o sistema usa o OAuth2 Token Client, que abre um popup para autenticação.

**Características:**
- Requer clique no botão "Login with Google"
- Abre popup do Google para autenticação
- Funciona mesmo se o usuário não estiver logado no Google
- Mais confiável em diferentes cenários

### Tipos de Tokens Suportados

O backend suporta dois tipos de tokens do Google:

#### ID Token (JWT)

O ID Token é um JWT (JSON Web Token) assinado pelo Google que contém informações do usuário diretamente no payload.

**Estrutura do Token:**
```
header.payload.signature
```

**Payload típico:**
```json
{
  "iss": "https://accounts.google.com",
  "sub": "1234567890",
  "email": "user@example.com",
  "email_verified": true,
  "name": "John Doe",
  "given_name": "John",
  "family_name": "Doe",
  "picture": "https://...",
  "iat": 1234567890,
  "exp": 1234571490
}
```

**Vantagens:**
- Contém todas as informações necessárias no próprio token
- Não requer chamada adicional à API do Google
- Mais rápido e eficiente
- Mais seguro (assinado digitalmente)

#### Access Token

O Access Token é um token OAuth2 que permite acessar recursos protegidos da API do Google.

**Uso no Sistema:**
Quando o backend recebe um access token (não é um JWT válido), ele faz uma chamada à API do Google para obter as informações do usuário:

```
GET https://www.googleapis.com/oauth2/v2/userinfo
Authorization: Bearer {access_token}
```

**Resposta da API:**
```json
{
  "id": "1234567890",
  "email": "user@example.com",
  "verified_email": true,
  "name": "John Doe",
  "given_name": "John",
  "family_name": "Doe",
  "picture": "https://...",
  "locale": "pt-BR"
}
```

**Vantagens:**
- Funciona como fallback quando ID token não está disponível
- Permite flexibilidade na implementação do frontend

### Fluxo Passo a Passo

#### 1. Inicialização no Frontend

```javascript
// Aguarda o Google Identity Services carregar
await new Promise((resolve) => {
  if (typeof google !== 'undefined' && google.accounts) {
    resolve()
  } else {
    // Aguarda até 5 segundos
  }
})

// Inicializa o cliente OAuth2
google.accounts.id.initialize({
  client_id: 'YOUR_CLIENT_ID',
  callback: async (response) => {
    // response.credential contém o ID token
    await userApi.loginWithGoogle(response.credential)
  }
})
```

#### 2. Tentativa de One Tap

O sistema primeiro tenta exibir o One Tap:

```javascript
google.accounts.id.prompt((notification) => {
  if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
    // Fallback para OAuth2 Token Client
  }
})
```

#### 3. Fallback para OAuth2 Token Client

Se o One Tap não funcionar, o sistema usa o OAuth2 Token Client:

```javascript
google.accounts.oauth2.initTokenClient({
  client_id: 'YOUR_CLIENT_ID',
  scope: 'openid profile email',
  callback: async (tokenResponse) => {
    // tokenResponse.access_token contém o access token
    await userApi.loginWithGoogle(tokenResponse.access_token)
  }
}).requestAccessToken()
```

#### 4. Envio para o Backend

O frontend envia o token (ID token ou access token) para o backend:

```javascript
// POST /users/login/google
// Content-Type: application/x-www-form-urlencoded
// idToken={token}
```

#### 5. Validação no Backend

O backend valida o token seguindo este processo:

1. **Normalização**: Remove espaços em branco do token
2. **Tentativa de Validação como JWT**:
   - Verifica se o token tem formato JWT (3 partes separadas por pontos)
   - Decodifica o payload (base64url)
   - Extrai email e nome do payload
3. **Fallback para Access Token**:
   - Se não for um JWT válido, assume que é um access token
   - Faz chamada à API do Google: `GET https://www.googleapis.com/oauth2/v2/userinfo`
   - Extrai email e nome da resposta

#### 6. Criação/Busca do Usuário

O backend busca o usuário pelo email. Se não existir, cria automaticamente:

- Email marcado como validado (`emailValid = true`)
- Senha aleatória gerada (não usada, mas requerida pelo schema)
- Role padrão: "user"

#### 7. Geração do JWT do Sistema

O backend gera um JWT próprio do sistema e retorna:

```json
{
  "user": { ... },
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9..."
}
```

## HTTPS endpoints

### Google Login

* `/users/login/google`
  * Method: POST
  * Consumes: application/x-www-form-urlencoded
  * Produces: application/json
  * Description: Autentica um usuário usando um token do Google (ID token ou access token)

#### Request com ID Token (JWT)

```shell
curl -X POST \
  'http://localhost:8080/users/login/google' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzQ1NiJ9.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJzdWIiOiIxMjM0NTY3ODkwIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJKb2huIERvZSIsInBpY3R1cmUiOiJodHRwczovLy4uLiIsImlhdCI6MTIzNDU2Nzg5MCwiZXhwIjoxMjM0NTcxNDkwfQ.signature'
```

#### Request com Access Token

```shell
curl -X POST \
  'http://localhost:8080/users/login/google' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=ya29.a0AfH6SMBx...'
```

#### Response de Sucesso

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

#### Response de Erro (Token Inválido)

```json
{
  "message": "Invalid Google token: Token is empty"
}
```

Status: `401 Unauthorized`

#### Response de Erro (Token Expirado)

```json
{
  "message": "Failed to fetch user info from Google API: HTTP 401 - Invalid Credentials"
}
```

Status: `401 Unauthorized`

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

## Integração Frontend

### Google Sign-In - Detalhado

O frontend utiliza o Google Identity Services (GIS) para gerenciar o fluxo OAuth2. A implementação está localizada no arquivo `LoginView.vue` e suporta dois métodos de autenticação: Google One Tap e OAuth2 Token Client.

#### Carregamento do Google Identity Services

O script do Google Identity Services é carregado no arquivo `index.html`:

```html
<script src="https://accounts.google.com/gsi/client" async defer></script>
```

O atributo `async defer` garante que o script não bloqueie o carregamento da página.

#### Inicialização do Cliente OAuth2

Antes de usar o Google Identity Services, o frontend aguarda o script carregar completamente:

```javascript
// Aguarda o Google Identity Services carregar
await new Promise((resolve) => {
  if (typeof google !== 'undefined' && google.accounts) {
    resolve()
  } else {
    const checkInterval = setInterval(() => {
      if (typeof google !== 'undefined' && google.accounts) {
        clearInterval(checkInterval)
        resolve()
      }
    }, 100)
    // Timeout de 5 segundos
    setTimeout(() => {
      clearInterval(checkInterval)
      resolve()
    }, 5000)
  }
})
```

**Verificação de Disponibilidade:**
- Verifica se o objeto `google` está disponível
- Verifica se `google.accounts` está disponível
- Aguarda até 5 segundos antes de considerar que falhou

#### Método 1: Google One Tap

O Google One Tap é tentado primeiro. Ele aparece automaticamente na página quando o usuário já está logado no Google.

**Inicialização:**
```javascript
const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID || 'default-client-id'

google.accounts.id.initialize({
  client_id: clientId,
  callback: async (response) => {
    // response.credential contém o ID token (JWT)
    try {
      const apiResponse = await userApi.loginWithGoogle(response.credential)
      const data = apiResponse.data

      if (data.token && data.user) {
        authStore.setAuth(data.token, data.user)
        showMessage('Login with Google successful!')
        router.push('/dashboard')
      }
    } catch (error) {
      const message = error.response?.data?.message || error.message || 'Error logging in with Google'
      showMessage(message, 'error')
    }
  }
})
```

**Callback de Sucesso:**
- `response.credential`: Contém o ID token (JWT) do Google
- O token é enviado diretamente para o backend
- Não requer chamada adicional à API do Google

**Tentativa de Exibição:**
```javascript
google.accounts.id.prompt((notification) => {
  if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
    // One Tap não foi exibido, usar fallback
  }
})
```

**Notificações Possíveis:**
- `isNotDisplayed()`: One Tap não pode ser exibido (usuário não logado, bloqueado, etc.)
- `isSkippedMoment()`: Usuário fechou o One Tap anteriormente
- `isDismissedMoment()`: Usuário fechou o One Tap agora
- `isDisplayed()`: One Tap foi exibido com sucesso
- `isDisplayed()`: One Tap foi exibido e o usuário selecionou uma conta

#### Método 2: OAuth2 Token Client (Fallback)

Se o One Tap não funcionar, o sistema usa o OAuth2 Token Client, que abre um popup para autenticação.

**Inicialização:**
```javascript
google.accounts.oauth2.initTokenClient({
  client_id: clientId,
  scope: 'openid profile email',
  callback: async (tokenResponse) => {
    // tokenResponse.access_token contém o access token
    try {
      // Opção 1: Enviar access token diretamente (backend faz fallback)
      const apiResponse = await userApi.loginWithGoogle(tokenResponse.access_token)
      
      // Opção 2: Buscar informações do usuário e criar ID token mock
      // (não recomendado, apenas para casos especiais)
      
      const data = apiResponse.data

      if (data.token && data.user) {
        authStore.setAuth(data.token, data.user)
        showMessage('Login with Google successful!')
        router.push('/dashboard')
      }
    } catch (error) {
      const message = error.response?.data?.message || error.message || 'Error logging in with Google'
      showMessage(message, 'error')
    }
  }
}).requestAccessToken()
```

**Escopos Solicitados:**
- `openid`: Identificador OpenID Connect
- `profile`: Informações básicas do perfil (nome, foto)
- `email`: Endereço de email do usuário

**Callback de Sucesso:**
- `tokenResponse.access_token`: Contém o access token OAuth2
- O access token é enviado para o backend
- O backend detecta que não é um JWT e faz chamada à API do Google

**Solicitação do Token:**
- `.requestAccessToken()`: Inicia o fluxo OAuth2
- Abre popup do Google para autenticação
- Usuário seleciona conta e concede permissões
- Google retorna o access token no callback

#### Envio para o Backend

O frontend envia o token para o backend através do serviço de API:

**Serviço de API (`api.js`):**
```javascript
loginWithGoogle: (idToken) => {
  return api.post('/users/login/google', toFormData({ idToken }))
}
```

**Formato da Requisição:**
- Método: POST
- Endpoint: `/users/login/google`
- Content-Type: `application/x-www-form-urlencoded`
- Body: `idToken={token}`

**Função Helper:**
```javascript
function toFormData(data) {
  const formData = new URLSearchParams()
  Object.keys(data).forEach(key => {
    formData.append(key, data[key])
  })
  return formData
}
```

#### Tratamento de Resposta

**Sucesso:**
```javascript
if (data.token && data.user) {
  authStore.setAuth(data.token, data.user)
  showMessage('Login with Google successful!')
  router.push('/dashboard')
}
```

- Salva o token e usuário no store de autenticação
- Redireciona para o dashboard
- Exibe mensagem de sucesso

**Erro:**
```javascript
catch (error) {
  const message = error.response?.data?.message || error.message || 'Error logging in with Google'
  showMessage(message, 'error')
}
```

- Extrai mensagem de erro da resposta
- Exibe mensagem de erro ao usuário
- Mantém o usuário na página de login

#### Tratamento de Erros Específicos

**Biblioteca Não Carregada:**
```javascript
if (typeof google === 'undefined' || !google.accounts) {
  showMessage('Google Sign-In library not loaded. Please refresh the page.', 'error')
  return
}
```

**Client ID Não Configurado:**
- O sistema usa um Client ID padrão se `VITE_GOOGLE_CLIENT_ID` não estiver configurado
- Em produção, sempre configure o Client ID correto

**Erro de Rede:**
- Timeout na requisição ao backend
- Erro de CORS
- Backend não disponível

**Token Inválido:**
- Token expirado
- Token malformado
- Token de outro provider

#### Exemplo Completo de Implementação

```javascript
const handleGoogleLogin = async () => {
  googleLoading.value = true
  try {
    // 1. Aguarda Google Identity Services carregar
    await waitForGoogleIdentityServices()

    // 2. Obtém Client ID
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID || 'default-client-id'

    // 3. Inicializa Google Identity Services
    google.accounts.id.initialize({
      client_id: clientId,
      callback: async (response) => {
        // 4. Callback do One Tap
        await handleGoogleToken(response.credential)
      }
    })

    // 5. Tenta exibir One Tap
    google.accounts.id.prompt((notification) => {
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        // 6. Fallback para OAuth2 Token Client
        google.accounts.oauth2.initTokenClient({
          client_id: clientId,
          scope: 'openid profile email',
          callback: async (tokenResponse) => {
            // 7. Callback do OAuth2 Token Client
            await handleGoogleToken(tokenResponse.access_token)
          }
        }).requestAccessToken()
      }
    })
  } catch (error) {
    showMessage(error.message || 'Error initializing Google Sign-In', 'error')
    googleLoading.value = false
  }
}

const handleGoogleToken = async (token) => {
  try {
    const apiResponse = await userApi.loginWithGoogle(token)
    const data = apiResponse.data

    if (data.token && data.user) {
      authStore.setAuth(data.token, data.user)
      showMessage('Login with Google successful!')
      router.push('/dashboard')
    }
  } catch (error) {
    const message = error.response?.data?.message || error.message || 'Error logging in with Google'
    showMessage(message, 'error')
  } finally {
    googleLoading.value = false
  }
}
```

#### Variáveis de Ambiente

Configure o Client ID do Google no arquivo `.env`:

```env
VITE_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

**Importante:**
- Reinicie o servidor de desenvolvimento após adicionar variáveis de ambiente
- Em produção, configure as variáveis no ambiente de hospedagem
- Nunca commite o arquivo `.env` com credenciais reais

### Apple Sign-In

The frontend uses Apple's Sign In JavaScript API:

1. Load the Apple Sign In script
2. Initialize with your Apple Client ID
3. Request sign-in with scopes: `name email`
4. Send the ID token to the backend endpoint `/users/login/apple`

## Configuration

### Configuração do Google OAuth2

Esta seção fornece um guia passo a passo detalhado para configurar o Google OAuth2 no Google Cloud Console e na aplicação.

#### 1. Criar Credenciais no Google Cloud Console

##### Passo 1.1: Acessar o Google Cloud Console

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Faça login com sua conta do Google
3. Se você não tiver um projeto, será necessário criar um

##### Passo 1.2: Criar ou Selecionar um Projeto

1. No topo da página, clique no seletor de projetos
2. Clique em **New Project** para criar um novo projeto, ou selecione um projeto existente
3. Se criando novo projeto:
   - **Project name**: Digite um nome descritivo (ex: "Orion Users")
   - **Organization**: Selecione sua organização (opcional)
   - **Location**: Selecione a localização (opcional)
   - Clique em **Create**

##### Passo 1.3: Configurar a Tela de Consentimento OAuth

Antes de criar as credenciais OAuth, você precisa configurar a tela de consentimento:

1. No menu lateral, navegue até **APIs & Services** > **OAuth consent screen**
2. Selecione o tipo de usuário:
   - **External**: Para usuários fora da sua organização (recomendado para aplicações públicas)
   - **Internal**: Apenas para usuários da sua organização (requer Google Workspace)
3. Preencha as informações obrigatórias:
   - **App name**: Nome da sua aplicação (ex: "Orion Users")
   - **User support email**: Seu email de suporte
   - **Developer contact information**: Seu email (obrigatório)
4. Clique em **Save and Continue**
5. Na seção **Scopes**, adicione os escopos necessários:
   - Clique em **Add or Remove Scopes**
   - Selecione os seguintes escopos:
     - `openid` (OpenID Connect)
     - `.../auth/userinfo.email` (Ver endereço de email)
     - `.../auth/userinfo.profile` (Ver informações básicas do perfil)
   - Clique em **Update** e depois **Save and Continue**
6. Na seção **Test users** (apenas para apps em modo de teste):
   - Adicione emails de usuários que podem testar a aplicação
   - Clique em **Save and Continue**
7. Revise as informações e clique em **Back to Dashboard**

**Importante**: Apps em modo de teste só podem ser usados por até 100 usuários de teste. Para produção, você precisará submeter a aplicação para verificação do Google.

##### Passo 1.4: Criar OAuth Client ID

1. No menu lateral, navegue até **APIs & Services** > **Credentials**
2. Clique em **+ Create Credentials** > **OAuth client ID**
3. Se solicitado, selecione **Web application** como tipo de aplicação
4. Configure as credenciais:

   **Name**: 
   - Digite um nome descritivo (ex: "Orion Users Web Client")
   
   **Authorized JavaScript origins**:
   - Adicione as origens JavaScript autorizadas:
     - Para desenvolvimento: `http://localhost:3000`
     - Para desenvolvimento (Vite): `http://localhost:5173`
     - Para produção: `https://your-domain.com`
     - Para produção (se usar subdomínio): `https://app.your-domain.com`
   
   **Authorized redirect URIs**:
   - Adicione os URIs de redirecionamento autorizados:
     - Para desenvolvimento: `http://localhost:3000`
     - Para desenvolvimento (Vite): `http://localhost:5173`
     - Para produção: `https://your-domain.com`
     - Para produção (se usar subdomínio): `https://app.your-domain.com`
   
   **Nota**: Para Google Identity Services, os redirect URIs podem ser os mesmos que as origens JavaScript, pois o Google Identity Services não usa redirects tradicionais.

5. Clique em **Create**
6. Uma janela popup aparecerá com suas credenciais:
   - **Client ID**: Copie este valor (formato: `xxxxx.apps.googleusercontent.com`)
   - **Client secret**: Não é necessário para Google Identity Services (pode ser ignorado)

**Importante**: Guarde o Client ID com segurança. Você precisará dele para configurar o frontend e backend.

##### Passo 1.5: Habilitar APIs Necessárias (Opcional)

Embora não seja estritamente necessário, você pode habilitar APIs relacionadas:

1. Navegue até **APIs & Services** > **Library**
2. Procure e habilite (se necessário):
   - **Google+ API** (deprecated, mas ainda pode ser útil)
   - **People API** (alternativa moderna)

#### 2. Configurar o Backend

Adicione a configuração do Google OAuth2 no arquivo `src/main/resources/application.properties`:

```properties
# Google OAuth2 Client ID
social.auth.google.client-id=YOUR_GOOGLE_CLIENT_ID_HERE
```

**Exemplo:**
```properties
social.auth.google.client-id=[Google Client ID]
```

**Notas:**
- Substitua `YOUR_GOOGLE_CLIENT_ID_HERE` pelo Client ID obtido no Google Cloud Console
- O Client ID deve estar no formato `xxxxx.apps.googleusercontent.com`
- Não inclua espaços ou caracteres extras
- Em produção, considere usar variáveis de ambiente ou um sistema de gerenciamento de configuração

**Configuração por Ambiente:**

Para diferentes ambientes, você pode usar perfis do Quarkus:

```properties
# Desenvolvimento
%dev.social.auth.google.client-id=dev-client-id.apps.googleusercontent.com

# Teste
%test.social.auth.google.client-id=test-client-id.apps.googleusercontent.com

# Produção
%prod.social.auth.google.client-id=prod-client-id.apps.googleusercontent.com
```

#### 3. Configurar o Frontend

##### Passo 3.1: Criar Arquivo de Variáveis de Ambiente

Crie ou edite o arquivo `.env` na raiz do projeto frontend (`src/main/resources/META-INF/resources/playground/`):

```env
VITE_GOOGLE_CLIENT_ID=YOUR_GOOGLE_CLIENT_ID_HERE
```

**Exemplo:**
```env
VITE_GOOGLE_CLIENT_ID=[Google Client ID]
```

**Importante:**
- O prefixo `VITE_` é necessário para que o Vite exponha a variável ao código do frontend
- Não use aspas ao redor do valor
- Não adicione espaços antes ou depois do `=`
- Reinicie o servidor de desenvolvimento após adicionar/modificar variáveis de ambiente

##### Passo 3.2: Verificar Script do Google Identity Services

O script do Google Identity Services já está incluído no arquivo `index.html`:

```html
<script src="https://accounts.google.com/gsi/client" async defer></script>
```

**Verificações:**
- O script deve estar no `<head>` do documento
- Os atributos `async defer` garantem que o script não bloqueie o carregamento da página
- O script carrega de `https://accounts.google.com/gsi/client`

##### Passo 3.3: Configurar Variáveis de Ambiente por Ambiente

**Desenvolvimento:**
```env
# .env.development
VITE_GOOGLE_CLIENT_ID=dev-client-id.apps.googleusercontent.com
VITE_API_URL=http://localhost:8080
```

**Produção:**
```env
# .env.production
VITE_GOOGLE_CLIENT_ID=prod-client-id.apps.googleusercontent.com
VITE_API_URL=https://api.your-domain.com
```

**Nota**: Em produção, configure as variáveis de ambiente no seu provedor de hospedagem (Vercel, Netlify, etc.) ou no servidor.

#### 4. Verificar Configuração

##### Passo 4.1: Verificar Configuração do Backend

1. Verifique se o arquivo `application.properties` contém o Client ID correto
2. Reinicie o servidor backend se necessário
3. Verifique os logs do backend para erros de configuração

##### Passo 4.2: Verificar Configuração do Frontend

1. Verifique se o arquivo `.env` existe e contém `VITE_GOOGLE_CLIENT_ID`
2. Reinicie o servidor de desenvolvimento (`npm run dev`)
3. Abra o console do navegador e verifique:
   - Não deve haver erros relacionados ao Google Identity Services
   - O objeto `google` deve estar disponível após o carregamento da página

##### Passo 4.3: Testar Integração

1. Acesse a página de login
2. Verifique se o botão "Login with Google" está visível
3. Clique no botão "Login with Google"
4. Deve aparecer:
   - **One Tap**: Um popup do Google com contas sugeridas (se o usuário estiver logado no Google)
   - **OAuth2 Popup**: Um popup do Google para seleção de conta e autenticação
5. Após autenticar, você deve ser redirecionado para o dashboard

##### Passo 4.4: Verificar Logs

**Console do Navegador:**
- Abra as ferramentas de desenvolvedor (F12)
- Vá para a aba Console
- Verifique se há erros relacionados ao Google Identity Services

**Logs do Backend:**
- Verifique os logs do servidor backend
- Procure por erros relacionados à validação de tokens
- Verifique se as requisições estão chegando ao endpoint `/users/login/google`

#### 5. Configuração para Produção

##### Passo 5.1: Verificar Tela de Consentimento

1. No Google Cloud Console, vá para **APIs & Services** > **OAuth consent screen**
2. Verifique se a aplicação está em modo de produção (não "Testing")
3. Se ainda estiver em modo de teste, você precisará:
   - Preencher todas as informações obrigatórias
   - Adicionar políticas de privacidade e termos de serviço
   - Submeter a aplicação para verificação do Google

##### Passo 5.2: Configurar Domínios Autorizados

1. No Google Cloud Console, vá para **APIs & Services** > **Credentials**
2. Edite seu OAuth Client ID
3. Adicione todos os domínios de produção em **Authorized JavaScript origins**:
   - `https://your-domain.com`
   - `https://www.your-domain.com` (se usar www)
   - `https://app.your-domain.com` (se usar subdomínio)
4. Adicione os mesmos domínios em **Authorized redirect URIs**

##### Passo 5.3: Configurar Variáveis de Ambiente em Produção

**Vercel:**
1. Vá para Settings > Environment Variables
2. Adicione `VITE_GOOGLE_CLIENT_ID` com o valor do Client ID de produção
3. Selecione os ambientes (Production, Preview, Development)
4. Faça deploy novamente

**Netlify:**
1. Vá para Site settings > Environment variables
2. Adicione `VITE_GOOGLE_CLIENT_ID` com o valor do Client ID de produção
3. Selecione os contextos (Production, Deploy previews, Branch deploys)
4. Faça deploy novamente

**Docker:**
```dockerfile
ENV VITE_GOOGLE_CLIENT_ID=prod-client-id.apps.googleusercontent.com
```

**Servidor Tradicional:**
Configure as variáveis de ambiente no servidor web (nginx/apache) ou no sistema operacional.

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

Esta seção fornece soluções para problemas comuns ao configurar e usar o Google Sign-In.

#### Google Sign-In não está funcionando

##### Problema: Botão "Login with Google" não aparece

**Possíveis Causas:**
1. Script do Google Identity Services não carregou
2. Client ID não configurado ou incorreto
3. Erro de JavaScript no console

**Soluções:**
1. Verifique se o script está no `index.html`:
   ```html
   <script src="https://accounts.google.com/gsi/client" async defer></script>
   ```
2. Abra o console do navegador (F12) e verifique:
   - Se há erros relacionados ao Google Identity Services
   - Se o objeto `google` está disponível: `console.log(typeof google)`
3. Verifique se `VITE_GOOGLE_CLIENT_ID` está configurado no `.env`
4. Reinicie o servidor de desenvolvimento após adicionar variáveis de ambiente

##### Problema: Erro "Google Sign-In library not loaded"

**Causa:** O script do Google Identity Services não carregou a tempo.

**Soluções:**
1. Verifique sua conexão com a internet
2. Verifique se há bloqueadores de anúncio/script bloqueando `accounts.google.com`
3. Tente recarregar a página
4. Verifique o console do navegador para erros de rede
5. Se o problema persistir, aumente o timeout no código:
   ```javascript
   setTimeout(() => {
     clearInterval(checkInterval)
     resolve()
   }, 10000) // Aumentar de 5000 para 10000
   ```

##### Problema: Popup do Google não abre

**Possíveis Causas:**
1. Bloqueador de popup ativo
2. Domínios não autorizados no Google Cloud Console
3. Client ID incorreto

**Soluções:**
1. Desabilite bloqueadores de popup para o seu domínio
2. Verifique no Google Cloud Console se o domínio está em **Authorized JavaScript origins**:
   - Desenvolvimento: `http://localhost:3000` ou `http://localhost:5173`
   - Produção: `https://your-domain.com`
3. Verifique se o Client ID está correto no `.env`
4. Verifique o console do navegador para erros específicos

##### Problema: Erro "Invalid Google token: Token is empty"

**Causa:** O token não está sendo enviado corretamente para o backend.

**Soluções:**
1. Verifique o código do frontend que envia o token:
   ```javascript
   await userApi.loginWithGoogle(response.credential) // Para ID token
   // ou
   await userApi.loginWithGoogle(tokenResponse.access_token) // Para access token
   ```
2. Verifique se o callback está sendo executado corretamente
3. Adicione logs para debug:
   ```javascript
   console.log('Token recebido:', response.credential)
   ```
4. Verifique a requisição no Network tab das ferramentas de desenvolvedor

##### Problema: Erro "Invalid Google token: Token is empty" no backend

**Causa:** O backend está recebendo um token vazio ou malformado.

**Soluções:**
1. Verifique se o token está sendo enviado no formato correto:
   - Content-Type: `application/x-www-form-urlencoded`
   - Parâmetro: `idToken={token}`
2. Verifique os logs do backend para ver o que está sendo recebido
3. Teste manualmente com curl:
   ```bash
   curl -X POST \
     'http://localhost:8080/users/login/google' \
     --header 'Content-Type: application/x-www-form-urlencoded' \
     --data-urlencode 'idToken=SEU_TOKEN_AQUI'
   ```

##### Problema: Erro "Failed to fetch user info from Google API: HTTP 401"

**Causa:** O access token está inválido ou expirado.

**Soluções:**
1. O token pode ter expirado. Tente fazer login novamente
2. Verifique se o token está sendo usado imediatamente após ser obtido
3. Verifique se os escopos solicitados estão corretos: `openid profile email`
4. Teste o access token manualmente:
   ```bash
   curl -H "Authorization: Bearer SEU_ACCESS_TOKEN" \
     https://www.googleapis.com/oauth2/v2/userinfo
   ```

##### Problema: Erro de CORS

**Causa:** O backend não está configurado para aceitar requisições do frontend.

**Soluções:**
1. Verifique a configuração CORS no backend
2. Adicione o domínio do frontend às origens permitidas
3. Em desenvolvimento, verifique se o proxy do Vite está configurado corretamente
4. Verifique os headers CORS na resposta do backend

##### Problema: Client ID incorreto

**Sintomas:**
- Erro no console: "Invalid client_id"
- Popup do Google não abre
- Erro de autenticação

**Soluções:**
1. Verifique se o Client ID está correto no `.env`:
   ```env
   VITE_GOOGLE_CLIENT_ID=seu-client-id.apps.googleusercontent.com
   ```
2. Verifique se não há espaços extras ou caracteres inválidos
3. Copie o Client ID diretamente do Google Cloud Console
4. Reinicie o servidor de desenvolvimento após alterar o `.env`

##### Problema: Domínios não autorizados

**Sintomas:**
- Erro: "Error 400: redirect_uri_mismatch"
- Popup do Google não abre
- Erro após autenticação

**Soluções:**
1. No Google Cloud Console, vá para **APIs & Services** > **Credentials**
2. Edite seu OAuth Client ID
3. Verifique se o domínio está em **Authorized JavaScript origins**:
   - Desenvolvimento: `http://localhost:3000`, `http://localhost:5173`
   - Produção: `https://your-domain.com`
4. Verifique se o domínio está em **Authorized redirect URIs** (mesmos valores)
5. Salve as alterações e aguarde alguns minutos para propagação

##### Problema: HTTPS necessário em produção

**Causa:** O Google requer HTTPS em produção (exceto localhost).

**Soluções:**
1. Configure HTTPS no seu servidor de produção
2. Use um certificado SSL válido (Let's Encrypt, Cloudflare, etc.)
3. Configure o servidor para redirecionar HTTP para HTTPS
4. Atualize as URLs no Google Cloud Console para usar `https://`

##### Problema: Token inválido ou expirado

**Sintomas:**
- Erro 401 após tentar fazer login
- Mensagem: "Invalid Google token" ou "Token expired"

**Soluções:**
1. Tokens do Google têm tempo de vida limitado
2. Tente fazer login novamente para obter um novo token
3. Verifique se o token está sendo usado imediatamente após ser obtido
4. Para ID tokens, verifique o campo `exp` no payload:
   ```javascript
   const payload = JSON.parse(atob(token.split('.')[1]))
   console.log('Expira em:', new Date(payload.exp * 1000))
   ```

##### Problema: Email não encontrado no token

**Causa:** O usuário não concedeu permissão para acessar o email, ou o escopo não foi solicitado.

**Soluções:**
1. Verifique se o escopo `email` está sendo solicitado:
   ```javascript
   scope: 'openid profile email'
   ```
2. Peça ao usuário para fazer login novamente e conceder permissões
3. Verifique se o email está disponível no token:
   ```javascript
   const payload = JSON.parse(atob(token.split('.')[1]))
   console.log('Email:', payload.email)
   ```

#### Como Debugar Problemas

##### 1. Verificar Console do Navegador

1. Abra as ferramentas de desenvolvedor (F12)
2. Vá para a aba **Console**
3. Procure por erros relacionados ao Google Identity Services
4. Verifique se o objeto `google` está disponível:
   ```javascript
   console.log(typeof google)
   console.log(google?.accounts)
   ```

##### 2. Verificar Network Tab

1. Abra as ferramentas de desenvolvedor (F12)
2. Vá para a aba **Network**
3. Filtre por "google" ou "login"
4. Verifique as requisições:
   - Requisição para `/users/login/google`
   - Status da resposta
   - Corpo da requisição e resposta

##### 3. Verificar Logs do Backend

1. Verifique os logs do servidor backend
2. Procure por erros relacionados à validação de tokens
3. Verifique se as requisições estão chegando ao endpoint correto
4. Adicione logs adicionais se necessário:
   ```kotlin
   println("Token recebido: ${token.take(20)}...")
   ```

##### 4. Validar Token Manualmente

**Para ID Token (JWT):**
```bash
# Extrair e decodificar o payload
echo "SEU_TOKEN_AQUI" | cut -d. -f2 | base64 -d | jq
```

**Para Access Token:**
```bash
# Testar acesso à API do Google
curl -H "Authorization: Bearer SEU_ACCESS_TOKEN" \
  https://www.googleapis.com/oauth2/v2/userinfo
```

##### 5. Testar com cURL

```bash
# Testar endpoint do backend diretamente
curl -X POST \
  'http://localhost:8080/users/login/google' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=SEU_TOKEN_AQUI'
```

##### 6. Verificar Configuração

1. Verifique se todas as variáveis de ambiente estão configuradas
2. Verifique se o Client ID está correto em ambos frontend e backend
3. Verifique se os domínios estão autorizados no Google Cloud Console
4. Verifique se os escopos estão corretos

#### Problemas Comuns e Soluções Rápidas

| Problema | Solução Rápida |
|----------|----------------|
| Botão não aparece | Verificar script no `index.html` e Client ID no `.env` |
| Popup não abre | Desabilitar bloqueador de popup e verificar domínios autorizados |
| Token inválido | Fazer login novamente para obter novo token |
| Erro 401 | Verificar Client ID e domínios autorizados |
| Erro CORS | Verificar configuração CORS no backend |
| Email não encontrado | Verificar escopos solicitados (`email`) |
| HTTPS necessário | Configurar HTTPS em produção |
| Token expirado | Fazer login novamente |

#### Obter Ajuda Adicional

Se os problemas persistirem:

1. Verifique a [documentação oficial do Google Identity Services](https://developers.google.com/identity/gsi/web)
2. Verifique os [fóruns do Google Cloud](https://cloud.google.com/support)
3. Revise os logs completos do backend e frontend
4. Teste em um ambiente limpo (novo projeto, novas credenciais)
5. Verifique se há atualizações disponíveis para as bibliotecas usadas

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

## Validação de Tokens do Google

O backend implementa uma validação inteligente de tokens do Google que suporta tanto ID tokens (JWT) quanto access tokens. O processo de validação está implementado no método `validateGoogleToken()` do arquivo `SocialAuthWS.kt`.

### Processo de Validação

#### 1. Normalização do Token

Primeiro, o backend normaliza o token recebido:

```kotlin
val normalizedToken = token.trim().replace("\\s+".toRegex(), "")
```

- Remove espaços em branco no início e fim
- Remove espaços extras no meio do token
- Valida que o token não está vazio

#### 2. Tentativa de Validação como JWT (ID Token)

O backend primeiro tenta validar o token como um JWT (ID token):

**Verificação de Formato:**
- O token deve ter exatamente 3 partes separadas por pontos (`.`)
- Formato esperado: `header.payload.signature`
- Cada parte não pode estar vazia

**Decodificação do Payload:**
```kotlin
val payload = String(java.util.Base64.getUrlDecoder().decode(parts[1]))
```

- Decodifica a segunda parte (payload) usando base64url
- Se a decodificação falhar, o token não é um JWT válido

**Parse do JSON:**
```kotlin
val json = ObjectMapper().readTree(payload)
```

- Converte o payload decodificado em um objeto JSON
- Se o parse falhar, o token não é um JWT válido

**Extração de Informações:**
```kotlin
val email = json.get("email")?.asText()
val name = json.get("name")?.asText()
    ?: json.get("given_name")?.asText()?.plus(" ").plus(json.get("family_name")?.asText() ?: "")
    ?: email
```

- Extrai o email do campo `email`
- Extrai o nome na seguinte ordem de prioridade:
  1. Campo `name` (nome completo)
  2. Combinação de `given_name` + `family_name`
  3. Email como fallback

**Campos Esperados no JWT:**
- `email`: Email do usuário (obrigatório)
- `name`: Nome completo (opcional)
- `given_name`: Primeiro nome (opcional)
- `family_name`: Sobrenome (opcional)
- `email_verified`: Indica se o email foi verificado (opcional)
- `iss`: Issuer (deve ser `https://accounts.google.com`)
- `exp`: Timestamp de expiração (não validado atualmente)
- `iat`: Timestamp de emissão (não validado atualmente)

#### 3. Fallback para Access Token

Se o token não for um JWT válido, o backend assume que é um access token e faz uma chamada à API do Google:

**Chamada à API:**
```kotlin
webClient.get(443, "www.googleapis.com", "/oauth2/v2/userinfo")
    .ssl(true)
    .putHeader("Authorization", "Bearer $accessToken")
    .send()
```

- Endpoint: `https://www.googleapis.com/oauth2/v2/userinfo`
- Método: GET
- Autenticação: Bearer token no header Authorization
- SSL: Habilitado (HTTPS)

**Validação da Resposta:**
- Status HTTP 200: Sucesso
- Outros status: Erro (401 = token inválido/expirado, 403 = sem permissão, etc.)

**Extração de Informações:**
```kotlin
val email = json.get("email")?.asText()
val name = json.get("name")?.asText()
    ?: json.get("given_name")?.asText()?.plus(" ").plus(json.get("family_name")?.asText() ?: "")
    ?: email
```

- Mesma lógica de extração usada para JWT
- Campos esperados na resposta da API são os mesmos

**Campos Esperados na Resposta da API:**
- `email`: Email do usuário (obrigatório)
- `verified_email`: Indica se o email foi verificado
- `name`: Nome completo (opcional)
- `given_name`: Primeiro nome (opcional)
- `family_name`: Sobrenome (opcional)
- `picture`: URL da foto de perfil (opcional)
- `locale`: Idioma preferido (opcional)

### Tratamento de Erros

O backend trata diferentes tipos de erros:

**Token Vazio:**
```kotlin
throw IllegalArgumentException("Invalid Google token: Token is empty")
```

**Token com Formato Inválido:**
- Se não for possível decodificar como JWT e a chamada à API falhar
- Mensagem: "Invalid Google token: {detalhes do erro}"

**Falha na API do Google:**
```kotlin
throw IllegalArgumentException("Failed to fetch user info from Google API: HTTP {status} - {errorBody}")
```

**Email Não Encontrado:**
```kotlin
throw IllegalArgumentException("Email not found in Google API response")
```

### Validação em Produção (Recomendado)

A implementação atual fornece validação básica adequada para desenvolvimento. Para produção, recomenda-se implementar:

1. **Validação de Assinatura do JWT:**
   - Baixar as chaves públicas do Google de `https://www.googleapis.com/oauth2/v3/certs`
   - Validar a assinatura do token usando a chave pública correspondente
   - Garantir que o token foi realmente emitido pelo Google

2. **Validação de Expiração:**
   - Verificar o campo `exp` (expiration) do JWT
   - Rejeitar tokens expirados

3. **Validação do Issuer:**
   - Verificar que o campo `iss` (issuer) é `https://accounts.google.com`
   - Prevenir tokens de outros emissores

4. **Validação do Audience:**
   - Verificar que o campo `aud` (audience) corresponde ao Client ID configurado
   - Garantir que o token foi emitido para sua aplicação

5. **Validação de Nonce:**
   - Implementar validação de nonce para prevenir replay attacks
   - Gerar nonce único para cada requisição e validar no token

### Exemplo de Validação Manual de Token

Para testar manualmente um ID token do Google:

**1. Decodificar o Token:**
```bash
# Extrair o payload (segunda parte do JWT)
echo "eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20i..." | cut -d. -f2 | base64 -d
```

**2. Verificar Campos Importantes:**
```json
{
  "iss": "https://accounts.google.com",
  "email": "user@example.com",
  "email_verified": true,
  "exp": 1234571490,
  "iat": 1234567890
}
```

**3. Verificar Expiração:**
```bash
# Converter timestamp Unix para data legível
date -d @1234571490
```

**4. Testar Access Token:**
```bash
curl -H "Authorization: Bearer ya29.a0AfH6SMBx..." \
  https://www.googleapis.com/oauth2/v2/userinfo
```

## Exceptions

* HTTP 401 (Unauthorized): If the token is invalid or expired
* HTTP 400 (Bad Request): If the request is malformed or missing required parameters

## Exemplos Práticos Completos

### Exemplo 1: Implementação Completa no Frontend

Este exemplo mostra uma implementação completa do Google Sign-In no frontend Vue.js:

```vue
<template>
  <v-btn
    color="primary"
    @click="handleGoogleLogin"
    :loading="googleLoading"
    prepend-icon="mdi-google"
  >
    Login with Google
  </v-btn>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/services/api'

const router = useRouter()
const authStore = useAuthStore()
const googleLoading = ref(false)

const waitForGoogleIdentityServices = () => {
  return new Promise((resolve, reject) => {
    if (typeof google !== 'undefined' && google.accounts) {
      resolve()
      return
    }
    
    let attempts = 0
    const maxAttempts = 50 // 5 segundos
    const checkInterval = setInterval(() => {
      attempts++
      if (typeof google !== 'undefined' && google.accounts) {
        clearInterval(checkInterval)
        resolve()
      } else if (attempts >= maxAttempts) {
        clearInterval(checkInterval)
        reject(new Error('Google Identity Services failed to load'))
      }
    }, 100)
  })
}

const handleGoogleLogin = async () => {
  googleLoading.value = true
  try {
    // 1. Aguarda Google Identity Services carregar
    await waitForGoogleIdentityServices()

    // 2. Obtém Client ID
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
    if (!clientId) {
      throw new Error('Google Client ID not configured')
    }

    // 3. Inicializa Google Identity Services
    google.accounts.id.initialize({
      client_id: clientId,
      callback: async (response) => {
        // 4. Callback do One Tap - ID token recebido
        await handleGoogleToken(response.credential)
      }
    })

    // 5. Tenta exibir One Tap
    google.accounts.id.prompt((notification) => {
      if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
        // 6. Fallback para OAuth2 Token Client
        google.accounts.oauth2.initTokenClient({
          client_id: clientId,
          scope: 'openid profile email',
          callback: async (tokenResponse) => {
            // 7. Callback do OAuth2 - access token recebido
            await handleGoogleToken(tokenResponse.access_token)
          }
        }).requestAccessToken()
      }
    })
  } catch (error) {
    console.error('Google Sign-In error:', error)
    alert(error.message || 'Error initializing Google Sign-In')
    googleLoading.value = false
  }
}

const handleGoogleToken = async (token) => {
  try {
    // 8. Envia token para o backend
    const apiResponse = await userApi.loginWithGoogle(token)
    const data = apiResponse.data

    // 9. Verifica resposta e autentica usuário
    if (data.token && data.user) {
      authStore.setAuth(data.token, data.user)
      router.push('/dashboard')
    } else {
      throw new Error('Invalid response from server')
    }
  } catch (error) {
    console.error('Authentication error:', error)
    const message = error.response?.data?.message || error.message || 'Error logging in with Google'
    alert(message)
  } finally {
    googleLoading.value = false
  }
}
</script>
```

### Exemplo 2: Testando o Endpoint com cURL

#### Teste com ID Token (JWT)

```bash
# 1. Obter um ID token do Google (use o console do navegador após fazer login)
# O token estará em: response.credential

# 2. Testar o endpoint
curl -X POST \
  'http://localhost:8080/users/login/google' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzQ1NiJ9.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJzdWIiOiIxMjM0NTY3ODkwIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJKb2huIERvZSIsInBpY3R1cmUiOiJodHRwczovLy4uLiIsImlhdCI6MTIzNDU2Nzg5MCwiZXhwIjoxMjM0NTcxNDkwfQ.signature'

# 3. Resposta esperada
# {
#   "user": {
#     "hash": "...",
#     "name": "John Doe",
#     "email": "user@example.com",
#     "emailValid": true,
#     "secret2FA": null,
#     "using2FA": false
#   },
#   "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9..."
# }
```

#### Teste com Access Token

```bash
# 1. Obter um access token do Google (use o console do navegador)
# O token estará em: tokenResponse.access_token

# 2. Testar o endpoint
curl -X POST \
  'http://localhost:8080/users/login/google' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=ya29.a0AfH6SMBx...'

# 3. O backend fará uma chamada à API do Google automaticamente
# Resposta esperada é a mesma do exemplo anterior
```

### Exemplo 3: Decodificando um ID Token Manualmente

```bash
# 1. Extrair o payload do JWT (segunda parte)
TOKEN="eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzQ1NiJ9.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJzdWIiOiIxMjM0NTY3ODkwIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJKb2huIERvZSIsInBpY3R1cmUiOiJodHRwczovLy4uLiIsImlhdCI6MTIzNDU2Nzg5MCwiZXhwIjoxMjM0NTcxNDkwfQ.signature"

# 2. Decodificar o payload
echo $TOKEN | cut -d. -f2 | base64 -d | jq

# 3. Saída esperada:
# {
#   "iss": "https://accounts.google.com",
#   "sub": "1234567890",
#   "email": "user@example.com",
#   "email_verified": true,
#   "name": "John Doe",
#   "picture": "https://...",
#   "iat": 1234567890,
#   "exp": 1234571490
# }

# 4. Verificar expiração
EXP=1234571490
date -d @$EXP
```

### Exemplo 4: Testando Access Token com API do Google

```bash
# 1. Obter um access token (use o console do navegador)

# 2. Testar acesso à API do Google
curl -H "Authorization: Bearer ya29.a0AfH6SMBx..." \
  https://www.googleapis.com/oauth2/v2/userinfo

# 3. Resposta esperada:
# {
#   "id": "1234567890",
#   "email": "user@example.com",
#   "verified_email": true,
#   "name": "John Doe",
#   "given_name": "John",
#   "family_name": "Doe",
#   "picture": "https://...",
#   "locale": "pt-BR"
# }
```

### Exemplo 5: Implementação no Backend (Kotlin)

Este exemplo mostra como o backend valida tokens do Google:

```kotlin
@POST
@Path("/google")
fun loginWithGoogle(
    @RestForm @NotEmpty idToken: String
): Uni<Response> {
    return validateGoogleToken(idToken)
        .onItem().transform { (email, name) ->
            // Busca ou cria usuário
            controller.loginWithSocialProvider(email, name, "google")
        }
        .onItem().transformToUni { authUni ->
            authUni.onItem().transform { dto ->
                Response.ok(dto).build()
            }
        }
        .onFailure().transform { e ->
            ServiceException(
                e.message ?: "Google authentication failed",
                Response.Status.UNAUTHORIZED
            )
        }
}

private fun validateGoogleToken(token: String): Uni<Pair<String, String>> {
    val normalizedToken = token.trim().replace("\\s+".toRegex(), "")
    
    // Tenta validar como JWT primeiro
    val jwtResult = tryValidateAsJWT(normalizedToken)
    if (jwtResult != null) {
        return jwtResult
    }
    
    // Fallback para access token
    return fetchUserInfoFromGoogleAPI(normalizedToken)
}

private fun tryValidateAsJWT(token: String): Uni<Pair<String, String>>? {
    return try {
        val parts = token.split(".")
        if (parts.size != 3) return null
        
        val payload = String(Base64.getUrlDecoder().decode(parts[1]))
        val json = ObjectMapper().readTree(payload)
        
        val email = json.get("email")?.asText() ?: return null
        val name = json.get("name")?.asText()
            ?: json.get("given_name")?.asText()?.plus(" ")
                .plus(json.get("family_name")?.asText() ?: "")
            ?: email
        
        Uni.createFrom().item(Pair(email, name))
    } catch (e: Exception) {
        null
    }
}

private fun fetchUserInfoFromGoogleAPI(accessToken: String): Uni<Pair<String, String>> {
    val future = webClient.get(443, "www.googleapis.com", "/oauth2/v2/userinfo")
        .ssl(true)
        .putHeader("Authorization", "Bearer $accessToken")
        .send()
    
    return Uni.createFrom().completionStage(future.toCompletionStage())
        .onItem().transform { response ->
            if (response.statusCode() != 200) {
                throw IllegalArgumentException("Failed to fetch user info: HTTP ${response.statusCode()}")
            }
            
            val json = ObjectMapper().readTree(response.bodyAsString())
            val email = json.get("email")?.asText()
                ?: throw IllegalArgumentException("Email not found")
            
            val name = json.get("name")?.asText()
                ?: json.get("given_name")?.asText()?.plus(" ")
                    .plus(json.get("family_name")?.asText() ?: "")
                ?: email
            
            Pair(email, name)
        }
}
```

### Exemplo 6: Configuração Completa do Ambiente

#### Arquivo `.env` (Desenvolvimento)

```env
# Backend API
VITE_API_URL=http://localhost:8080

# Google OAuth2
VITE_GOOGLE_CLIENT_ID=[Google Client ID]

# Apple OAuth2 (opcional)
VITE_APPLE_CLIENT_ID=com.yourcompany.orionusers
```

#### Arquivo `application.properties` (Backend)

```properties
# Google OAuth2 Client ID
social.auth.google.client-id=[Google Client ID]

# Outras configurações...
quarkus.http.port=8080
```

#### Arquivo `index.html` (Frontend)

```html
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <meta charset="UTF-8">
    <title>Orion Users - Login</title>
    <!-- Google Identity Services -->
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <!-- Apple Sign In -->
    <script type="text/javascript" 
            src="https://appleid.cdn-apple.com/appleauth/static/jsapi/appleid/1/en_US/appleid.auth.js" 
            async></script>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

## Security Considerations

### Validação de Tokens em Produção

A implementação atual fornece validação básica adequada para desenvolvimento. Para produção, implemente:

1. **Validação de Assinatura do JWT:**
   - Baixe as chaves públicas do Google de `https://www.googleapis.com/oauth2/v3/certs`
   - Valide a assinatura do token usando a chave pública correspondente
   - Garanta que o token foi realmente emitido pelo Google

2. **Validação de Expiração:**
   - Verifique o campo `exp` (expiration) do JWT
   - Rejeite tokens expirados

3. **Validação do Issuer:**
   - Verifique que o campo `iss` (issuer) é `https://accounts.google.com`
   - Previna tokens de outros emissores

4. **Validação do Audience:**
   - Verifique que o campo `aud` (audience) corresponde ao Client ID configurado
   - Garanta que o token foi emitido para sua aplicação

5. **Validação de Nonce:**
   - Implemente validação de nonce para prevenir replay attacks
   - Gere nonce único para cada requisição e valide no token

### Boas Práticas de Segurança

* **Sempre use HTTPS em produção** - O Google requer HTTPS (exceto localhost)
* **Valide assinaturas de tokens** - Não confie apenas na decodificação do payload
* **Verifique expiração de tokens** - Tokens expirados devem ser rejeitados
* **Verifique o issuer** - Garanta que o token veio do Google
* **Use variáveis de ambiente** - Nunca hardcode credenciais no código
* **Monitore tentativas de autenticação** - Logue tentativas falhadas para detectar abusos
* **Implemente rate limiting** - Prevenha abuso do endpoint de autenticação
* **Mantenha dependências atualizadas** - Bibliotecas de segurança devem estar atualizadas
* **Use Content Security Policy (CSP)** - Configure CSP para prevenir XSS
* **Valide todos os inputs** - Não confie em dados do cliente sem validação
* **Armazene secrets com segurança** - Use sistemas de gerenciamento de secrets (AWS Secrets Manager, HashiCorp Vault, etc.)
* **Nunca exponha Client Secrets** - Client secrets nunca devem estar no código do frontend
* **Implemente logging adequado** - Logue eventos de autenticação sem expor tokens
* **Use tokens de curta duração** - Tokens devem expirar rapidamente
* **Implemente refresh tokens** - Use refresh tokens para renovar access tokens sem reautenticação

