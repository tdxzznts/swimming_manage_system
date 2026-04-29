<template>
  <div class="default-layout">
    <!-- Navigation Bar -->
    <NavBar v-if="showNavBar" :title="pageTitle" :show-back="showBack" />

    <!-- Content Area -->
    <div class="layout-content" :class="{ 'safe-area-top': showNavBar }">
      <router-view />
    </div>

    <!-- Tab Bar -->
    <TabBar v-if="showTabBar" v-model="activeTab" :tabs="tabs" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import TabBar from '@/components/common/TabBar.vue'

const route = useRoute()

const activeTab = ref('Home')

const tabs = [
  { name: 'Home', label: '首页', icon: 'HomeFilled', path: '/' },
  { name: 'Reservation', label: '预约', icon: 'Calendar', path: '/reservation' },
  { name: 'Order', label: '订单', icon: 'Tickets', path: '/order' },
  { name: 'Profile', label: '我的', icon: 'User', path: '/profile' }
]

const showNavBar = computed(() => {
  return route.meta.showNavBar !== false
})

const showTabBar = computed(() => {
  return route.meta.showTabBar !== false
})

const showBack = computed(() => {
  return route.meta.showBack !== false
})

const pageTitle = computed(() => {
  return route.meta.title as string || ''
})

// Update active tab based on route
watch(
  () => route.name,
  (newName) => {
    if (newName) {
      activeTab.value = newName as string
    }
  },
  { immediate: true }
)
</script>

<style scoped lang="scss">
.default-layout {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-cream);
}

.layout-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 70px; // Account for TabBar height
}

.safe-area-top {
  padding-top: calc(56px + env(safe-area-inset-top)); // Account for NavBar height
}
</style>
