---
layout: default
title: Social Authentication
parent: Use Cases
nav_order: 10
---

## Social Authentication

Este caso de uso é responsável por autenticar usuários via provedores sociais (Google) usando OAuth2.

### Fluxo Normal

1. O usuário clica em "Login with Google" no frontend
2. O frontend inicia o fluxo OAuth2 com o Google Identity Services
3. O Google autentica o usuário e retorna um token (ID token ou access token)
4. O frontend envia o token para o backend
5. O backend valida o token e extrai informações do usuário (email, nome)
6. O backend busca o usuário pelo email
7. Se o usuário não existir, o backend cria automaticamente
8. O backend gera um JWT do sistema e retorna AuthenticationDTO com usuário e token

### Criação Automática de Usuário

Quando um usuário autentica via provedor social pela primeira vez:
- Uma nova conta de usuário é criada automaticamente
- O email do usuário é marcado como validado (já verificado pelo provedor)
- Uma senha aleatória é gerada (não usada, mas requerida pelo schema do banco)
- O usuário recebe a role padrão "user"

### Tipos de Tokens Suportados

O sistema suporta dois tipos de tokens do Google:

1. **ID Token (JWT)**: Token de identidade assinado pelo Google que contém informações do usuário diretamente no payload. Método preferido e mais seguro.

2. **Access Token**: Token OAuth2 que permite buscar informações do usuário através da API do Google. Usado como fallback quando o ID token não está disponível.

### Métodos de Autenticação no Frontend

O frontend implementa dois métodos de autenticação:

- **Google One Tap**: Aparece automaticamente na página quando o usuário já está logado no Google. Tenta usar este método primeiro.
- **OAuth2 Token Client**: Fallback que abre um popup para autenticação quando o One Tap não está disponível.

## Configuração

### Como Gerar o Client ID no Google Cloud Console

#### 1. Criar ou Selecionar um Projeto

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Faça login com sua conta do Google
3. No topo da página, clique no seletor de projetos
4. Clique em **New Project** para criar um novo projeto, ou selecione um projeto existente
5. Se criando novo projeto:
   - **Project name**: Digite um nome descritivo (ex: "Orion Users")
   - Clique em **Create**

#### 2. Configurar a Tela de Consentimento OAuth

1. No menu lateral, navegue até **APIs & Services** > **OAuth consent screen**
2. Selecione o tipo de usuário:
   - **External**: Para usuários fora da sua organização (recomendado para aplicações públicas)
   - **Internal**: Apenas para usuários da sua organização (requer Google Workspace)
3. Preencha as informações obrigatórias:
   - **App name**: Nome da sua aplicação
   - **User support email**: Seu email de suporte
   - **Developer contact information**: Seu email
4. Clique em **Save and Continue**
5. Na seção **Scopes**, adicione os escopos necessários:
   - Clique em **Add or Remove Scopes**
   - Selecione: `openid`, `.../auth/userinfo.email`, `.../auth/userinfo.profile`
   - Clique em **Update** e depois **Save and Continue**
6. Na seção **Test users** (apenas para apps em modo de teste):
   - Adicione emails de usuários que podem testar a aplicação
   - Clique em **Save and Continue**
7. Revise as informações e clique em **Back to Dashboard**

**Importante**: Apps em modo de teste só podem ser usados por até 100 usuários de teste. Para produção, você precisará submeter a aplicação para verificação do Google.

#### 3. Criar OAuth Client ID

1. No menu lateral, navegue até **APIs & Services** > **Credentials**
2. Clique em **+ Create Credentials** > **OAuth client ID**
3. Se solicitado, selecione **Web application** como tipo de aplicação
4. Configure as credenciais:

   **Name**: 
   - Digite um nome descritivo (ex: "Orion Users Web Client")
   
   **Authorized JavaScript origins**:
   - Adicione as origens JavaScript autorizadas:
     - Para desenvolvimento: `http://localhost:3000`, `http://localhost:5173`
     - Para produção: `https://your-domain.com`
   
   **Authorized redirect URIs**:
   - Adicione os URIs de redirecionamento autorizados (mesmos valores das origens JavaScript)
   - Para desenvolvimento: `http://localhost:3000`, `http://localhost:5173`
   - Para produção: `https://your-domain.com`

5. Clique em **Create**
6. Uma janela popup aparecerá com suas credenciais:
   - **Client ID**: Copie este valor (formato: `xxxxx.apps.googleusercontent.com`)
   - **Client secret**: Não é necessário para Google Identity Services (pode ser ignorado)

