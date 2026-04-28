<template>
  <div>
    <div class="main" v-show="showAdminContext == true ">
      <!-- Key登录加载状态 -->
      <div v-if="keyLoginLoading" class="key-login-loading">
        <a-spin size="large">
          <div class="loading-text">正在验证身份信息...</div>
        </a-spin>
      </div>

      <!-- Key登录失败，显示手动登录选项 -->
      <div v-else-if="showManualLogin" class="key-login-failed">
        <a-result
          status="error"
          title="无访问权限"
          sub-title="未能获取用户有效信息，请使用手动登录"
        >
          <!--        <a-button slot="extra" type="primary" @click="showManualLoginForm">-->
          <!--          手动登录-->
          <!--        </a-button>-->
        </a-result>
      </div>

      <!-- 手动登录表单 -->
      <a-form-model v-else class="user-layout-login" @keyup.enter.native="handleSubmit">
        <!--      <span style="font-size: 24px;font-weight:bold;color:rgb(255,57,57);">[{{securityText }}]</span>-->
        <a-tabs :activeKey="customActiveKey" :tabBarStyle="{ textAlign: 'center', borderBottom: 'unset' }"
                @change="handleTabClick">
          <a-tab-pane key="tab1" tab="仅限三员用户登录">
            <login-account ref="alogin" @validateFail="validateFail" @success="requestSuccess"
                           @fail="requestFailed"></login-account>
          </a-tab-pane>
        </a-tabs>

  <!--      <a-form-model-item>-->
  <!--        <a-checkbox @change="handleRememberMeChange" default-checked>自动登录</a-checkbox>-->
  <!--&lt;!&ndash;        <router-link :to="{ name: 'alteration'}" class="forge-password" style="float: right;">&ndash;&gt;-->
  <!--&lt;!&ndash;          忘记密码&ndash;&gt;-->
  <!--&lt;!&ndash;        </router-link>&ndash;&gt;-->
  <!--        &lt;!&ndash;        <router-link :to="{ name: 'register'}" class="forge-password" style="float: right;margin-right: 10px" >&ndash;&gt;-->
  <!--        &lt;!&ndash;          注册账户&ndash;&gt;-->
  <!--        &lt;!&ndash;        </router-link>&ndash;&gt;-->
  <!--      </a-form-model-item>-->

        <a-form-item style="margin-top:24px">
          <a-button size="large" type="primary" htmlType="submit" class="login-button" :loading="loginBtn"
                    @click.stop.prevent="handleSubmit" :disabled="loginBtn">确定
          </a-button>
        </a-form-item>

      </a-form-model>

      <two-step-captcha v-if="requiredTwoStepCaptcha" :visible="stepCaptchaVisible" @success="stepCaptchaSuccess"
                        @cancel="stepCaptchaCancel"></two-step-captcha>
      <login-select-tenant ref="loginSelect" @success="loginSelectOk"></login-select-tenant>
      <!--    <third-login ref="thirdLogin"></third-login>-->
    </div>
    <div v-show="showAdminContext == false">
      <h1 style="text-align: center"> 加载中请稍等</h1>
    </div>
  </div>


</template>

<script>
import Vue from 'vue'
import { ACCESS_TOKEN, ENCRYPTED_STRING, UI_CACHE_DB_DICT_DATA, USER_INFO, USER_NAME } from '@/store/mutation-types'
import ThirdLogin from './third/ThirdLogin'
import LoginSelectTenant from './LoginSelectTenant'
import TwoStepCaptcha from '@/components/tools/TwoStepCaptcha'
import { getEncryptedString } from '@/utils/encryption/aesEncrypt'
import { timeFix, welcome } from '@/utils/util'

import LoginAccount from './LoginAccount'
import LoginPhone from './LoginPhone'
import { getAction } from '@api/manage'
import { keyLogin } from '@/api/login'

