USE accounting_db;
DELETE FROM sys_user WHERE username='admin';
INSERT INTO sys_user (id, username, password, real_name, role, enabled) VALUES (1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi', '管理员', 'ADMIN', 1);
SELECT id, username, password FROM sys_user;
