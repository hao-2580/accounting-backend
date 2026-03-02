
# 企业账务管理系统 - Spring Boot 后端

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.3 | 核心框架 |
| Spring Data JPA | 3.2.x | ORM / 数据访问 |
| Spring Security | 3.2.x | 安全框架（CORS配置）|
| MySQL | 8.0+ | 生产数据库 |
| H2 | 内存 | 开发/测试数据库 |
| Lombok | 最新 | 代码简化 |
| Java | 17 | JDK 版本 |

---

## 项目结构

```
src/main/java/com/accounting/
├── AccountingApplication.java       # 启动类
├── config/
│   ├── SecurityConfig.java          # CORS & 安全配置
│   ├── JpaConfig.java               # JPA 审计
│   └── DataInitializer.java         # 开发环境初始数据
├── entity/
│   ├── BaseEntity.java              # 公共字段（createdAt/updatedAt）
│   ├── Account.java                 # 账户
│   ├── Transaction.java             # 收支记录
│   ├── Invoice.java                 # 发票
│   └── InvoiceItem.java             # 发票明细
├── repository/
│   ├── AccountRepository.java
│   ├── TransactionRepository.java   # 含自定义统计查询
│   └── InvoiceRepository.java
├── service/
│   ├── AccountService.java          # 接口
│   ├── TransactionService.java
│   ├── InvoiceService.java
│   ├── DashboardService.java
│   └── impl/                        # 实现类
├── controller/
│   ├── AccountController.java
│   ├── TransactionController.java
│   ├── InvoiceController.java
│   └── DashboardController.java
├── dto/
│   ├── request/                     # 请求 DTO（含 Validation）
│   └── response/                    # 响应 DTO
├── common/
│   ├── Result.java                  # 统一响应封装
│   └── PageResult.java              # 分页结果
└── exception/
    ├── BusinessException.java
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java  # 全局异常处理
```

---

## 快速启动（开发模式 - H2内存库）

```bash
# 1. 克隆 / 进入项目目录
cd accounting-backend

# 2. 编译运行（自动使用 H2，无需配置数据库）
mvn spring-boot:run

# 3. 访问
# API:        http://localhost:8080/api/
# H2 Console: http://localhost:8080/h2-console
#             JDBC URL: jdbc:h2:mem:accountingdb
```

---

## 切换 MySQL（生产模式）

```bash
# 1. 执行建表 SQL
mysql -u root -p < src/main/resources/schema-mysql.sql

# 2. 修改启动 profile
# application.yml 中将 active: dev 改为 active: prod
# 或启动时指定:
mvn spring-boot:run -Dspring-boot.run.profiles=prod \
  -Dspring-boot.run.arguments="--DB_USERNAME=root --DB_PASSWORD=yourpassword"
```

---

## API 接口文档

### 账户管理 `/api/accounts`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/accounts` | 获取所有账户 |
| GET | `/api/accounts/{id}` | 获取单个账户 |
| POST | `/api/accounts` | 创建账户 |
| PUT | `/api/accounts/{id}` | 更新账户 |
| DELETE | `/api/accounts/{id}` | 删除账户 |

### 收支记录 `/api/transactions`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/transactions` | 分页查询（支持筛选） |
| GET | `/api/transactions/{id}` | 获取单条记录 |
| POST | `/api/transactions` | 新增记录（自动更新账户余额）|
| DELETE | `/api/transactions/{id}` | 删除记录（自动回滚余额）|

**查询参数**: `type`, `accountId`, `category`, `yearMonth(2026-02)`, `startDate`, `endDate`, `page`, `size`

### 发票管理 `/api/invoices`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/invoices` | 分页查询 |
| GET | `/api/invoices/{id}` | 获取发票详情 |
| GET | `/api/invoices/overdue` | 逾期发票列表 |
| POST | `/api/invoices` | 新建发票 |
| PUT | `/api/invoices/{id}` | 编辑发票 |
| PATCH | `/api/invoices/{id}/status?status=PAID` | 更新发票状态 |
| DELETE | `/api/invoices/{id}` | 删除发票 |

### 仪表盘 & 报表

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/dashboard` | 本月概览 + 最近流水 + 逾期发票 |
| GET | `/api/reports?year=2026` | 年度报表 + 分类统计 + 月度趋势 |

---

## 请求示例

```bash
# 新增收入记录
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "type": "INCOME",
    "amount": 50000,
    "category": "销售收入",
    "date": "2026-02-28",
    "accountId": 1,
    "note": "2月货款"
  }'

# 新建发票
curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "北京科技有限公司",
    "issueDate": "2026-02-28",
    "dueDate": "2026-03-28",
    "taxRate": 13,
    "status": "UNPAID",
    "items": [
      {"description": "软件开发服务", "quantity": 1, "unitPrice": 30000}
    ]
  }'

# 标记发票已付款
curl -X PATCH "http://localhost:8080/api/invoices/1/status?status=PAID"

# 查询仪表盘
curl http://localhost:8080/api/dashboard
```

---

## 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

错误响应:
```json
{
  "code": 400,
  "message": "客户名称不能为空",
  "data": null
}
```
