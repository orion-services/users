# Admin - Gerenciamento de Usuários

Aplicação Vue 3 com Vuetify para gerenciamento administrativo de usuários.

## Estrutura do Projeto

```
admin/
├── src/
│   ├── components/          # Componentes reutilizáveis
│   │   └── DeleteUserDialog.vue
│   ├── router/              # Configuração de rotas
│   │   └── index.js
│   ├── services/           # Serviços de API
│   │   └── api.js
│   ├── stores/             # Stores Pinia
│   │   └── users.js
│   ├── views/              # Views principais
│   │   ├── LoginView.vue
│   │   ├── UsersListView.vue
│   │   ├── UserDetailView.vue
│   │   ├── CreateUserView.vue
│   │   └── EditUserView.vue
│   ├── App.vue             # Componente raiz
│   └── main.js             # Entry point
├── index.html
├── package.json
├── vite.config.js
└── README.md
```

## Funcionalidades

- **Autenticação**: Login com verificação de role admin
- **Listagem de Usuários**: Tabela com todos os usuários, filtros e busca
- **Visualização de Detalhes**: Exibição completa das informações do usuário
- **Criação de Usuários**: Formulário para criar novos usuários
- **Edição de Usuários**: Atualização de e-mail e senha
- **Exclusão de Usuários**: Deletar usuários com confirmação

## Requisitos

- Node.js 18+
- npm ou yarn

## Instalação

```bash
cd src/main/resources/META-INF/resources/admin
npm install
```

## Desenvolvimento

```bash
npm run dev
```

A aplicação estará disponível em `http://localhost:3001/console/`

## Build

```bash
npm run build
```

Os arquivos serão gerados em `target/classes/META-INF/resources/console/`

## Endpoints da API Utilizados

- `GET /users/list` - Lista todos os usuários (admin only)
- `GET /users/by-email?email={email}` - Busca usuário por email (admin only)
- `POST /users/create` - Cria novo usuário
- `PUT /users/update` - Atualiza usuário
- `POST /users/delete` - Deleta usuário (admin only)
- `POST /users/login` - Autenticação

## Autenticação

A aplicação requer autenticação com role `admin` no JWT token. O token é armazenado no `localStorage` e incluído automaticamente nas requisições via interceptor do Axios.

## Notas

- A aplicação está configurada para ser servida em `/console/` pelo Quarkus
- Em desenvolvimento, o Vite roda na porta 3001 com proxy para a API
- O build gera os arquivos diretamente no diretório de recursos do Quarkus (`target/classes/META-INF/resources/console/`)

