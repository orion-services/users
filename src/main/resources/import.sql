INSERT INTO "Role" (id, name) VALUES (1, 'admin');
INSERT INTO "Role" (id, name) VALUES (2, 'user');

-- Admin user: admin@orion.dev / orionadmin
-- Password hash (SHA256): 24febcc27e4a5762911a4481a941a3563cc4bf5e5f61f0ea3799333871d2a89b
INSERT INTO "User" (id, hash, name, email, password, emailvalid, emailvalidationcode, isusing2fa, secret2fa, require2faforbasiclogin, require2faforsociallogin)
VALUES (1, '00000000-0000-0000-0000-000000000001', 'Administrator', 'admin@orion.dev', '24febcc27e4a5762911a4481a941a3563cc4bf5e5f61f0ea3799333871d2a89b', true, '00000000-0000-0000-0000-000000000001', false, NULL, false, false);

-- Associate roles to admin user (admin and user)
INSERT INTO "User_Role" ("User_id", "roles_id") VALUES (1, 1);
INSERT INTO "User_Role" ("User_id", "roles_id") VALUES (1, 2);

-- Align identity sequences so the next generated keys do not collide with seeded rows
SELECT setval(pg_get_serial_sequence('public."User"', 'id'), (SELECT COALESCE(MAX(id), 1) FROM "User"));
SELECT setval(pg_get_serial_sequence('public."Role"', 'id'), (SELECT COALESCE(MAX(id), 1) FROM "Role"));
