# 智能游泳馆 H5 - UI 设计指南

## 设计主题

**美学概念**: "Fluid Intelligence"（流动智能）

将水的自然流动感与现代智能科技相结合，创造清爽、专业、易用的移动端体验。

---

## 配色方案

### 主色系
```scss
// 深海蓝 - 主色、标题、重要文字
$primary-dark: #0A4D68;

// 青绿 - 辅助色、图标、链接
$primary-cyan: #05BFDB;

// 渐变 - 按钮、卡片
$gradient-main: linear-gradient(135deg, #05BFDB 0%, #0A4D68 100%);
$gradient-light: linear-gradient(135deg, #0A4D68 0%, #05BFDB 100%);
```

### 背景色
```scss
// 极浅蓝灰 - 页面背景
$bg-page: #F0F8FF;
$bg-page-gradient: linear-gradient(180deg, #F0F8FF 0%, #E8F4F8 100%);

// 白色 - 卡片背景
$bg-card: #ffffff;

// 浅灰 - 输入框背景
$bg-input: #F8FAFC;
```

### 文字色
```scss
// 主文字
$text-primary: #0A4D68;

// 次要文字
$text-secondary: #6B7280;

// 辅助文字
$text-tertiary: #9CA3AF;

// 白色（深色背景用）
$text-white: #ffffff;
```

### 功能色
```scss
// 成功
$color-success: #10B981;

// 警告
$color-warning: #F59E0B;

// 错误
$color-error: #EF4444;

// 信息
$color-info: #05BFDB;
```

---

## 字体系统

### 字号层级
```scss
$font-size-h1: 28px;    // 页面主标题
$font-size-h2: 22px;    // 卡片标题
$font-size-h3: 18px;    // 区块标题
$font-size-lg: 17px;    // 重要文字
$font-size-base: 15px;  // 正文
$font-size-sm: 14px;    // 辅助说明
$font-size-xs: 13px;    // 次要信息
$font-size-xxs: 12px;   // 标签/提示
$font-size-micro: 11px; // 极小文字
```

### 字重
```scss
$font-weight-bold: 700;   // 标题
$font-weight-semibold: 600; // 强调
$font-weight-medium: 500;  // 中等
$font-weight-regular: 400; // 常规
```

---

## 间距系统

```scss
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 12px;
$spacing-lg: 16px;
$spacing-xl: 20px;
$spacing-2xl: 24px;
$spacing-3xl: 32px;
```

---

## 圆角系统

```scss
$radius-sm: 8px;   // 小卡片、标签
$radius-md: 10px;  // 输入框
$radius-lg: 12px;  // 按钮
$radius-xl: 14px;  // 大按钮
$radius-2xl: 16px; // 卡片
$radius-3xl: 24px; // 大卡片（登录/注册）
```

---

## 阴影系统

```scss
// 轻阴影 - 输入框、小卡片
$shadow-sm: 0 2px 8px rgba(10, 77, 104, 0.04);

// 中阴影 - 一般卡片
$shadow-md: 0 4px 16px rgba(10, 77, 104, 0.06);

// 大阴影 - 重要卡片
$shadow-lg: 0 8px 32px rgba(10, 77, 104, 0.08);

// 特大阴影 - 登录/注册卡片
$shadow-xl: 0 20px 60px rgba(10, 77, 104, 0.2);

// 按钮阴影
$shadow-btn: 0 8px 24px rgba(5, 191, 219, 0.3);
$shadow-btn-hover: 0 12px 32px rgba(5, 191, 219, 0.4);
```

---

## 组件规范

### 按钮

#### 主要按钮
```vue
<el-button
  type="primary"
  size="large"
  class="btn-primary"
>
  按钮文字
</el-button>
```

```scss
.btn-primary {
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  background: linear-gradient(135deg, #05BFDB 0%, #0A4D68 100%);
  border: none;
  box-shadow: 0 8px 24px rgba(5, 191, 219, 0.3);

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 12px 32px rgba(5, 191, 219, 0.4);
  }
}
```

#### 次要按钮
```scss
.btn-secondary {
  height: 44px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 10px;
  background: transparent;
  border: 2px dashed #05BFDB;
  color: #05BFDB;
}
```

### 输入框

```vue
<div class="input-wrapper">
  <div class="input-icon">
    <el-icon :size="20"><User /></el-icon>
  </div>
  <el-input
    v-model="value"
    placeholder="请输入..."
    size="large"
    class="custom-input"
  />
</div>
```

