<template>
  <div class="profile-edit-page">
    <!-- 几何背景 -->
    <div class="geometric-bg">
      <div class="gradient-bg"></div>
      <div class="diagonal-stripes"></div>
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 主内容 -->
    <div class="edit-content">
      <!-- 头像上传 -->
      <div class="avatar-section-brutal">
        <div class="avatar-wrapper-brutal" @click="handleAvatarClick">
          <img v-if="userForm.avatar" :src="userForm.avatar" class="avatar-img-brutal" />
          <div v-else class="avatar-placeholder-brutal">
            <el-icon :size="50"><User /></el-icon>
          </div>
          <div class="avatar-edit-brutal">
            <el-icon :size="20"><Camera /></el-icon>
          </div>
        </div>
        <p class="avatar-tip-brutal">点击上传头像</p>
        <input
          ref="avatarInput"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleAvatarChange"
        />
      </div>

      <!-- Tab切换 -->
      <div class="tabs-card-brutal">
        <el-tabs v-model="activeTab" class="profile-tabs-brutal">
          <!-- 基本资料 -->
          <el-tab-pane label="基本资料" name="profile">
            <el-form
              ref="profileFormRef"
              :model="userForm"
              :rules="profileRules"
              label-position="top"
              class="profile-form-brutal"
            >
              <el-form-item label="用户昵称" prop="nickName">
                <el-input
                  v-model="userForm.nickName"
                  placeholder="请输入用户昵称"
                  maxlength="30"
                  show-word-limit
                  class="input-brutal"
                />
              </el-form-item>

              <el-form-item label="手机号码" prop="phonenumber">
                <el-input
                  v-model="userForm.phonenumber"
                  placeholder="请输入手机号码"
                  maxlength="11"
                  class="input-brutal"
                />
              </el-form-item>

              <el-form-item label="邮箱" prop="email">
                <el-input
                  v-model="userForm.email"
                  placeholder="请输入邮箱地址"
                  maxlength="50"
                  class="input-brutal"
                />
              </el-form-item>

              <el-form-item label="性别">
                <el-radio-group v-model="userForm.sex" class="gender-radio-brutal">
                  <el-radio value="0" class="gender-radio-item">男</el-radio>
                  <el-radio value="1" class="gender-radio-item">女</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item>
                <button
                  type="button"
                  class="submit-btn-brutal"
                  :disabled="submitting"
                  @click="handleSaveProfile"
                >
                  {{ submitting ? '保存中...' : '保存修改' }}
                </button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 修改密码 -->
          <el-tab-pane label="修改密码" name="password">
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-position="top"
              class="password-form-brutal"
            >
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入旧密码"
                  show-password
                  maxlength="20"
                  class="input-brutal"
                />
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                  maxlength="20"
                  class="input-brutal"
                />
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                  maxlength="20"
                  class="input-brutal"
                />
              </el-form-item>

              <el-form-item>
                <button
                  type="button"
                  class="submit-btn-brutal"
                  :disabled="submitting"
                  @click="handleSavePassword"
                >
                  {{ submitting ? '修改中...' : '修改密码' }}
                </button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules, type UploadRawFile } from 'element-plus'
import { ArrowLeft, User, Camera } from '@element-plus/icons-vue'
import { getUserProfile, updateUserProfile, updateUserPwd, uploadAvatar } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeTab = ref('profile')
const submitting = ref(false)
const avatarInput = ref<HTMLInputElement>()
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

