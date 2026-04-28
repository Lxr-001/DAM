import Vue from 'vue'
import Router from 'vue-router'
import { constantRouterMap } from '@/config/router.config'

Vue.use(Router)

const router =  new Router({
  mode: 'hash',
  base: process.env.BASE_URL,
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
})
/**
 * start保密检查使用（不允许地址栏输入地址跳转  只能内部页面跳转from.name === null）
 */
router.beforeEach((to, from, next) => {
  // console.log("123："+to.path);
  // if (from.name === null   && to.path !== '/user/login'&& to.path !== '/404' ){///user/login白名单输入url
  //    //next('/');//重定向到主页 && !store.state.isInternalNavigation
  //   next({ path: '/404', query:{resourceType:"browserEnter"}})
  //   return;
  // }
  next();
})
// end保密检查使用

export default router;