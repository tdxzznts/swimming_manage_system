# 🏊‍♂️ 智能游泳馆管理系统

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-green.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-blue.svg)
![Vue](https://img.shields.io/badge/Vue-3.4+-success.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)

**基于 Spring Boot + Vue 3 的智能化游泳馆运营管理平台**

[English](README_EN.md) | 简体中文

</div>

---

## 📖 项目简介

**智能游泳馆管理系统**（Intelligent Swimming Pool Management System）是一款面向游泳馆的智能化运营管理平台。系统支持会员管理、泳道预约、在线支付、水质监控等核心功能，采用分布式架构设计，有效解决高并发预约场景下的数据冲突问题。

> 🎯 **项目定位**：帮助游泳馆实现数字化运营，提升用户体验，降低管理成本

---

## ✨ 功能特性

### 🏠 用户模块
| 功能 | 说明 |
|------|------|
| 🔐 注册登录 | 支持手机号/邮箱注册，图形验证码校验 |
| 🔒 安全认证 | BCrypt 密码加密，Sa-Token 无状态认证 |
| 👤 个人信息 | 头像上传，个人资料管理 |

### ⭐ 会员体系
| 功能 | 说明 |
|------|------|
| 👑 会员等级 | 普通 → 银卡 → 金卡 → 钻石，四档权益 |
| 📈 自动升级 | 累计消费金额达标自动升级 |
| 🎁 等级权益 | 专属折扣、优先预约权、专属泳道 |

### 📅 泳道预约
| 功能 | 说明 |
|------|------|
| 🛤️ 精细化预约 | 时段 + 泳道双重选择 |
| 🏅 泳道分类 | 1号(慢速/10人) · 2号(快速/8人) · 3号(中速/6人) |
| 📆 灵活排期 | 支持未来7天预约 |
| 🔒 并发控制 | Redisson 分布式锁防冲突 |

### 💳 支付充值
| 功能 | 说明 |
|------|------|
| 💰 余额支付 | 账户余额实时扣款 |
| 🎫 免费次数 | 会员免费游泳次数抵扣 |
| 📱 扫码充值 | 支付宝沙箱环境二维码支付 |
| 🎁 阶梯赠送 | 充值金额越高赠送越多 |

### 📋 订单管理
| 功能 | 说明 |
|------|------|
| 📊 状态流转 | 待支付 → 已支付 → 已完成/已取消 |
| ⏰ 超时取消 | 30分钟未支付自动释放预约 |
| 💸 退款取消 | 支持用户主动取消并退款 |

### 📡 IoT 监控
| 功能 | 说明 |
|------|------|
| 🌊 水质监测 | 余氯、pH 值实时采集 |
| 📶 MQTT 通信 | 物联网设备数据上云 |
| 🚨 智能告警 | 水质超标自动推送通知 |

### 📈 数据统计
| 功能 | 说明 |
|------|------|
| 📊 经营概览 | 订单量、收入、会员增长 |
| 📈 数据可视化 | 图表展示，辅助决策 |

---

## 🏗️ 技术架构

```
┌─────────────────────────────────────────────────────────────────┐
│                           客户端层                               │
│                   Vue 3 + TypeScript + Vite                     │
│                      Element Plus + Pinia                       │
├─────────────────────────────────────────────────────────────────┤
│                           网关层                                 │
│                         Nginx (可选)                            │
├─────────────────────────────────────────────────────────────────┤
│                           应用层                                 │
│                    Spring Boot 3.5 REST API                     │
│              MyBatis-Plus | Sa-Token | Redisson                 │
├─────────────────────────────────────────────────────────────────┤
│                          服务层                                  │
│     ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│     │ 用户服务  │  │ 预约服务  │  │ 支付服务  │  │ IoT服务  │      │
│     └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
├─────────────────────────────────────────────────────────────────┤
│                          数据层                                  │
│     ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│     │  MySQL   │  │  Redis   │  │ MQTT Broker │  │ AliPay  │      │
│     │   8.0    │  │   6.0+   │  │  (Mosquitto) │  │  沙箱   │      │
│     └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
└─────────────────────────────────────────────────────────────────┘
```

### 🛠️ 技术栈详情

| 层级 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **前端** | Vue 3 | 3.4+ | 渐进式前端框架 |
| | TypeScript | 5.0+ | 类型安全 |
| | Vite | 5.0+ | 下一代前端构建工具 |
| | Element Plus | 2.5+ | UI 组件库 |
| | Pinia | 2.1+ | 状态管理 |
| | Axios | 1.6+ | HTTP 客户端 |
| **后端** | Spring Boot | 3.5 | 企业级应用框架 |
| | MyBatis-Plus | 3.5+ | ORM 增强工具 |
| | Sa-Token | 1.37+ | 权限认证框架 |
| | Redisson | 3.27+ | 分布式锁解决方案 |
| **数据库** | MySQL | 8.0+ | 关系型数据库 |
| | Redis | 6.0+ | 缓存与分布式锁 |
| **IoT** | MQTT | - | 物联网通信协议 |
| **支付** | Alipay SDK | - | 支付宝沙箱支付 |

---

## 📷 系统截图

> 📸 截图待补充，可前往 [Wiki](https://github.com/your-repo/swimming-pool-system/wiki) 查看更多

<details>
<summary>点击展开查看截图</summary>

| 模块 | 描述 |
|------|------|
| ![首页](docs/screenshots/home.png) | 首页概览 |
| ![预约](docs/screenshots/booking.png) | 泳道预约 |
| ![会员](docs/screenshots/membership.png) | 会员中心 |
| ![订单](docs/screenshots/orders.png) | 订单管理 |
| ![监控](docs/screenshots/monitoring.png) | IoT监控面板 |

</details>

---

## 🚀 快速开始

### 📋 环境要求

| 环境 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 后端运行环境 |
| Node.js | 18+ | 前端构建工具 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存与锁 |
| Maven | 3.6+ | 后端构建工具 |

### 🖥️ 后端启动

```bash
# 1. 克隆项目
git clone https://github.com/your-repo/swimming-pool-system.git
cd swimming-pool-system

# 2. 创建数据库
mysql -u root -p
CREATE DATABASE swimming_pool DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE swimming_pool;
source sql/init.sql;

# 3. 配置 Redis
# 确保 Redis 服务已启动，默认端口 6379

# 4. 修改配置文件
# 编辑 backend/src/main/resources/application.yml
# 配置数据库连接、Redis、支付宝沙箱参数

# 5. 启动后端服务
cd backend
mvn clean install -DskipTests
mvn spring-boot:run

# 或打包后运行
java -jar target/swimming-pool-backend-1.0.0.jar
```

### 🎨 前端启动

```bash
# 1. 进入前端目录
cd frontend

# 2. 安装依赖
npm install

# 3. 开发模式启动
npm run dev

# 访问地址: http://localhost:5173
```

### 📱 配置说明

**后端配置** `backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/swimming_pool?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: # 如有密码请填写

  rabbitmq:
    host: localhost
    port: 1883
    username: admin
    password: admin

alipay:
  sandbox:
    app-id: your_app_id
    private-key: your_private_key
    alipay-public-key: alipay_public_key
```

---

## 📁 项目结构

### 🌲 整体结构

```
swimming-pool-system/
├── docs/                    # 项目文档
│   ├── sql/                 # 数据库脚本
│   │   └── init.sql
│   └── screenshots/         # 截图资源
├── backend/                 # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/swimmingpool/
│   │       │       ├── common/          # 通用模块
│   │       │       │   ├── config/      # 配置类
│   │       │       │   ├── exception/   # 异常处理
│   │       │       │   ├── result/      # 统一响应
│   │       │       │   └── security/    # 安全相关
│   │       │       ├── module/          # 业务模块
│   │       │       │   ├── user/        # 用户模块
│   │       │       │   ├── membership/  # 会员模块
│   │       │       │   ├── booking/     # 预约模块
│   │       │       │   ├── payment/     # 支付模块
│   │       │       │   ├── order/       # 订单模块
│   │       │       │   └── iot/         # IoT模块
│   │       │       └── SwimmingPoolApplication.java
│   │       └── resources/
│   │           ├── application.yml
│   │           └── mapper/
│   ├── pom.xml
│   └── README.md
└── frontend/                # 前端项目
    ├── public/
    ├── src/
    │   ├── api/             # 接口封装
    │   │   ├── user.ts
    │   │   ├── booking.ts
    │   │   ├── membership.ts
    │   │   ├── payment.ts
    │   │   └── order.ts
    │   ├── assets/          # 静态资源
    │   ├── components/      # 公共组件
    │   ├── layouts/         # 布局组件
    │   ├── router/          # 路由配置
    │   ├── stores/          # Pinia 状态
    │   │   ├── user.ts
    │   │   ├── membership.ts
    │   │   └── booking.ts
    │   ├── utils/           # 工具函数
    │   ├── views/           # 页面视图
    │   │   ├── home/
    │   │   ├── auth/
    │   │   ├── booking/
    │   │   ├── membership/
    │   │   ├── order/
    │   │   ├── payment/
    │   │   └── iot/
    │   ├── App.vue
    │   └── main.ts
    ├── index.html
    ├── package.json
    ├── tsconfig.json
    └── vite.config.ts
```

### 📦 后端模块说明

```
com.swimmingpool
├── common                    # 公共基础模块
│   ├── config               # Redis / Sa-Token / CORS 等配置
│   ├── exception            # 全局异常处理器
│   ├── result               # 统一响应结构
│   └── security             # JWT / 登录认证
├── user                      # 👤 用户管理
│   ├── controller
│   ├── service
│   ├── mapper
│   └── entity
├── membership                # ⭐ 会员等级
│   ├── controller
│   ├── service              # 等级计算、权益判定
│   ├── mapper
│   └── entity
├── booking                   # 📅 泳道预约
│   ├── controller
│   ├── service              # 预约逻辑、分布式锁
│   ├── mapper
│   └── entity
├── payment                   # 💳 支付充值
│   ├── controller
│   ├── service              # 支付宝对接
│   ├── mapper
│   └── entity
├── order                     # 📋 订单管理
│   ├── controller
│   ├── service              # 状态流转、定时任务
│   ├── mapper
│   └── entity
└── iot                       # 📡 IoT监控
    ├── controller
    ├── service              # MQTT 数据处理
    ├── mapper
    └── entity
```

---

## 💡 核心设计

### 🔐 分布式锁解决预约冲突

**问题场景**：多个用户同时抢同一时段、同一泳道，可能导致超卖

**解决方案**：基于 Redisson 的 **RLock** 分布式锁

```java
// 锁的粒度：日期 + 时段 + 泳道
String lockKey = String.format("booking:lock:%s:%d:%d", date, timeSlotId, laneId);
RLock lock = redissonClient.getLock(lockKey);

try {
    // 尝试获取锁，等待5秒，锁定10秒后自动释放
    boolean locked = lock.tryLock(5, 10, TimeUnit.SECONDS);
    if (!locked) {
        throw new BizException("预约人数已满，请稍后重试");
    }
    
    // 执行预约逻辑
    return doBooking(userId, date, timeSlotId, laneId);
    
} finally {
    // 释放锁
    if (lock.isHeldByCurrentThread()) {
        lock.unlock();
    }
}
```

**锁 Key 设计**：
```
booking:lock:2024-01-15:1:2
      ↑      ↑       ↑  ↑
   前缀    日期    时段 泳道
```

**优势**：
- ✅ 锁粒度细化到具体的「日期+时段+泳道」组合
- ✅ 互不影响，不同泳道可并行预约
- ✅ 锁超时自动释放，防止死锁
- ✅ Redisson 基于 Redis，性能优秀

---

### 📡 MQTT 物联网水质监控

**架构流程**：

```
┌─────────────┐    MQTT     ┌─────────────┐    HTTP     ┌─────────────┐
│  水质传感器  │ ──────────→ │ MQTT Broker │ ──────────→ │ Spring Boot │
│  (ESP32等)  │   Topics:   │ (Mosquitto) │             │   IoT模块   │
└─────────────┘  iot/water  └─────────────┘             └─────────────┘
                                                                │
                                                                ▼
                                                         ┌─────────────┐
                                                         │  告警判断    │
                                                         │ 余氯/Ph超标  │
                                                         └─────────────┘
```

**MQTT 消息格式**：

```json
{
  "deviceId": "sensor_001",
  "timestamp": 1705312800000,
  "data": {
    "chlorine": 1.2,    // 余氯 mg/L
    "ph": 7.4,          // pH值
    "temperature": 26.5 // 水温 ℃
  }
}
```

**告警阈值**：

| 指标 | 正常范围 | 告警阈值 |
|------|----------|----------|
| 余氯 | 0.3-1.0 mg/L | < 0.3 或 > 1.0 |
| pH值 | 7.0-7.8 | < 6.8 或 > 8.2 |
| 水温 | 25-28 ℃ | < 24 或 > 30 |

---

### 👑 会员等级自动体系

**等级规则**：

| 等级 | 累计消费 | 折扣 | 免费次数/月 | 优先预约 |
|------|----------|------|-------------|----------|
| 普通 | 0元 | 100% | 0 | ❌ |
| 银卡 | 500元 | 95% | 2次 | ❌ |
| 金卡 | 2000元 | 90% | 5次 | ✅ |
| 钻石 | 5000元 | 85% | 10次 | ✅ |

**升降级逻辑**：

```java
/**
 * 根据累计消费计算会员等级
 * 每次支付完成后调用此方法检查是否需要升级
 */
public Membership calculateLevel(BigDecimal totalConsumption) {
    if (totalConsumption.compareTo(new BigDecimal("5000")) >= 0) {
        return Membership.DIAMOND;
    } else if (totalConsumption.compareTo(new BigDecimal("2000")) >= 0) {
        return Membership.GOLD;
    } else if (totalConsumption.compareTo(new BigDecimal("500")) >= 0) {
        return Membership.SILVER;
    }
    return Membership.NORMAL;
}
```

**权益计算**：

```java
/**
 * 计算订单实际支付金额
 */
public BigDecimal calculatePayment(Order order, Membership membership) {
    BigDecimal originalPrice = order.getPrice();
    
    // 1. 应用会员折扣
    BigDecimal discountedPrice = originalPrice.multiply(membership.getDiscountRate());
    
    // 2. 扣除免费次数
    if (membership.getFreeTimesLeft() > 0) {
        return BigDecimal.ZERO; // 免费
    }
    
    return discountedPrice;
}
```

---

## 🌐 部署说明

### 🐳 Docker 部署（推荐）

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: swimming_pool
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docs/sql/init.sql:/docker-entrypoint-initdb.d/init.sql
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  mosquitto:
    image: eclipse-mosquitto:latest
    ports:
      - "1883:1883"
      - "9001:9001"
    volumes:
      - ./mosquitto.conf:/mosquitto/config/mosquitto.conf

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=prod

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

### ☁️ 云服务器部署

1. **安装基础环境**
```bash
# 安装 JDK 17
sudo apt install openjdk-17-jdk -y

# 安装 Node.js 18
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install nodejs -y

# 安装 Maven
sudo apt install maven -y

# 安装 MySQL 8.0
sudo apt install mysql-server -y

# 安装 Redis
sudo apt install redis-server -y
```

2. **构建项目**
```bash
# 后端打包
cd backend
mvn clean package -DskipTests
# 产物: target/swimming-pool-backend-1.0.0.jar

# 前端打包
cd frontend
npm install
npm run build
# 产物: dist/
```

3. **配置 Nginx**
```nginx
server {
    listen 80;
    server_name your_domain.com;
    
    # 前端静态文件
    location / {
        root /var/www/swimming-pool/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端 API 代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 📜 API 文档

部署后访问 `http://localhost:8080/swagger-ui.html` 查看完整 API 文档

| 模块 | 接口前缀 | 说明 |
|------|----------|------|
| 用户 | `/api/user` | 注册、登录、个人信息 |
| 会员 | `/api/membership` | 等级、权益查询 |
| 预约 | `/api/booking` | 泳道、时段、预约 |
| 支付 | `/api/payment` | 充值、支付 |
| 订单 | `/api/order` | 订单列表、取消 |
| IoT | `/api/iot` | 设备管理、水质数据 |

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📄 License

本项目采用 [MIT License](LICENSE) 开源协议。

---

## 🙏 鸣谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 后端框架
- [Vue.js](https://vuejs.org/) - 前端框架
- [Element Plus](https://element-plus.org/) - UI 组件库
- [MyBatis-Plus](https://baomidou.com/) - ORM 框架
- [Sa-Token](https://sa-token.dev/) - 权限认证
- [Redisson](https://redisson.org/) - 分布式锁
- [Alipay](https://open.alipay.com/) - 支付服务

---

<div align="center">

**如果这个项目对你有帮助，请给我们一个 ⭐**

Made with ❤️ by [Your Team Name]

</div>
