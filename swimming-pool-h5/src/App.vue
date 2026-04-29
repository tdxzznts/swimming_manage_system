<template>
  <router-view v-slot="{ Component }">
    <keep-alive :include="['Home', 'Reservation', 'Order', 'Profile']">
      <component :is="Component" />
    </keep-alive>
  </router-view>
</template>

<script setup lang="ts">
</script>

<style>
#app {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
}
</style>
