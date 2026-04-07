<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { NButton, NCard, NText, NUl, NLi, NSpin, NSelect } from 'naive-ui'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import * as echarts from 'echarts'
import { fetchDailyNewUserStats } from '@/api/admin'

const router = useRouter()
const auth = useAuthStore()
const chartRef = ref(null)
const loading = ref(true)
const stats = ref([])
const chartInstance = ref(null)
const selectedDays = ref(7)

const dayOptions = [
  { label: '最近 7 天', value: 7 },
  { label: '最近 14 天', value: 14 },
  { label: '最近 30 天', value: 30 }
]

const greetings = [
  '主人，美好的一天要开始写 Bug 了吗？',
  '今天也是元气满满地制造 Bug 的一天呢！',
  '代码写得好，头发掉得少（大概）',
  'Hello World! 又是新的一天~',
  '主人，服务器正在想念你的代码',
  '今日宜写代码，忌摸鱼（划掉）',
  '愿你的代码永远编译通过，Bug 绕道而行',
  '主人，今天的你也是闪闪发光的程序员呢',
  'Ctrl+C Ctrl+V 是程序员的浪漫',
  '警告：检测到主人即将开始高强度编码',
  '主人，咖啡已备好，请开始你的表演',
  '今日运势：宜重构，忌删库跑路'
]

const greeting = ref('')

function getRandomGreeting() {
  const hour = new Date().getHours()
  let timeGreeting = ''
  if (hour >= 5 && hour < 9) {
    timeGreeting = '早安，'
  } else if (hour >= 9 && hour < 12) {
    timeGreeting = '上午好，'
  } else if (hour >= 12 && hour < 14) {
    timeGreeting = '中午好，'
  } else if (hour >= 14 && hour < 18) {
    timeGreeting = '下午好，'
  } else if (hour >= 18 && hour < 22) {
    timeGreeting = '晚上好，'
  } else {
    timeGreeting = '夜深了，'
  }
  const randomMsg = greetings[Math.floor(Math.random() * greetings.length)]
  return timeGreeting + randomMsg
}

async function loadStats() {
  loading.value = true
  try {
    const res = await fetchDailyNewUserStats(selectedDays.value)
    stats.value = res || []
    renderChart()
  } catch (e) {
    console.error('加载统计数据失败', e)
  } finally {
    loading.value = false
  }
}

function renderChart() {
  if (!chartRef.value) return

  if (chartInstance.value) {
    chartInstance.value.dispose()
  }

  chartInstance.value = echarts.init(chartRef.value)

  const dates = stats.value.length
    ? stats.value.map(item => {
        const date = new Date(item.date)
        return `${date.getMonth() + 1}/${date.getDate()}`
      })
    : []

  const counts = stats.value.length
    ? stats.value.map(item => item.count)
    : []

  const total = counts.length ? counts.reduce((a, b) => a + b, 0) : 0

  const option = {
    backgroundColor: 'transparent',
    title: {
      text: '新增用户趋势',
      subtext: stats.value.length
        ? `共 ${total} 位新用户`
        : '暂无数据',
      left: 'center',
      top: 10
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: 70, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates.length ? dates : ['暂无数据'],
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        data: counts.length ? counts : [0],
      }
    ]
  }

  chartInstance.value.setOption(option)
}

function handleResize() {
  chartInstance.value?.resize()
}

onMounted(() => {
  greeting.value = getRandomGreeting()
  loadStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance.value?.dispose()
})
</script>

<template>
  <div>
    <n-card title="仪表盘">
      <n-text>{{ greeting }}</n-text>
    </n-card>

    <n-card title="新增用户统计" style="margin-top: 16px">
      <div style="margin-bottom: 12px">
        <n-select
          v-model:value="selectedDays"
          :options="dayOptions"
          style="width: 120px"
          @update:value="loadStats"
        />
      </div>
      <n-spin :show="loading">
        <div ref="chartRef" style="width: 100%; height: 300px"></div>
      </n-spin>
    </n-card>
  </div>
</template>
