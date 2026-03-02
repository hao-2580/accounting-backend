USE accounting_db;

-- 初始管理员账号 密码: 123456
DELETE FROM sys_user WHERE username='admin';
INSERT INTO sys_user (id, username, password, real_name, role, enabled)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'ADMIN', 1);

-- 初始账户数据
INSERT INTO account (user_id, name, type, balance, description) VALUES
(1, '招行对公账户', 'BANK',    128600.00, '招商银行对公活期账户'),
(1, '现金',         'CASH',      5200.00, '公司现金'),
(1, '支付宝',       'ALIPAY',   18900.00, '支付宝企业账户');

-- 初始收支记录
INSERT INTO transactions (type, amount, category, account_id, date, note) VALUES
('INCOME',  52000.00, '销售收入', 1, '2026-02-05', '2月货款'),
('EXPENSE', 12000.00, '办公租金', 1, '2026-02-01', '2月租金'),
('EXPENSE', 25000.00, '人员工资', 1, '2026-02-15', '员工薪酬'),
('INCOME',  18000.00, '服务收入', 1, '2026-02-10', '咨询项目'),
('INCOME',   8000.00, '服务收入', 3, '2026-01-20', '维护费'),
('EXPENSE',  3500.00, '广告推广', 1, '2026-01-15', '推广费');

-- 初始发票数据
INSERT INTO invoices (invoice_no, client_name, issue_date, due_date, status, tax_rate, remark) VALUES
('INV-2026-001', '北京科技有限公司', '2026-02-01', '2026-03-01', 'UNPAID', 13.00, '软件开发服务'),
('INV-2026-002', '上海贸易集团',     '2026-01-15', '2026-02-15', 'PAID',   13.00, '产品销售'),
('INV-2025-088', '广州供应链公司',   '2025-12-01', '2026-01-01', 'UNPAID',  6.00, '顾问咨询费');

-- 发票明细
INSERT INTO invoice_item (invoice_id, description, quantity, unit_price) VALUES
(1, '软件开发服务', 1, 28000.00),
(2, '产品销售',    50,   600.00),
(3, '顾问咨询费',   3,  5000.00);

SELECT '数据初始化完成' as status;
