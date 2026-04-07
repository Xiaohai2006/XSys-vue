# XSys-Vue

简洁的后台管理系统，基于 Spring Boot + Vue 3 构建。

## 技术栈

**后端**
- Spring Boot 4.0 + Spring Security
- MyBatis + MySQL
- JWT 认证

**前端**
- Vue 3 + Vite
- Naive UI + Pinia
- Axios + Vue Router

## 功能模块

- 用户管理 - 用户增删改查、角色分配
- 角色管理 - 角色配置、权限分配
- 权限管理 - 权限定义与控制
- 菜单管理 - 动态菜单配置
- 系统设置 - 全局配置

## 快速开始

### 后端

```bash
mvn spring-boot:run
```

配置 `application.yml` 中的数据库连接。

### 前端

```bash
cd vue
npm install
npm run dev
```

访问 http://localhost:5173

## 项目结构

```
├── src/main/java/cn/zhkj/xsys
│   ├── controller/    控制器
│   ├── service/       业务层
│   ├── mapper/        数据访问
│   ├── domain/        实体类
│   └── security/      安全配置
├── src/main/resources
│   ├── mapper/        MyBatis XML
│   └── application.yml
└── vue/               前端项目
    └── src/
        ├── views/     页面
        ├── api/       接口
        ├── stores/    状态管理
        └── router/    路由
```

## 权限模型

采用 RBAC 模型：用户 → 角色 → 权限，支持菜单级权限控制。