**Importante**: Guarde o Client ID com segurança. Você precisará dele para configurar o frontend e backend.

### Como Configurar no Frontend Playground

#### 1. Criar Arquivo de Variáveis de Ambiente

Crie ou edite o arquivo `.env` no diretório do playground:

**Localização**: `src/main/resources/META-INF/resources/playground/.env`

```env
VITE_GOOGLE_CLIENT_ID=seu-client-id.apps.googleusercontent.com
```

**Importante**:
- O prefixo `VITE_` é necessário para que o Vite exponha a variável ao código do frontend
- Não use aspas ao redor do valor
- Não adicione espaços antes ou depois do `=`
- Substitua `seu-client-id.apps.googleusercontent.com` pelo Client ID obtido no Google Cloud Console

#### 2. Verificar Script do Google Identity Services

O script do Google Identity Services deve estar incluído no arquivo `index.html`:

**Localização**: `src/main/resources/META-INF/resources/playground/index.html`

```html
<script src="https://accounts.google.com/gsi/client" async defer></script>
```

**Verificações**:
- O script deve estar no `<head>` do documento
- Os atributos `async defer` garantem que o script não bloqueie o carregamento da página

#### 3. Reiniciar o Servidor de Desenvolvimento

Após adicionar ou modificar variáveis de ambiente:

1. Pare o servidor de desenvolvimento (se estiver rodando)
2. Reinicie o servidor:
   ```bash
   cd src/main/resources/META-INF/resources/playground
   npm run dev
   ```

#### 4. Testar a Configuração

1. Acesse a página de login
2. Verifique se o botão "Login with Google" está visível
3. Clique no botão "Login with Google"
4. Deve aparecer um popup do Google para seleção de conta e autenticação
5. Após autenticar, você deve ser redirecionado para o dashboard

### Configurar o Backend

Adicione a configuração do Google OAuth2 no arquivo `src/main/resources/application.properties`:

```properties
# Google OAuth2 Client ID
social.auth.google.client-id=seu-client-id.apps.googleusercontent.com
```

**Notas**:
- Substitua `seu-client-id.apps.googleusercontent.com` pelo Client ID obtido no Google Cloud Console
- O Client ID deve estar no formato `xxxxx.apps.googleusercontent.com`
- Não inclua espaços ou caracteres extras
- Reinicie o servidor backend após modificar o arquivo

**Configuração por Ambiente**:

Para diferentes ambientes, você pode usar perfis do Quarkus:

```properties
# Desenvolvimento
%dev.social.auth.google.client-id=dev-client-id.apps.googleusercontent.com

# Teste
%test.social.auth.google.client-id=test-client-id.apps.googleusercontent.com

# Produção
%prod.social.auth.google.client-id=prod-client-id.apps.googleusercontent.com
```

## Implementação

### Frontend

O frontend utiliza o Google Identity Services (GIS) para gerenciar o fluxo OAuth2. A implementação está localizada no arquivo `LoginView.vue`.

#### Fluxo Básico

1. **Aguarda o Google Identity Services carregar**:
   ```javascript
   await waitForGoogleIdentityServices()
   ```

2. **Inicializa o Google Identity Services**:
   ```javascript
   const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
   
   google.accounts.id.initialize({
     client_id: clientId,
     callback: async (response) => {
       // response.credential contém o ID token
       await userApi.loginWithGoogle(response.credential)
     }
   })
   ```

3. **Tenta exibir One Tap**:
   ```javascript
   google.accounts.id.prompt((notification) => {
     if (notification.isNotDisplayed() || notification.isSkippedMoment()) {
       // Fallback para OAuth2 Token Client
       google.accounts.oauth2.initTokenClient({
         client_id: clientId,
         scope: 'openid profile email',
         callback: async (tokenResponse) => {
           // tokenResponse.access_token contém o access token
           await userApi.loginWithGoogle(tokenResponse.access_token)
         }
       }).requestAccessToken()
     }
   })
   ```

4. **Envia token para o backend**:
   ```javascript
   // POST /users/login/google
   // Content-Type: application/x-www-form-urlencoded
   // Body: idToken={token}
   const apiResponse = await userApi.loginWithGoogle(token)
   ```

#### Serviço de API

O frontend envia o token através do serviço de API (`api.js`):

```javascript
loginWithGoogle: (idToken) => {
  return api.post('/users/login/google', toFormData({ idToken }))
}
```

### Backend

O backend valida tokens do Google suportando tanto ID tokens (JWT) quanto access tokens.

