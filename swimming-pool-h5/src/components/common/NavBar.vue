<template>
  <div class="nav-bar" :class="{ 'safe-area': safeArea }">
    <!-- Decorative bottom line -->
    <div class="nav-bar__line"></div>

    <!-- Geometric decorations -->
    <div class="nav-bar__deco">
      <div class="geometric-circle circle-1"></div>
      <div class="geometric-triangle triangle-1"></div>
    </div>

    <div class="nav-bar__left" @click="handleBack">
      <div class="back-btn" v-if="showBack">
        <div class="back-btn__bg"></div>
        <el-icon :size="20"><ArrowLeft /></el-icon>
      </div>
    </div>

    <div class="nav-bar__title">
      <span class="title-text">{{ title }}</span>
    </div>

    <div class="nav-bar__right">
      <slot name="right"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeft } from '@element-plus/icons-vue'

interface Props {
  title?: string
  showBack?: boolean
  safeArea?: boolean
}

withDefaults(defineProps<Props>(), {
  title: '',
  showBack: true,
  safeArea: true
})

const emit = defineEmits<{
  back: []
}>()

const handleBack = () => {
  emit('back')
  if (window.history.length > 1) {
    window.history.back()
  }
}
</script>

<style scoped lang="scss">
.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 16px;
  background: #fff;
  border: none;
  box-shadow: 0 2px 16px rgba(11, 37, 69, 0.06);

  &.safe-area {
    padding-top: calc(12px + env(safe-area-inset-top));
    height: calc(56px + env(safe-area-inset-top));
  }

  // Decorative gradient line at bottom
  &__line {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg,
      #FF6B6B 0%,
      #20C997 25%,
      #FFA94D 50%,
      #20C997 75%,
      #FF6B6B 100%
    );
    background-size: 200% 100%;
    animation: gradient-shift 8s linear infinite;
  }

  // Decorative geometric elements
  &__deco {
    position: absolute;
    inset: 0;
    pointer-events: none;
    overflow: hidden;
  }

  &__title {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 60px;
  }

  &__left,
  &__right {
    width: 60px;
    display: flex;
    align-items: center;
    flex-shrink: 0;
  }

  &__left {
    cursor: pointer;
  }
}

// Back button styling
.back-btn {
  position: relative;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);

  &:active {
    transform: scale(0.95);
  }

  &__bg {
    position: absolute;
    inset: 0;
    background: var(--color-cream);
    border-radius: 12px;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  }

  &:active &__bg {
    background: var(--color-sand);
  }

  .el-icon {
    position: relative;
    z-index: 1;
    color: var(--color-obsidian);
    transition: color 0.2s;
  }
}

// Title styling
.title-text {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
  color: var(--color-obsidian);
  letter-spacing: -0.3px;
  line-height: 1.2;
}

// Geometric decorations
.geometric-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.08) 0%, rgba(255, 169, 77, 0.08) 100%);

  &.circle-1 {
    width: 80px;
    height: 80px;
    top: -20px;
    right: -20px;
    animation: float 6s ease-in-out infinite;
  }
}

.geometric-triangle {
  position: absolute;
  width: 0;
  height: 0;
  border-left: 20px solid transparent;
  border-right: 20px solid transparent;
  border-bottom: 35px solid rgba(32, 201, 151, 0.06);
  transform: rotate(-15deg);

  &.triangle-1 {
    top: 20px;
    left: 10%;
    animation: float 8s ease-in-out infinite reverse;
  }
}

// Animations
@keyframes gradient-shift {
  0% { background-position: 0% 50%; }
  100% { background-position: 200% 50%; }
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(-15deg); }
  50% { transform: translateY(-8px) rotate(-10deg); }
}
</style>
