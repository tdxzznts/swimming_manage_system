# 智能游泳馆管理系统 - 用户端H5

## 项目介绍

智能游泳馆管理系统用户端H5应用，基于Vue3 + TypeScript + Element Plus开发。

## 技术栈

- **框架**: Vue 3.4+
- **语言**: TypeScript 5.0+
- **UI组件**: Element Plus 2.13+ (移动端)
- **状态管理**: Pinia 3.0+
- **路由**: Vue Router 5.0+
- **HTTP客户端**: Axios 1.13+
- **构建工具**: Vite 7.3+
- **CSS预处理**: SCSS

## 项目结构

```
swimming-pool-h5/
├── public/                     # 静态资源
├── src/
│   ├── api/                    # API接口
│   ├── assets/                 # 资源文件
│   │   ├── styles/            # 全局样式
│   │   ├── images/            # 图片
│   │   └── icons/             # 图标
│   ├── components/             # 组件
│   │   ├── common/            # 通用组件
│   │   ├── business/          # 业务组件
│   │   └── form/              # 表单组件
│   ├── composables/            # 组合式函数
│   ├── directives/             # 自定义指令
│   ├── layouts/                # 布局组件
│   ├── router/                 # 路由配置
│   ├── stores/                 # 状态管理
│   ├── types/                  # TypeScript类型
│   ├── utils/                  # 工具函数
│   ├── views/                  # 页面视图
│   ├── App.vue                 # 根组件
│   └── main.ts                 # 入口文件
├── .env.development            # 开发环境变量
├── .env.production             # 生产环境变量
├── vite.config.ts              # Vite配置
├── tsconfig.json               # TypeScript配置
└── package.json                # 项目依赖
```

## 快速开始

### 安装依赖

```bash
pnpm install
```

### 启动开发服务器

```bash
pnpm dev
```

应用将在 `http://localhost:5173` 启动

### 构建生产版本

```bash
pnpm build
```

### 预览生产构建

```bash
pnpm preview
```

## 核心功能

- ✅ **首页**: 场馆信息、快速预约、系统公告
- ✅ **预约**: 查看可预约时段、选择时段、发起预约
- ✅ **订单**: 订单列表、订单详情、支付
- ✅ **我的**: 个人中心、会员卡、设置

## 开发规范

### 命名规范

- **文件名**: kebab-case（如：user-info.vue）
- **组件名**: PascalCase（如：UserInfo）
- **变量名**: camelCase（如：userInfo）
- **常量名**: UPPER_SNAKE_CASE（如：API_BASE_URL）
- **类型名**: PascalCase（如：UserInfo）

### 代码规范

```typescript
// 使用 <script setup> 语法
<script setup lang="ts">
import { ref, computed } from 'vue'

const count = ref(0)
const double = computed(() => count.value * 2)
</script>

// 使用 TypeScript 类型定义
interface UserInfo {
  id: number
  username: string
  email: string
}
```

## 浏览器支持

- iOS Safari
- Android Chrome
- 微信浏览器
- 最新版移动端浏览器

## License

MIT
