import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/request'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页', keepAlive: true }
      },
      {
        path: 'reservation',
        name: 'Reservation',
        component: () => import('@/views/reservation/index.vue'),
        meta: { title: '预约', keepAlive: true, requiresAuth: true }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单', keepAlive: true, requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '我的', keepAlive: true, requiresAuth: true }
      },
      {
        path: 'membership',
        name: 'Membership',
        component: () => import('@/views/membership/index.vue'),
        meta: { title: '会员卡', keepAlive: true, requiresAuth: true, showTabBar: false }
      },
      {
        path: 'profile-edit',
        name: 'ProfileEdit',
        component: () => import('@/views/profile-edit/index.vue'),
        meta: { title: '个人信息', requiresAuth: true, showTabBar: false }
      },
      {
        path: 'records',
        name: 'Records',
        component: () => import('@/views/records/index.vue'),
        meta: { title: '消费记录', requiresAuth: true, showTabBar: false }
      },
      {
        path: 'timeslots',
        name: 'TimeSlots',
        component: () => import('@/views/timeslots/index.vue'),
        meta: { title: '选择时段', requiresAuth: true, showTabBar: false }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/views/announcements/index.vue'),
        meta: { title: '系统公告', requiresAuth: true, showTabBar: false }
      },
      {
        path: 'announcement/:id',
        name: 'AnnouncementDetail',
        component: () => import('@/views/announcement-detail/index.vue'),
        meta: { title: '公告详情', requiresAuth: true, showTabBar: false }
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('@/views/feedback/index.vue'),
        meta: { title: '建议反馈', keepAlive: true, requiresAuth: true, showTabBar: false }
      },
      {
        path: 'feedback/create',
        name: 'FeedbackCreate',
        component: () => import('@/views/feedback/create.vue'),
        meta: { title: '新建反馈', requiresAuth: true, showTabBar: false }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 白名单 - 不需要登录的页面
const whiteList = ['/login', '/register', '/']

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 智能游泳馆`
  }

  // 检查是否需要登录
  const hasToken = getToken()

  if (hasToken) {
    // 已登录
    if (to.path === '/login') {
      // 如果已登录，访问登录页时跳转到首页
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path) || !to.meta.requiresAuth) {
      // 在白名单中或不需要登录，直接访问
      next()
    } else {
      // 需要登录，跳转到登录页，携带重定向地址
      next({
        path: '/login',
        query: {
          redirect: to.fullPath
        }
      })
    }
  }
})

export default router
