import { createRouter, createWebHashHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/articles', redirect: '/archive' },
  { path: '/archive', name: 'Archive', component: () => import('../views/Archive.vue') },
  { path: '/projects', name: 'Projects', component: () => import('../views/Projects.vue') },
  { path: '/articles/:id', name: 'ArticleDetail', component: () => import('../views/ArticleDetail.vue') },
  { path: '/photowall', name: 'PhotoWall', component: () => import('../views/PhotoWall.vue') },
  { path: '/music', name: 'Music', component: () => import('../views/Music.vue') },
  { path: '/tree', name: 'Tree', component: () => import('../views/Tree.vue') },
  { path: '/moments', name: 'Moments', component: () => import('../views/Moments.vue') },
  { path: '/chatter', name: 'Chatter', component: () => import('../views/Chatter.vue') },
  { path: '/friends', name: 'Friends', component: () => import('../views/Friends.vue') },
  { path: '/about', name: 'About', component: () => import('../views/About.vue') },
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/Login.vue') },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { requireAuth: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'articles', name: 'AdminArticles', component: () => import('../views/admin/Articles.vue') },
      { path: 'articles/edit/:id', name: 'AdminArticleEdit', component: () => import('../views/admin/ArticleEdit.vue') },
      { path: 'articles/create', name: 'AdminArticleCreate', component: () => import('../views/admin/ArticleEdit.vue') },
      { path: 'categories', name: 'AdminCategories', component: () => import('../views/admin/Categories.vue') },
      { path: 'profile', name: 'AdminProfile', component: () => import('../views/admin/Profile.vue') },
      { path: 'projects', name: 'AdminProjects', component: () => import('../views/admin/Projects.vue') },
      { path: 'photos', name: 'AdminPhotos', component: () => import('../views/admin/Photos.vue') },
      { path: 'musics', name: 'AdminMusics', component: () => import('../views/admin/Musics.vue') },
      { path: 'tree', name: 'AdminTree', component: () => import('../views/admin/TreeAdmin.vue') },
      { path: 'moments', name: 'AdminMoments', component: () => import('../views/admin/MomentsAdmin.vue') },
      { path: 'essays', name: 'AdminEssays', component: () => import('../views/admin/Essays.vue') },
      { path: 'friends', name: 'AdminFriends', component: () => import('../views/admin/Friends.vue') },
      { path: 'comments', name: 'AdminComments', component: () => import('../views/admin/Comments.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.afterEach(() => {
  window.scrollTo({ top: 0, behavior: 'auto' })
})

router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth && !localStorage.getItem('token')) {
    ElMessage.warning('请先登录')
    next('/admin/login')
  } else {
    next()
  }
})

export default router
