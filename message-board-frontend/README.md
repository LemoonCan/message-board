# 留言板前端项目

这是一个使用React和Ant Design开发的留言板前端应用。

## 功能特点

- 用户注册与登录
- 发布留言
- 查看所有留言
- 回复留言
- 嵌套留言展示

## 技术栈

- React 18
- TypeScript
- Ant Design 5
- React Router 6
- Axios

## 项目结构

```
src/
  ├── components/       # 可复用组件
  ├── pages/            # 页面组件
  ├── services/         # API服务
  ├── utils/            # 工具函数
  ├── assets/           # 静态资源
  ├── App.tsx           # 主应用组件
  └── index.tsx         # 入口文件
```

## 开始使用

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm start
```

### 构建生产版本

```bash
npm run build
```

## API 接口

应用默认连接到 `http://localhost:8080/api` 后端服务。API 端点包括：

- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/me` - 获取当前用户信息
- `GET /api/messages` - 获取所有顶层留言
- `GET /api/messages/{id}/replies` - 获取指定留言的回复
- `POST /api/messages` - 创建新留言

## 页面说明

1. **登录页** - 使用用户名和密码登录，支持"记住我"功能
2. **注册页** - 提供用户名、邮箱和密码注册新账户
3. **首页** - 根据用户登录状态显示不同内容，已登录用户可以发布留言 