#### Processo de Validação

1. **Normalização**: Remove espaços em branco do token

2. **Tentativa de Validação como JWT**:
   - Verifica se o token tem formato JWT (3 partes separadas por pontos)
   - Decodifica o payload (base64url)
   - Extrai email e nome do payload

3. **Fallback para Access Token**:
   - Se não for um JWT válido, assume que é um access token
   - Faz chamada à API do Google: `GET https://www.googleapis.com/oauth2/v2/userinfo`
   - Extrai email e nome da resposta

4. **Criação/Busca do Usuário**:
   - Busca o usuário pelo email
   - Se não existir, cria automaticamente com email validado e role padrão

5. **Geração do JWT do Sistema**:
   - Gera um JWT próprio do sistema
   - Retorna AuthenticationDTO com usuário e token

## API Reference

### Endpoint: Login com Google

**POST** `/users/login/google`

**Content-Type**: `application/x-www-form-urlencoded`

**Parâmetros**:
- `idToken` (String, obrigatório): ID token (JWT) ou access token do Google

**Resposta de Sucesso** (200 OK):

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

**Resposta de Erro** (401 Unauthorized):

```json
{
  "message": "Invalid Google token: Token is empty"
}
```

**Exemplo com cURL**:

```bash
curl -X POST \
  'http://localhost:8080/users/login/google' \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data-urlencode 'idToken=eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzQ1NiJ9...'
```

## Troubleshooting

### Problemas Comuns

| Problema | Solução |
|----------|---------|
| Botão "Login with Google" não aparece | Verificar script no `index.html` e Client ID no `.env` |
| Popup do Google não abre | Desabilitar bloqueador de popup e verificar domínios autorizados no Google Cloud Console |
| Erro "Invalid Google token" | Fazer login novamente para obter novo token |
| Erro 401 | Verificar Client ID e domínios autorizados |
| Erro CORS | Verificar configuração CORS no backend |
| Email não encontrado | Verificar escopos solicitados (`email`) |
| HTTPS necessário | Configurar HTTPS em produção (exceto localhost) |
| Token expirado | Fazer login novamente |

### Verificações Básicas

1. **Console do Navegador** (F12):
   - Verificar se há erros relacionados ao Google Identity Services
   - Verificar se o objeto `google` está disponível: `console.log(typeof google)`

2. **Variáveis de Ambiente**:
   - Verificar se `VITE_GOOGLE_CLIENT_ID` está configurado no `.env`
   - Reiniciar servidor após adicionar/modificar variáveis

3. **Google Cloud Console**:
   - Verificar se os domínios estão em **Authorized JavaScript origins**
   - Verificar se os domínios estão em **Authorized redirect URIs**
   - Desenvolvimento: `http://localhost:3000`, `http://localhost:5173`
   - Produção: `https://your-domain.com`

4. **Backend**:
   - Verificar se `social.auth.google.client-id` está configurado em `application.properties`
   - Verificar logs do backend para erros de validação

## Segurança

### Recomendações para Produção

A implementação atual fornece validação básica adequada para desenvolvimento. Para produção, recomenda-se implementar:

1. **Validação de Assinatura do JWT**:
   - Baixar as chaves públicas do Google de `https://www.googleapis.com/oauth2/v3/certs`
   - Validar a assinatura do token usando a chave pública correspondente

2. **Validação de Expiração**:
   - Verificar o campo `exp` (expiration) do JWT
   - Rejeitar tokens expirados

3. **Validação do Issuer**:
   - Verificar que o campo `iss` (issuer) é `https://accounts.google.com`

4. **Validação do Audience**:
   - Verificar que o campo `aud` (audience) corresponde ao Client ID configurado

5. **Rate Limiting**:
   - Implementar rate limiting para prevenir abuso do endpoint

### Boas Práticas

- **Sempre use HTTPS em produção** - O Google requer HTTPS (exceto localhost)
- **Use variáveis de ambiente** - Nunca hardcode credenciais no código
- **Monitore tentativas de autenticação** - Logue tentativas falhadas para detectar abusos
- **Mantenha dependências atualizadas** - Bibliotecas de segurança devem estar atualizadas
- **Valide todos os inputs** - Não confie em dados do cliente sem validação
- **Nunca exponha Client Secrets** - Client secrets nunca devem estar no código do frontend

## Exceptions

- **HTTP 401 (Unauthorized)**: Se o token for inválido ou expirado
- **HTTP 400 (Bad Request)**: Se a requisição estiver malformada ou faltando parâmetros obrigatórios
