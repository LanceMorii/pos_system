# 智慧超市系统后端

## 快速启动

### 1. 确保环境
- Java 17 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.0 或更高版本（可选，有内存数据库支持）

### 2. 启动后端服务

#### 方式一：使用Maven（推荐）
```bash
cd web_system
mvn spring-boot:run
```

#### 方式二：使用IDE
1. 在IDE中打开项目
2. 运行 `WebSystemApplication.java` 主类

#### 方式三：打包运行
```bash
cd web_system
mvn clean package
java -jar target/web_system-0.0.1-SNAPSHOT.jar
```

### 3. 验证启动
启动成功后，访问以下地址验证：
- 健康检查：http://localhost:8080/api/cashier/health
- API文档：http://localhost:8080/api/swagger-ui.html（如果配置了Swagger）

### 4. 常见问题

#### 端口被占用
如果8080端口被占用，可以修改 `application.yml` 中的端口：
```yaml
server:
  port: 8081
```

#### 数据库连接问题
如果MySQL连接失败，系统会自动使用内存数据库H2，无需担心。

#### 初始化数据
启动后可以通过以下API初始化测试数据：
```bash
POST http://localhost:8080/api/data-init/all
```

### 5. 配置说明

主要配置文件：`src/main/resources/application.yml`

```yaml
server:
  port: 8080                    # 服务端口
  servlet:
    context-path: /api          # API基础路径

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_market
    username: root
    password: root
  
  redis:
    host: localhost
    port: 6379
```

### 6. API接口

#### 收银台相关
- `GET /api/cashier/health` - 健康检查
- `GET /api/cashier/products` - 获取商品列表
- `GET /api/cashier/categories` - 获取分类列表
- `POST /api/cashier/checkout` - 订单结算

#### 数据初始化
- `POST /api/data-init/all` - 初始化所有数据
- `GET /api/data-init/stats` - 获取数据统计
- `DELETE /api/data-init/clear` - 清空数据

### 7. 开发模式

开发时建议使用以下命令启动，支持热重载：
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"
```