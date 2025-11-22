# Frontend - Orion Users

Aplicação Vue 3 com Vuetify para testar todas as funcionalidades do serviço de usuários.

## Funcionalidades

- ✅ Cadastro de usuários
- ✅ Login simples
- ✅ Autenticação em dois fatores (2FA)
- ✅ WebAuthn (autenticação biométrica/chave de segurança)

## Pré-requisitos

- Node.js 18+ 
- npm ou yarn

## Instalação

```bash
cd frontend
npm install
```

## Configuração

Crie um arquivo `.env` na raiz do projeto `frontend/` com:

```
VITE_API_URL=http://localhost:8080
```

## Executar em desenvolvimento

```bash
npm run dev
```

A aplicação estará disponível em `http://localhost:3000`

## Build para produção

```bash
npm run build
```

Os arquivos serão gerados na pasta `dist/`

## Estrutura do Projeto

```
frontend/
├── src/
│   ├── main.js              # Configuração Vue e Vuetify
│   ├── App.vue              # Componente principal
│   ├── router/
│   │   └── index.js         # Configuração de rotas
│   ├── services/
│   │   └── api.js           # Cliente HTTP e métodos da API
│   ├── stores/
│   │   └── auth.js          # Store Pinia para autenticação
│   └── views/
│       ├── LoginView.vue    # Página de login/cadastro
│       ├── TwoFactorView.vue # Página de 2FA
│       └── WebAuthnView.vue  # Página de WebAuthn
```

## Uso

### Cadastro
1. Acesse a aba "Cadastro" na página inicial
2. Preencha nome, email e senha (mínimo 8 caracteres)
3. Clique em "Cadastrar"

### Login Simples
1. Acesse a aba "Login" na página inicial
2. Preencha email e senha
3. Clique em "Entrar"
4. Se o usuário tiver 2FA habilitado, será redirecionado para a página de validação

### Configurar 2FA
1. Acesse a página de 2FA (`/2fa`)
2. Preencha email e senha
3. Clique em "Gerar QR Code"
4. Escaneie o QR code com um aplicativo autenticador (Google Authenticator, Authy, etc.)
5. Clique em "Já escaneei, validar código"
6. Digite o código de 6 dígitos do aplicativo

### Autenticar com 2FA
1. Após fazer login, se 2FA estiver habilitado, você será redirecionado automaticamente
2. Digite o código de 6 dígitos do seu aplicativo autenticador
3. Clique em "Validar Código"

### WebAuthn - Registrar Dispositivo
1. Acesse a página de WebAuthn (`/webauthn`)
2. Vá para a aba "Registrar Dispositivo"
3. Preencha seu email
4. (Opcional) Digite um nome para o dispositivo
5. Clique em "Registrar Dispositivo"
6. Siga as instruções do navegador para autenticação biométrica ou chave de segurança

### WebAuthn - Autenticar
1. Acesse a página de WebAuthn (`/webauthn`)
2. Vá para a aba "Autenticar"
3. Preencha seu email
4. Clique em "Autenticar com WebAuthn"
5. Siga as instruções do navegador para autenticação biométrica ou chave de segurança

## Notas

- O backend deve estar rodando em `http://localhost:8080` (ou conforme configurado no `.env`)
- WebAuthn requer um navegador moderno (Chrome, Firefox, Edge) e HTTPS em produção
- Para desenvolvimento local, alguns navegadores podem permitir WebAuthn em localhost sem HTTPS

