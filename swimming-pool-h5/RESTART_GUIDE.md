# 前端完整清理和重启指南

## 问题诊断

当前错误 `ReferenceError: registerApi is not defined` 通常是由于以下原因之一：

1. **浏览器缓存** - 浏览器缓存了旧版本的JavaScript文件
2. **Vite缓存** - Vite开发服务器缓存了旧代码
3. **热更新失败** - Vite的热模块替换(HMR)没有正确更新代码

## 完整解决方案

### 步骤1: 完全停止前端服务

```bash
# 在运行前端的终端中按 Ctrl+C 停止服务
# 或者强制关闭进程
taskkill /F /IM node.exe
```

### 步骤2: 清理所有缓存

```bash
# 在 swimming-pool-h5 目录下执行

# 1. 删除node_modules下的.vite缓存
rm -rf node_modules/.vite

# 2. 删除项目根目录的.vite缓存（如果有）
rm -rf .vite

# 3. 删除dist目录
rm -rf dist

# 4. 清理浏览器缓存 - 在浏览器中按 Ctrl+Shift+Delete
# 或者使用无痕模式（推荐）
```

### 步骤3: 清理浏览器Storage

在浏览器开发者工具中执行：

```javascript
// 打开开发者工具(F12) -> Console -> 粘贴并回车
localStorage.clear();
sessionStorage.clear();
location.reload(true);
```

### 步骤4: 重新启动前端服务

```bash
cd swimming-pool-h5
pnpm dev
```

### 步骤5: 使用无痕模式测试

1. 打开浏览器
2. 使用无痕/隐私模式（Ctrl+Shift+N）
3. 访问 `http://localhost:5173/register`
4. 打开开发者工具(F12)查看Console和Network

## 验证清单

### 后端验证

```bash
# 测试后端是否运行
curl http://localhost:8080/h5/auth/getUserInfo

# 应该返回: {"code":500,"msg":"获取用户信息失败","data":null}
# 如果返回这个说明后端正常运行
```

### 前端验证

打开 `test-api.html` 文件：

```bash
# 在浏览器中直接打开
file:///F:/Ys/bishedoc/whr/swimming-pool-h5/test-api.html
```

依次测试：
1. ✅ 发送验证码按钮
2. ✅ 注册按钮
3. ✅ 登录按钮

## 常见问题排查

### 问题1: 端口被占用

```bash
# 查看端口占用
netstat -ano | findstr :5173

# 杀死进程
taskkill /PID <进程ID> /F
```

### 问题2: Vite编译错误

```bash
# 删除node_modules重新安装
rm -rf node_modules
pnpm install
```

### 问题3: 类型错误

检查以下文件是否有红色波浪线：
- `src/stores/user.ts` (第4行)
- `src/api/auth.ts` (第14行)
- `src/types/user.ts` (第10行)

## 快速测试命令

### 测试注册接口

```bash
curl -X POST http://localhost:8080/h5/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"test123\",\"password\":\"Test123456\",\"confirmPassword\":\"Test123456\",\"email\":\"test@qq.com\",\"verifyCode\":\"123456\"}"
```

### 测试登录接口

```bash
curl -X POST http://localhost:8080/h5/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"account\":\"test123\",\"password\":\"Test123456\"}"
```

## 预期结果

成功后应该看到：

1. **发送验证码**: `{"code":200,"msg":"验证码已发送","data":null}`
2. **注册成功**: `{"code":200,"msg":"注册成功","data":null}`
3. **登录成功**: `{"code":200,"msg":"操作成功","data":{...}}`

## 注意事项

1. ⚠️ 确保后端服务已启动（IDEA中运行）
2. ⚠️ 确保邮件服务已配置（application-dev.yml）
3. ⚠️ 验证码在Redis中有效期为5分钟
4. ⚠️ 测试时使用真实的邮箱地址
5. ⚠️ QQ邮箱需要开启SMTP并获取授权码
