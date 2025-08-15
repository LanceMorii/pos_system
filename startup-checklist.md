# 启动检查清单

## 1. 后端启动检查

### 步骤1：启动后端服务
```bash
cd web_system
mvn spring-boot:run
```

### 步骤2：验证后端启动成功
查看控制台输出，应该看到类似信息：
```
Maven智慧超市系统后端启动成功！
访问地址: http://localhost:8080/api
```

### 步骤3：浏览器验证
在浏览器中访问以下地址：
- http://localhost:8080/api/cashier/health
- http://localhost:8080/api/cashier/test

应该看到JSON响应，例如：
```json
{
  "code": 200,
  "message": "收银台服务正常",
  "data": {
    "productCount": 16,
    "categoryCount": 5,
    "status": "正常"
  }
}
```

## 2. 前端启动检查

### 步骤1：启动前端服务
```bash
cd web_ui
npm run dev
```

### 步骤2：验证前端启动成功
控制台应该显示：
```
Local:   http://localhost:3000/
Network: use --host to expose
```

### 步骤3：访问前端
浏览器访问：http://localhost:3000

## 3. 连接测试

### 方法1：使用收银台测试
1. 访问：http://localhost:3000/dashboard/simple-cashier
2. 点击"测试后端连接"按钮
3. 应该显示"后端连接正常"

### 方法2：使用连接测试页面
1. 访问：http://localhost:3000/dashboard/connection-test
2. 查看连接状态
3. 运行各种测试

### 方法3：直接API测试
使用curl或Postman测试：
```bash
curl http://localhost:8080/api/cashier/health
curl http://localhost:8080/api/cashier/test
```

## 4. 常见问题排查

### 问题1：后端启动失败
- 检查Java版本：`java -version`（需要17+）
- 检查Maven版本：`mvn -version`
- 检查端口占用：`netstat -ano | findstr :8080`

### 问题2：404错误
- 确认后端已启动
- 检查URL路径是否正确
- 查看后端控制台是否有错误

### 问题3：CORS错误
- 后端已配置`@CrossOrigin(origins = "*")`
- 如果仍有问题，检查浏览器控制台

### 问题4：数据库连接失败
- 系统会自动使用内存数据库H2
- 不影响基本功能测试

## 5. 成功标志

当以下条件都满足时，系统应该正常工作：
- [ ] 后端在8080端口正常启动
- [ ] 前端在3000端口正常启动
- [ ] 浏览器访问 http://localhost:8080/api/cashier/health 返回JSON
- [ ] 前端"测试后端连接"按钮显示成功
- [ ] 收银台可以正常加载商品列表

## 6. 下一步

如果所有测试都通过：
1. 初始化测试数据：点击"初始化测试数据"
2. 测试收银功能：添加商品到购物车并结算
3. 如果结算仍然失败，查看浏览器控制台和后端日志的具体错误信息