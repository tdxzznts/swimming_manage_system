<template>
  <div class="feedback-create-page">
    <!-- 几何背景 -->
    <div class="geometric-bg">
      <div class="gradient-bg"></div>
      <div class="diagonal-stripes"></div>
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
    </div>

    <!-- 主内容 -->
    <div class="create-content">
      <!-- 提示卡片 -->
      <div class="tips-card-brutal">
        <div class="tips-icon-brutal">
          <el-icon><InfoFilled /></el-icon>
        </div>
        <div class="tips-content-brutal">
          <p class="tips-title-brutal">感谢您的反馈</p>
          <p class="tips-text-brutal">我们会认真对待每一条反馈，尽快处理并回复您</p>
        </div>
      </div>

      <!-- 表单卡片 -->
      <div class="form-card-brutal">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="formRules"
          label-position="top"
          class="feedback-form-brutal"
        >
          <!-- 反馈类型 -->
          <el-form-item label="反馈类型" prop="feedbackType">
            <el-radio-group v-model="formData.feedbackType" class="type-radio-group-brutal">
              <el-radio value="1" class="type-radio-brutal type-suggestion-brutal">
                <div class="radio-content-brutal">
                  <el-icon><ChatDotRound /></el-icon>
                  <span>建议</span>
                </div>
              </el-radio>
              <el-radio value="2" class="type-radio-brutal type-complaint-brutal">
                <div class="radio-content-brutal">
                  <el-icon><WarningFilled /></el-icon>
                  <span>投诉</span>
                </div>
              </el-radio>
            </el-radio-group>
          </el-form-item>

          <!-- 反馈标题 -->
          <el-form-item label="反馈标题" prop="title">
            <el-input
              v-model="formData.title"
              placeholder="请简要描述您的问题或建议"
              maxlength="50"
              show-word-limit
              clearable
              class="form-input-brutal"
            />
          </el-form-item>

          <!-- 反馈内容 -->
          <el-form-item label="反馈详情" prop="content">
            <el-input
              v-model="formData.content"
              type="textarea"
              :rows="8"
              placeholder="请详细描述您的问题或建议，以便我们更好地为您服务..."
              maxlength="500"
              show-word-limit
              class="form-textarea-brutal"
            />
          </el-form-item>

          <!-- 联系方式 -->
          <el-form-item label="联系方式" prop="contactInfo">
            <el-input
              v-model="formData.contactInfo"
              placeholder="请输入您的手机号或邮箱，方便我们联系您"
              clearable
              class="form-input-brutal"
            />
          </el-form-item>

          <!-- 满意度评分 -->
          <el-form-item label="满意度评分" prop="satisfaction">
            <div class="rating-container-brutal">
              <div class="rating-stars-brutal">
                <el-icon
                  v-for="star in 5"
                  :key="star"
                  :size="32"
                  class="star-icon-brutal"
                  :class="{ active: star <= (formData.satisfaction || 0) }"
                  @click="handleRate(star)"
                >
                  <StarFilled v-if="star <= (formData.satisfaction || 0)" />
                  <Star v-else />
                </el-icon>
              </div>
              <div class="rating-text-brutal">
                <span v-if="!formData.satisfaction">请点击星星评分</span>
                <span v-else>{{ getRatingText(formData.satisfaction) }}</span>
              </div>
            </div>
          </el-form-item>

          <!-- 提交按钮 -->
          <el-form-item>
            <button
              type="button"
              :disabled="submitting"
              class="submit-btn-brutal"
              @click="handleSubmit"
            >
              <span v-if="!submitting">
                <el-icon><Promotion /></el-icon>
                提交反馈
              </span>
              <span v-else>提交中...</span>
            </button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  InfoFilled,
  ChatDotRound,
  WarningFilled,
  Promotion,
  StarFilled,
  Star
} from '@element-plus/icons-vue'
import { createFeedback } from '@/api/feedback'
import type { FeedbackForm } from '@/types/feedback'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)

// 表单数据
const formData = reactive<FeedbackForm>({
  feedbackType: '1',
  title: '',
  content: '',
  contactInfo: '',
  satisfaction: undefined
})

// 表单验证规则
const formRules: FormRules = {
  feedbackType: [
    { required: true, message: '请选择反馈类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入反馈标题', trigger: 'blur' },
    { min: 5, max: 50, message: '标题长度在 5 到 50 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入反馈详情', trigger: 'blur' },
    { min: 10, max: 500, message: '反馈内容在 10 到 500 个字符', trigger: 'blur' }
  ],
  contactInfo: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    {
      pattern: /^1[3-9]\d{9}$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
      message: '请输入正确的手机号或邮箱',
      trigger: 'blur'
    }
  ]
}

