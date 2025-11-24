INSERT INTO Role (id, name) VALUES (1, 'admin');
INSERT INTO Role (id, name) VALUES (2, 'user');

-- Admin user: admin@orion.dev / orionadmin
-- Password hash (SHA256): 24febcc27e4a5762911a4481a941a3563cc4bf5e5f61f0ea3799333871d2a89b
INSERT INTO User (id, hash, name, email, password, emailValid, emailValidationCode, isUsing2FA, secret2FA, require2FAForBasicLogin, require2FAForSocialLogin) 
VALUES (1, '00000000-0000-0000-0000-000000000001', 'Administrator', 'admin@orion.dev', '24febcc27e4a5762911a4481a941a3563cc4bf5e5f61f0ea3799333871d2a89b', true, '00000000-0000-0000-0000-000000000001', false, NULL, false, false);

-- Associar roles ao usuário admin (admin e user)
-- A tabela de junção está definida explicitamente na entidade UserEntity como "User_Role"
INSERT INTO User_Role (User_id, roles_id) VALUES (1, 1); -- admin
INSERT INTO User_Role (User_id, roles_id) VALUES (1, 2); -- user
