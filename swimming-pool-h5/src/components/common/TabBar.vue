<template>
  <div class="tab-bar" :class="{ 'safe-area': safeArea }">
    <!-- Decorative top line -->
    <div class="tab-bar__line"></div>

    <div
      v-for="tab in tabs"
      :key="tab.name"
      class="tab-bar__item"
      :class="{ active: modelValue === tab.name }"
      @click="handleTabClick(tab)"
    >
      <div class="tab-bar__icon-wrapper">
        <div class="tab-bar__icon-bg"></div>
        <el-icon :size="24">
          <component :is="tab.icon" />
        </el-icon>
        <div class="tab-bar__dot"></div>
      </div>
      <span class="tab-bar__label">{{ tab.label }}</span>
    </div>

    <!-- Decorative geometric shapes -->
    <div class="tab-bar__deco">
      <div class="geometric-shape shape-1"></div>
      <div class="geometric-shape shape-2"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

interface Tab {
  name: string
  label: string
  icon: string
  path: string
}

interface Props {
  modelValue: string
  tabs: Tab[]
  safeArea?: boolean
}

defineProps<Props>()

const emit = defineEmits<{
  'update:modelValue': [value: string]
  change: [tab: Tab]
}>()

const router = useRouter()

const handleTabClick = (tab: Tab) => {
  emit('update:modelValue', tab.name)
  emit('change', tab)
  router.push(tab.path)
}
</script>

<style scoped lang="scss">
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  justify-content: space-around;
  align-items: flex-start;
  padding: 12px 16px 0;
  background: #fff;
  border: none;
  box-shadow: 0 -4px 24px rgba(11, 37, 69, 0.08);

  &.safe-area {
    padding-bottom: calc(12px + env(safe-area-inset-bottom));
  }

  // Decorative top accent line
  &__line {
    position: absolute;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 120px;
    height: 3px;
    background: linear-gradient(90deg, #FF6B6B 0%, #20C997 50%, #FFA94D 100%);
    border-radius: 0 0 3px 3px;
  }

  &__item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    position: relative;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &:active {
      transform: scale(0.95);
    }
  }

  &__icon-wrapper {
    position: relative;
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  &__icon-bg {
    position: absolute;
    inset: 0;
    background: var(--color-cream);
    border-radius: 14px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  &__dot {
    position: absolute;
    bottom: 2px;
    width: 4px;
    height: 4px;
    background: var(--color-primary);
    border-radius: 50%;
    opacity: 0;
    transform: scale(0);
    transition: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  }

  .el-icon {
    position: relative;
    z-index: 1;
    color: var(--color-taupe);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  &__label {
    font-family: var(--font-body);
    font-size: 11px;
    font-weight: 500;
    color: var(--color-taupe);
    letter-spacing: 0.03em;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }

  // Active state
  &__item.active {
    .tab-bar__icon-wrapper {
      transform: translateY(-4px);
    }

    .tab-bar__icon-bg {
      background: linear-gradient(135deg, #FF6B6B 0%, #FFA94D 100%);
      box-shadow: 0 8px 16px rgba(255, 107, 107, 0.3);
    }

    .tab-bar__dot {
      opacity: 1;
      transform: scale(1);
    }

    .el-icon {
      color: #fff;
    }

    .tab-bar__label {
      color: var(--color-obsidian);
      font-weight: 600;
    }
  }

  // Decorative geometric shapes
  &__deco {
    position: absolute;
    inset: 0;
    pointer-events: none;
    overflow: hidden;
  }
}

.geometric-shape {
  position: absolute;
  opacity: 0.05;
  background: var(--color-obsidian);

  &.shape-1 {
    width: 40px;
    height: 40px;
    bottom: 20px;
    left: 10%;
    transform: rotate(45deg);
    animation: geometric-float 8s ease-in-out infinite;
  }

  &.shape-2 {
    width: 24px;
    height: 24px;
    bottom: 30px;
    right: 15%;
    border-radius: 50%;
    animation: geometric-float 10s ease-in-out infinite reverse;
  }
}

@keyframes geometric-float {
  0%, 100% {
    transform: translateY(0) rotate(45deg);
  }
  50% {
    transform: translateY(-8px) rotate(50deg);
  }
}
</style>
