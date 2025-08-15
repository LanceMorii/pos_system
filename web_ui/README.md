# 智慧超市系统前端

## 快速启动

### 1. 安装依赖
```bash
cd web_ui
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```

前端将运行在：http://localhost:3000

### 3. 验证连接
启动后访问：http://localhost:3000/dashboard/connection-test 进行连接测试

### 4. 常见问题解决

#### 无法连接到后端服务
1. **检查后端是否启动**
   - 确保后端运行在 http://localhost:8080
   - 访问 http://localhost:8080/api/cashier/health 验证

2. **检查代理配置**
   - 前端代理配置在 `vite.config.ts` 中
   - 默认将 `/api` 请求代理到 `http://localhost:8080`

3. **检查防火墙**
   - 确保8080端口没有被防火墙阻止
   - Windows: 检查Windows Defender防火墙
   - Mac/Linux: 检查iptables或ufw

4. **端口冲突**
   - 如果后端使用了其他端口，修改 `vite.config.ts` 中的代理配置

### 5. 项目结构
```
web_ui/
├── src/
│   ├── api/           # API接口
│   ├── components/    # 公共组件
│   ├── views/         # 页面组件
│   │   ├── cashier/   # 收银台页面
│   │   └── test/      # 测试页面
│   ├── router/        # 路由配置
│   ├── store/         # 状态管理
│   └── utils/         # 工具函数
├── vite.config.ts     # Vite配置
└── package.json       # 依赖配置
```

### 6. 主要页面

#### 收银台系统
- `/dashboard/cashier` - 收银台入口
- `/dashboard/simple-cashier` - 简化收银台
- `/dashboard/full-cashier` - 完整收银台

#### 测试工具
- `/dashboard/connection-test` - 连接测试
- `/dashboard/data-init-test` - 数据初始化测试

### 7. 开发配置

#### 代理配置 (vite.config.ts)
```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '/api')
    }
  }
}
```

#### 请求配置 (src/utils/request.ts)
```typescript
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})
```

### 8. 构建部署
```bash
# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

构建后的文件在 `dist/` 目录中。