export default {
  components: {
    LoginSelectTenant,
    TwoStepCaptcha,
    ThirdLogin,
    LoginAccount,
    LoginPhone
  },
  data() {
    return {
      securityText: '',
      customActiveKey: 'tab1',
      rememberMe: true,
      loginBtn: false,
      requiredTwoStepCaptcha: false,
      stepCaptchaVisible: false,
      encryptedString: {
        key: '',
        iv: ''
      },
      showAdminContext :true,
      keyLoginLoading: false,
      showManualLogin: false
    }
  },
  created() {
    getAction('/sys/getSystemSecurityText').then((res) => {
      this.securityText = res.result
    })
    Vue.ls.remove(ACCESS_TOKEN)
    this.getRouterData()
    this.rememberMe = true
    // 如果带 manual = 1, 则直接展示手动登录，不再尝试 key 登录
    if (this.$route.query && this.$route.query.manual === '1') {
      this.showManualLogin = false
      this.keyLoginLoading = false
      this.showAdminContext = true;//页面展示管理员 的输入用户名和秘密
    } else {
      // 自动尝试key登录
      this.showAdminContext = false;//页面不展示管理员 的输入用户名和秘密
      this.attemptKeyLogin()
    }
  },
  methods: {
    handleTabClick(key) {
      this.customActiveKey = key
    },
    handleRememberMeChange(e) {
      this.rememberMe = e.target.checked
    },
    /**跳转到登录页面的参数-账号获取*/
    getRouterData() {
      this.$nextTick(() => {
        let temp = this.$route.params.username || this.$route.query.username || ''
        if (temp) {
          this.$refs.alogin.acceptUsername(temp)
        }
      })
    },

    //登录
    handleSubmit() {
      this.loginBtn = true
      if (this.customActiveKey === 'tab1') {
        // 使用账户密码登录
        this.$refs.alogin.handleLogin(this.rememberMe)
      } else {
        //手机号码登录
        this.$refs.plogin.handleLogin(this.rememberMe)
      }
    },
    // 校验失败
    validateFail() {
      this.loginBtn = false
    },
    // 登录后台成功
    requestSuccess(loginResult) {
      this.$refs.loginSelect.show(loginResult)
    },
    //登录后台失败
    requestFailed(err) {
      let description = ((err.response || {}).data || {}).message || err.message || '请求出现错误，请稍后再试'
      this.$notification['error']({
        message: '登录失败',
        description: description,
        duration: 4
      })
      // 验证码功能已移除
      this.loginBtn = false
    },
    loginSelectOk() {
      this.loginSuccess()
    },
    //登录成功
    loginSuccess() {
      this.$router.push({ path: '/dashboard/analysis' }).catch(() => {
        //console.log('登录跳转首页出错,这个错误从哪里来的')
      })

      //登录Home页
      // this.$router.push({ path: '/home' }).catch(() => {
      //   //console.log('登录跳转首页出错,这个错误从哪里来的')
      // })
      this.$notification.success({
        message: '欢迎',
        description: `${timeFix()}，欢迎回来`
      })
    },

    stepCaptchaSuccess() {
      this.loginSuccess()
    },
    stepCaptchaCancel() {
      this.Logout().then(() => {
        this.loginBtn = false
        this.stepCaptchaVisible = false
      })
    },
    //获取密码加密规则
    getEncrypte() {
      var encryptedString = Vue.ls.get(ENCRYPTED_STRING)
      if (encryptedString == null) {
        getEncryptedString().then((data) => {
          this.encryptedString = data
        })
      } else {
        this.encryptedString = encryptedString
      }
    },

    // 尝试key登录
    attemptKeyLogin() {
      this.keyLoginLoading = true
      keyLogin().then((res) => {
        if (res.success) {
          try {
            if (res.result && res.result.token) {
              Vue.ls.set(ACCESS_TOKEN, res.result.token)
            }
            if (res.result && res.result.userInfo) {
              console.log('有用户信息')
              const u = res.result.userInfo
              Vue.ls.set(USER_NAME, u.username)
              Vue.ls.set(USER_INFO, u)
              if (res.result.sysAllDictItems) {
                Vue.ls.set(UI_CACHE_DB_DICT_DATA, res.result.sysAllDictItems)
              }
            }
          } catch (e) {
          }
          // 同步到 vuex, 确保头像和用户名等展示（如果有）
          try {
            if (res.result && res.result.userInfo) {
              this.$store.commit('SET_INFO', res.result.userInfo)
              this.$store.commit('SET_NAME', {
                username: res.result.userInfo.username,
                realname: res.result.userInfo.realname,
                welcome: welcome()
              })
              this.$store.commit('SET_AVATAR', res.result.userInfo.avatar)
              if (res.result.token) {
                this.$store.commit('SET_TOKEN', res.result.token)
              }
            }
          } catch (e) {

          }
          // 如果需要选择部门则弹出选择，否则直接跳转主页
          // key登录成功，直接跳转主页
          console.log('key 登陆成功')
          if (res.result && res.result.multi_depart === 2) {
            this.$refs.loginSelect && this.$refs.loginSelect.show(res.result)
          } else {
            this.loginSuccess()
          }
        } else {
          // key登录失败，显示手动登录选项
          console.log('key 登录失败')
          this.showManualLogin = true
          this.$message.error(res.message || '未能获取用户有效信息')
        }
      }).catch((err) => {
        // key登录异常，显示手动登录选项
        this.showManualLogin = true
        this.$message.error('未能获取用户有效信息')
      }).finally(() => {
        this.keyLoginLoading = false
      })
    },

    // 手动登录点击以后
    showManualLoginForm() {
      // 停止任何加载并展示手动登录表单
      this.keyLoginLoading = false
      this.showManualLogin = false
      // 确保路由是手动登录模式，避免 created 里再次尝试 key 登录
      // if (this.$route.query.manual !== '1') {
      //   this.$router.replace({ path: this.$route.path, query: { ...this.$route.query, manual: '1' } })
      // }
      this.$router.push({ path: '/user/login?manual=1' }).catch(() => {
        //console.log('登录跳转首页出错,错误来源是手动登录点击')
      })
    }
  }
}
</script>
<style lang="less" scoped>
.user-layout-login {
  label {
    font-size: 14px;
  }

  .getCaptcha {
    display: block;
    width: 100%;
    height: 40px;
  }

  .forge-password {
    font-size: 14px;
  }

  button.login-button {
    padding: 0 15px;
    font-size: 16px;
    height: 40px;
    width: 100%;
  }

  .user-login-other {
    text-align: left;
    margin-top: 24px;
    line-height: 22px;

    .item-icon {
      font-size: 24px;
      color: rgba(0, 0, 0, .2);
      margin-left: 16px;
      vertical-align: middle;
      cursor: pointer;
      transition: color .3s;

      &:hover {
        color: #1890ff;
      }
    }

    .register {
      float: right;
    }
  }
}

.key-login-loading {
  text-align: center;
  padding: 50px 0;

  .loading-text {
    margin-top: 16px;
    font-size: 16px;
    color: #666;
  }
}

.key-login-failed {
  padding: 20px 0;
}
</style>
<style>
.valid-error .ant-select-selection__placeholder {
  color: #f5222d;
}
</style>