// 用户信息表单
const userForm = reactive({
  nickName: '',
  phonenumber: '',
  email: '',
  sex: '0',
  avatar: ''
})

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 基本资料验证规则
const profileRules: FormRules = {
  nickName: [
    { required: true, message: '用户昵称不能为空', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '邮箱地址不能为空', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phonenumber: [
    { required: true, message: '手机号码不能为空', trigger: 'blur' },
    { pattern: /^1[3456789][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 密码验证规则
const equalToPassword = (_rule: any, value: string, callback: any) => {
  if (passwordForm.newPassword !== value) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '旧密码不能为空', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '新密码不能为空', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
    { pattern: /^[^<>"'|\\]+$/, message: '不能包含非法字符：< > " \' \\ |', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '确认密码不能为空', trigger: 'blur' },
    { validator: equalToPassword, trigger: 'blur' }
  ]
}

// 加载用户信息
const loadUserProfile = async () => {
  try {
    const res = await getUserProfile()
    if (res.code === 200 && res.data) {
      const user = res.data.user || res.data
      Object.assign(userForm, {
        nickName: user.nickName || user.nickname || '',
        phonenumber: user.phonenumber || '',
        email: user.email || '',
        sex: user.sex || '0',
        avatar: user.avatar || ''
      })
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// 保存基本资料
const handleSaveProfile = async () => {
  if (!profileFormRef.value) return

  await profileFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const res = await updateUserProfile(userForm)
      if (res.code === 200) {
        ElMessage.success('修改成功')
        // 更新store中的用户信息
        await userStore.getInfo()
      }
    } catch (error) {
      ElMessage.error('修改失败')
    } finally {
      submitting.value = false
    }
  })
}

// 保存密码
const handleSavePassword = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      const res = await updateUserPwd(passwordForm.oldPassword, passwordForm.newPassword)
      if (res.code === 200) {
        ElMessage.success('密码修改成功，请重新登录')
        // 清空表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
        passwordFormRef.value.resetFields()
        // 延迟跳转到登录页
        setTimeout(() => {
          userStore.logout()
          router.push('/login')
        }, 1500)
      }
    } catch (error) {
      ElMessage.error('密码修改失败')
    } finally {
      submitting.value = false
    }
  })
}

// 点击头像
const handleAvatarClick = () => {
  avatarInput.value?.click()
}

// 头像文件选择
const handleAvatarChange = async (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小（2MB）
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }

  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('avatarfile', file)

    const res = await uploadAvatar(formData)
    if (res.code === 200 && res.data) {
      userForm.avatar = res.data.imgUrl
      ElMessage.success('头像上传成功')
      // 更新store中的用户信息
      await userStore.getInfo()
    }
  } catch (error) {
    ElMessage.error('头像上传失败')
  } finally {
    submitting.value = false
    // 清空input，允许重复选择同一文件
    target.value = ''
  }
}

onMounted(() => {
  loadUserProfile()
  // 根据URL参数设置默认tab
  const tab = route.query.tab as string
  if (tab === 'password') {
    activeTab.value = 'password'
  }
})
</script>

<style scoped lang="scss">
.profile-edit-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  padding-bottom: 40px;
}

/* 几何背景 */
.geometric-bg {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  overflow: hidden;
}

.gradient-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, var(--color-obsidian) 0%, #1a3a5c 50%, var(--color-obsidian) 100%);
}

.diagonal-stripes {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: repeating-linear-gradient(
    45deg,
    transparent,
    transparent 35px,
    rgba(255, 255, 255, 0.02) 35px,
    rgba(255, 255, 255, 0.02) 70px
  );
}

.floating-shape {
  position: absolute;
  border: 2px solid rgba(255, 255, 255, 0.08);
  animation: geometric-float 20s ease-in-out infinite;

  &.shape-1 {
    width: 100px;
    height: 100px;
    top: 10%;
    right: 10%;
    border-radius: 8px;
    transform: rotate(45deg);
    animation-delay: 0s;
  }

  &.shape-2 {
    width: 60px;
    height: 60px;
    bottom: 20%;
    left: 5%;
    border-radius: 50%;
    animation-delay: -5s;
  }

  &.shape-3 {
    width: 80px;
    height: 80px;
    top: 50%;
    left: 15%;
    border-radius: 12px;
    transform: rotate(30deg);
    animation-delay: -10s;
  }
}

@keyframes geometric-float {
  0%, 100% {
    transform: translateY(0) rotate(45deg);
  }
  50% {
    transform: translateY(-30px) rotate(50deg);
  }
}

