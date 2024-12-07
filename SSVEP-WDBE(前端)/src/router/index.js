import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import MainView from '@/views/MainView.vue'
import HomeView from '@/views/HomeView.vue'
import ChartData from '@/views/ChartData.vue'
import DataBase from '@/views/DataBase.vue'
import EyeHealth from '@/views/EyeHealth.vue'
import HistoricalAssessments from '@/views/HistoricalAssessments.vue'
import SettingView from '@/views/SettingView.vue'
import TreatmentRecommended from '@/views/TreatmentRecommended.vue'
import VisualAnalysis from '@/views/VisualAnalysis.vue'
import VisualDetection from '@/views/VisualDetection.vue'
import RealTimeData from '@/views/RealTimeData.vue'
import LocalData from '@/views/LocalData.vue'
import ColorPerception from '@/views/ColorPerception.vue'
import VisualAcuityTesting from '@/views/VisualAcuityTesting.vue'
import SensitivityTesting from '@/views/SensitivityTesting.vue'
import GlaucomaTest from '@/views/GlaucomaTest.vue'
import CataractDetection from '@/views/CataractDetection.vue'
import MacularTest from '@/views/MacularTest.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Login',
      component: LoginView
    },
    {
      path: '/main',
      name: 'Main',
      component: MainView,
      meta: { requiresAuth: true }, 
      children:[
        {
          path: 'HomeView',
          name: 'Home',
          component: HomeView,
          meta: { requiresAuth: true }
        },
        {
          path: 'ChartData',
          name: 'ChartData',
          component: ChartData,
          meta: { requiresAuth: true }
        },
        {
          path: 'DataBase',
          name: 'DataBase',
          component: DataBase,
          meta: { requiresAuth: true },
          children: [
            {
              path: 'RealTimeData',
              name: 'RealTimeData',
              component: RealTimeData,
              meta: { requiresAuth: true }
            },
            {
              path: 'LocalData',
              name: 'LocalData',
              component: LocalData,
              meta: { requiresAuth: true }
            },
          ]
        },
        {
          path: 'EyeHealth',
          name: 'EyeHealth',
          component: EyeHealth,
          meta: { requiresAuth: true },
          children: [
            {
              path: 'GlaucomaTest',
              name: 'GlaucomaTest',
              component: GlaucomaTest,
              meta: { requiresAuth: true }
            },
            {
              path: 'CataractDetection',
              name: 'CataractDetection',
              component: CataractDetection,
              meta: { requiresAuth: true }
            },
            {
              path: 'MacularTest',
              name: 'MacularTest',
              component: MacularTest,
              meta: { requiresAuth: true }
            },
          ]
        },
        {
          path: 'HistoricalAssessments',
          name: 'HistoricalAssessments',
          component: HistoricalAssessments,
          meta: { requiresAuth: true }
        },
        {
          path: 'SettingView',
          name: 'SettingView',
          component: SettingView,
          meta: { requiresAuth: true }
        },
        {
          path: 'TreatmentRecommended',
          name: 'TreatmentRecommended',
          component: TreatmentRecommended,
          meta: { requiresAuth: true }
        },
        {
          path: 'VisualAnalysis',
          name: 'VisualAnalysis',
          component: VisualAnalysis,
          meta: { requiresAuth: true }
        },
        {
          path: 'VisualDetection',
          name: 'VisualDetection',
          component: VisualDetection,
          meta: { requiresAuth: true },
          children: [
            {
              path: 'ColorPerception',
              name: 'ColorPerception',
              component: ColorPerception,
              meta: { requiresAuth: true }
            },
            {
              path: 'VisualAcuityTesting',
              name: 'VisualAcuityTesting',
              component: VisualAcuityTesting,
              meta: { requiresAuth: true }
            },
            {
              path: 'SensitivityTesting',
              name: 'SensitivityTesting',
              component: SensitivityTesting,
              meta: { requiresAuth: true }
            },
          ]
        },
      ]
    },
  ],
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('authToken')

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'Login' }) // 未登录则跳转到登录页面
  } else {
    next() // 已登录则正常访问目标页面
  }
})

export default router
