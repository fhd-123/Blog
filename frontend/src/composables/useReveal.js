import { onMounted, onUnmounted } from 'vue'

// 滚动进入视口时添加 .in，触发 .reveal 入场。
// 使用 MutationObserver 监听动态插入的 .reveal（如异步加载的卡片）。
export function useReveal() {
  let io = null
  let mo = null

  const register = (el) => { if (el && io) io.observe(el) }

  const scan = () => {
    document.querySelectorAll('.reveal:not(.in)').forEach(register)
  }

  onMounted(() => {
    if (!('IntersectionObserver' in window)) {
      document.querySelectorAll('.reveal').forEach((e) => e.classList.add('in'))
      return
    }
    io = new IntersectionObserver((entries) => {
      entries.forEach((en) => {
        if (en.isIntersecting) {
          en.target.classList.add('in')
          io.unobserve(en.target)
        }
      })
    }, { threshold: 0.12, rootMargin: '0px 0px -8% 0px' })

    scan()
    // 监听后续异步渲染的节点
    mo = new MutationObserver((muts) => {
      for (const m of muts) {
        m.addedNodes.forEach((node) => {
          if (node.nodeType === 1) {
            if (node.classList && node.classList.contains('reveal')) register(node)
            if (node.querySelectorAll) node.querySelectorAll('.reveal:not(.in)').forEach(register)
          }
        })
      }
    })
    mo.observe(document.body, { childList: true, subtree: true })
  })

  onUnmounted(() => {
    if (io) io.disconnect()
    if (mo) mo.disconnect()
  })
}