/* 主内容 */
.edit-content {
  position: relative;
  z-index: 10;
  padding: 70px 20px 20px;
}

/* 头像区域 - Brutalist */
.avatar-section-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
}

.avatar-wrapper-brutal {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  border: 4px solid var(--color-obsidian);
  box-shadow: 6px 6px 0 var(--color-obsidian);
  transition: all 0.2s;

  &:active {
    transform: translate(2px, 2px);
    box-shadow: 3px 3px 0 var(--color-obsidian);
  }
}

.avatar-img-brutal {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder-brutal {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-sand);
  color: var(--color-obsidian);
}

.avatar-edit-brutal {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 32px;
  background: var(--color-obsidian);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-cream);
}

.avatar-tip-brutal {
  margin-top: 12px;
  font-family: var(--font-body);
  font-size: 13px;
  font-weight: 500;
  color: var(--color-cream);
  opacity: 0.8;
}

/* Tab卡片 - Brutalist */
.tabs-card-brutal {
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
}

.profile-tabs-brutal {
  :deep(.el-tabs__header) {
    margin-bottom: 24px;
  }

  :deep(.el-tabs__nav-wrap::after) {
    display: none;
  }

  :deep(.el-tabs__item) {
    font-family: var(--font-body);
    font-size: 15px;
    font-weight: 600;
    color: #6B7280;

    &.is-active {
      color: var(--color-primary);
    }
  }

  :deep(.el-tabs__active-bar) {
    background: var(--color-primary-gradient);
    height: 4px;
    border-radius: 2px;
  }

  :deep(.el-form-item__label) {
    font-family: var(--font-body);
    font-size: 14px;
    font-weight: 700;
    color: var(--color-obsidian);
    padding-bottom: 8px;
  }
}

/* 输入框 - Brutalist */
:deep(.input-brutal) {
  .el-input__wrapper {
    border-radius: 12px;
    border: 3px solid var(--color-obsidian);
    box-shadow: 4px 4px 0 var(--color-obsidian);
    background: var(--color-sand);
    transition: all 0.2s;

    &:hover, &.is-focus {
      box-shadow: 6px 6px 0 var(--color-obsidian);
    }

    &:active {
      box-shadow: 2px 2px 0 var(--color-obsidian);
    }
  }

  .el-input__inner {
    font-family: var(--font-body);
    font-size: 15px;
    color: var(--color-obsidian);
  }
}

/* 性别单选 - Brutalist */
.gender-radio-brutal {
  display: flex;
  gap: 16px;
}

.gender-radio-item {
  :deep(.el-radio__input) {
    display: none;
  }

  :deep(.el-radio__label) {
    padding: 10px 24px;
    border: 3px solid var(--color-obsidian);
    border-radius: 10px;
    font-family: var(--font-body);
    font-size: 14px;
    font-weight: 600;
    color: var(--color-obsidian);
    background: var(--color-sand);
    box-shadow: 4px 4px 0 var(--color-obsidian);
    transition: all 0.2s;
  }

  :deep(.el-radio__input.is-checked + .el-radio__label) {
    background: var(--color-secondary);
    color: var(--color-cream);
    border-color: var(--color-secondary);
    box-shadow: 4px 4px 0 var(--color-obsidian);
  }

  :deep(.el-radio__input.is-checked + .el-radio__label:active) {
    box-shadow: 2px 2px 0 var(--color-obsidian);
  }
}

/* 提交按钮 - Brutalist */
.submit-btn-brutal {
  width: 100%;
  padding: 14px 24px;
  font-family: var(--font-body);
  font-size: 16px;
  font-weight: 600;
  color: var(--color-cream);
  background: var(--color-primary-gradient);
  border: 3px solid var(--color-obsidian);
  border-radius: 12px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
  cursor: pointer;
  transition: all 0.2s;

  &:active:not(:disabled) {
    transform: translate(2px, 2px);
    box-shadow: 3px 3px 0 var(--color-obsidian);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}
</style>