```scss
.input-wrapper {
  display: flex;
  align-items: center;
  background: #F8FAFC;
  border-radius: 14px;
  padding: 0 16px;
  border: 2px solid transparent;
  transition: all 0.3s;

  &:focus-within {
    background: #fff;
    border-color: #05BFDB;
    box-shadow: 0 0 0 4px rgba(5, 191, 219, 0.1);
  }
}

.input-icon {
  color: #05BFDB;
  margin-right: 12px;
}
```

### 卡片

#### 标准卡片
```vue
<div class="card">
  <!-- 卡片内容 -->
</div>
```

```scss
.card {
  background: #fff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 16px rgba(10, 77, 104, 0.06);
}
```

#### 玻璃态卡片
```scss
.card-glass {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 20px 60px rgba(10, 77, 104, 0.2);
}
```

---

## 动画效果

### 水波纹背景
```vue
<div class="water-bg">
  <div class="wave wave-1"></div>
  <div class="wave wave-2"></div>
  <div class="wave wave-3"></div>
</div>
```

```scss
.water-bg {
  position: fixed;
  background: linear-gradient(180deg, #0A4D68 0%, #05BFDB 50%, #F0F8FF 100%);
}

.wave {
  position: absolute;
  bottom: 30%;
  left: 0;
  width: 200%;
  height: 100px;
  background: url('data:image/svg+xml,...');
  background-size: 50% 100%;
  animation: wave 10s linear infinite;
}

@keyframes wave {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}
```

### 漂浮气泡
```vue
<div class="bubbles">
  <div v-for="i in 15" :key="i" class="bubble" :style="bubbleStyle(i)"></div>
</div>
```

```typescript
const bubbleStyle = (index: number) => {
  const size = Math.random() * 20 + 10
  const left = Math.random() * 100
  const delay = Math.random() * 5
  const duration = Math.random() * 10 + 10
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  }
}
```

### 脉冲动画
```scss
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes pulse-ring {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0.2;
  }
}
```

---

## 图标使用

### 图标颜色
- 主功能图标: `#05BFDB` (青色)
- 次要图标: `#0A4D68` (深蓝)
- 浅色背景图标: `rgba(5, 191, 219, 0.1)`

### 图标尺寸
- 小图标: 16-18px
- 标准图标: 20-22px
- 大图标: 24-28px
- 特大图标: 48-60px

---

## 页面模板

### 标准页面结构
```vue
<template>
  <div class="page-name">
    <!-- 水背景（可选） -->
    <div class="water-bg">
      <div class="wave wave-1"></div>
      <div class="wave wave-2"></div>
      <div class="wave wave-3"></div>
    </div>

    <!-- 导航栏 -->
    <NavBar title="页面标题" :show-back="true" />

    <!-- 主内容 -->
    <div class="page-content">
      <!-- 页面内容 -->
    </div>

    <!-- 底部导航 -->
    <TabBar v-model="activeTab" :tabs="tabs" />
  </div>
</template>
```

---

## 最佳实践

### DO
- ✅ 使用深海蓝(#0A4D68)作为主色
- ✅ 使用青绿(#05BFDB)作为强调色
- ✅ 卡片使用16px圆角
- ✅ 输入框焦点时显示蓝色边框和阴影
- ✅ 按钮使用渐变背景
- ✅ 适当使用水波纹和气泡动画
- ✅ 保持足够的留白空间
- ✅ 使用玻璃态效果（登录/注册页面）

### DON'T
- ❌ 不要使用紫色渐变
- ❌ 不要使用Inter、Roboto等通用字体
- ❌ 不要过度使用阴影
- ❌ 不要使用过于鲜艳的颜色
- ❌ 不要让动画过于频繁
- ❌ 不要使用非语义化的颜色

---

## 响应式

当前设计针对移动端优化（375px - 428px），如需适配平板：
```scss
@media (min-width: 768px) {
  .page-content {
    max-width: 600px;
    margin: 0 auto;
  }
}
```

---

## 可复用代码片段

### SCSS 变量文件
```scss
// styles/variables.scss
$primary-dark: #0A4D68;
$primary-cyan: #05BFDB;
$bg-page: #F0F8FF;
$text-primary: #0A4D68;
$text-secondary: #6B7280;
```

### 全局样式文件
```scss
// styles/global.scss
body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: $bg-page;
  color: $text-primary;
}
```

---

## 组件库扩展

基于 Element Plus 的样式覆盖：
```scss
// 覆盖 Element Plus 默认样式
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #05BFDB 0%, #0A4D68 100%);
  border: none;
}
```