async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    const response = await createFeedback(formData)

    if (response.code === 200) {
      ElMessage.success('反馈提交成功，我们会尽快处理')
      // 跳转到反馈列表页面
      setTimeout(() => {
        router.replace('/feedback')
      }, 1500)
    }
  } catch (error: any) {
    if (error !== false) {
      console.error('提交反馈失败:', error)
      ElMessage.error(error.msg || error.message || '提交失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

// 评分点击事件
function handleRate(star: number) {
  formData.satisfaction = star
}

// 获取评分文本
function getRatingText(rating: number): string {
  const texts = ['', '非常不满意', '不满意', '一般', '满意', '非常满意']
  return texts[rating] || ''
}
</script>

<style scoped lang="scss">
.feedback-create-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
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
.create-content {
  position: relative;
  z-index: 10;
  padding: 70px 16px 20px;
}

/* 提示卡片 - Brutalist */
.tips-card-brutal {
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 20px;
  display: flex;
  gap: 14px;
  align-items: flex-start;
  box-shadow: 6px 6px 0 var(--color-obsidian);
}

.tips-icon-brutal {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--color-primary-gradient);
  border: 3px solid var(--color-obsidian);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-cream);
  flex-shrink: 0;
  box-shadow: 4px 4px 0 var(--color-obsidian);
}

.tips-content-brutal {
  flex: 1;
}

.tips-title-brutal {
  font-family: var(--font-display);
  font-size: 16px;
  font-weight: 700;
  color: var(--color-obsidian);
  margin: 0 0 6px;
}

.tips-text-brutal {
  font-family: var(--font-body);
  font-size: 13px;
  color: #6B7280;
  margin: 0;
  line-height: 1.6;
}

/* 表单卡片 - Brutalist */
.form-card-brutal {
  background: var(--color-cream);
  border: 3px solid var(--color-obsidian);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 6px 6px 0 var(--color-obsidian);
}

.feedback-form-brutal {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-form-item__label) {
    font-family: var(--font-body);
    font-size: 15px;
    font-weight: 700;
    color: var(--color-obsidian);
    margin-bottom: 8px;
  }
}

/* 类型选择 - Brutalist */
.type-radio-group-brutal {
  display: flex;
  width: 100%;
  gap: 12px;
}

.type-radio-brutal {
  flex: 1;
  margin: 0;
  padding: 0;
  position: relative;
  z-index: 1;

  :deep(.el-radio__input) {
    display: none;
  }

  :deep(.el-radio__label) {
    padding: 0;
    width: 100%;
  }
}

.type-suggestion-brutal {
  :deep(.el-radio__input.is-checked + .el-radio__label) {
    .radio-content-brutal {
      background: var(--color-secondary);
      color: var(--color-cream);
      border-color: var(--color-secondary);
      box-shadow: 4px 4px 0 var(--color-obsidian);
    }
  }

  :deep(.el-radio__input:not(.is-checked) + .el-radio__label) {
    .radio-content-brutal {
      background: var(--color-cream);
      color: var(--color-secondary);
      border: 3px solid var(--color-secondary);

      .el-icon {
        color: var(--color-secondary);
      }
    }
  }
}

.type-complaint-brutal {
  :deep(.el-radio__input.is-checked + .el-radio__label) {
    .radio-content-brutal {
      background: var(--color-primary);
      color: var(--color-cream);
      border-color: var(--color-primary);
      box-shadow: 4px 4px 0 var(--color-obsidian);
    }
  }

  :deep(.el-radio__input:not(.is-checked) + .el-radio__label) {
    .radio-content-brutal {
      background: var(--color-cream);
      color: var(--color-primary);
      border: 3px solid var(--color-primary);

      .el-icon {
        color: var(--color-primary);
      }
    }
  }
}

.radio-content-brutal {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border-radius: 12px;
  transition: all 0.2s;
  cursor: pointer;
  min-height: 48px;

  .el-icon {
    font-size: 20px;
  }

  span {
    font-family: var(--font-body);
    font-size: 14px;
    font-weight: 600;
  }
}

/* 表单输入 - Brutalist */
:deep(.form-input-brutal) {
  .el-input__wrapper {
    border-radius: 12px;
    padding: 12px 16px;
    border: 3px solid var(--color-obsidian);
    box-shadow: 4px 4px 0 var(--color-obsidian);
    transition: all 0.2s;
    background: var(--color-sand);

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
    font-weight: 500;
  }
}

:deep(.form-textarea-brutal) {
  .el-textarea__inner {
    border-radius: 12px;
    padding: 12px 16px;
    font-family: var(--font-body);
    font-size: 15px;
    line-height: 1.8;
    border: 3px solid var(--color-obsidian);
    box-shadow: 4px 4px 0 var(--color-obsidian);
    background: var(--color-sand);
    transition: all 0.2s;

    &:hover, &:focus {
      box-shadow: 6px 6px 0 var(--color-obsidian);
    }

    &:active {
      box-shadow: 2px 2px 0 var(--color-obsidian);
    }
  }
}

/* 满意度评分 - Brutalist */
.rating-container-brutal {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 20px;
  background: var(--color-sand);
  border: 3px solid var(--color-obsidian);
  border-radius: 12px;
  box-shadow: 4px 4px 0 var(--color-obsidian);
  box-sizing: border-box;
}

.rating-stars-brutal {
  display: flex;
  gap: 10px;
  align-items: center;
}

.star-icon-brutal {
  cursor: pointer;
  color: #CBD5E1;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: scale(1.15);
  }

  &:active {
    transform: scale(1.05);
  }

  &.active {
    color: var(--color-amber);

    &:hover {
      color: #FBBF24;
    }
  }
}

.rating-text-brutal {
  font-family: var(--font-body);
  font-size: 14px;
  font-weight: 600;
  color: var(--color-obsidian);
  text-align: center;
  min-height: 20px;
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
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;

  &:active:not(:disabled) {
    transform: translate(2px, 2px);
    box-shadow: 3px 3px 0 var(--color-obsidian);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .el-icon {
    font-size: 18px;
  }
}
</style>
