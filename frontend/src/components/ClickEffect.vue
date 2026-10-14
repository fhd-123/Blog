<template>
  <canvas class="click-canvas" ref="canvasEl"></canvas>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

// 对齐参照站 ClickEffect：点击处扩散靛蓝涟漪 + 淡实心圆，带 glow
const canvasEl = ref(null)
let ctx = null
let ripples = []
let raf = null

const ACCENT = [129, 140, 248] // 参照站使用的靛蓝 #818cf8

class Ripple {
  constructor(x, y) {
    this.x = x
    this.y = y
    this.r = 0
    this.maxR = 60
    this.opacity = 0.6
    this.velocity = 2.5
  }
  update() {
    this.r += this.velocity
    this.velocity *= 0.96
    this.opacity -= 0.015
  }
  draw(c) {
    c.beginPath()
    c.arc(this.x, this.y, this.r, 0, Math.PI * 2)
    c.strokeStyle = `rgba(${ACCENT.join(',')}, ${this.opacity})`
    c.lineWidth = 2
    c.stroke()

    c.beginPath()
    c.arc(this.x, this.y, this.r * 0.5, 0, Math.PI * 2)
    c.fillStyle = `rgba(${ACCENT.join(',')}, ${this.opacity * 0.3})`
    c.fill()
  }
}

const resize = () => {
  if (!canvasEl.value) return
  canvasEl.value.width = window.innerWidth
  canvasEl.value.height = window.innerHeight
}

const handleClick = (e) => {
  ripples.push(new Ripple(e.clientX, e.clientY))
}

const animate = () => {
  if (!ctx) return
  ctx.clearRect(0, 0, canvasEl.value.width, canvasEl.value.height)
  ctx.shadowBlur = 15
  ctx.shadowColor = `rgba(${ACCENT.join(',')}, 0.5)`
  for (let i = 0; i < ripples.length; i++) {
    ripples[i].update()
    ripples[i].draw(ctx)
    if (ripples[i].opacity <= 0) {
      ripples.splice(i, 1)
      i--
    }
  }
  raf = requestAnimationFrame(animate)
}

onMounted(() => {
  ctx = canvasEl.value.getContext('2d')
  const reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  resize()
  window.addEventListener('resize', resize)
  if (reduced) return
  window.addEventListener('click', handleClick)
  animate()
})

onUnmounted(() => {
  if (raf) cancelAnimationFrame(raf)
  window.removeEventListener('resize', resize)
  window.removeEventListener('click', handleClick)
})
</script>

<style scoped>
.click-canvas { position: fixed; inset: 0; pointer-events: none; z-index: 9998; }
</style>
