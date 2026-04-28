<template>
  <div>
    <a-form-model ref="form" :model="model" :rules="validatorRules">
      <a-form-model-item required prop="username">
        <a-input v-model="model.username" size="large" placeholder="请输入帐户名">
          <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }" />
        </a-input>
      </a-form-model-item>
      <a-form-model-item required prop="password">
        <a-input
          v-model="model.password"
          size="large"
          type="password"
          autocomplete="false"
          placeholder="请输入密码"
        >
          <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }" />
        </a-input>
      </a-form-model-item>

      <!-- 验证码功能已移除 -->
    </a-form-model>

    <password-modal ref="passwordmodal" @ok="passwordModalOk" @close="passwordModalCancel"></password-modal>
  </div>
</template>

<script>
import { getAction } from '@/api/manage'
import Vue from 'vue'
import { mapActions } from 'vuex'
import PasswordModal from "@views/system/modules/PasswordModal";
import store from "@/store";
import {ACCESS_TOKEN} from "@/store/mutation-types";

export default {
  name: 'LoginAccount',
  components: {
    PasswordModal,
  },
  data() {
    return {
      result: {},
      requestCodeSuccess: false,
      randCodeImage: '',
      currdatetime: '',
      loginType: 0,
      model: {
        username: '',
        password: ''
      },
      validatorRules: {
        username: [
          { required: true, message: '请输入用户名!' },
          { validator: this.handleUsernameOrEmail }
        ],
        password: [{
          required: true, message: '请输入密码!', validator: 'click'
        }]
      }

    }
  },
  created() {
    // 验证码功能已移除
  },
  methods: {
    ...mapActions(['Login']),
    ...mapActions(["Logout"]),
    // 验证码功能已移除
    // 判断登录类型
    handleUsernameOrEmail(rule, value, callback) {
      const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
      if (regex.test(value)) {
        this.loginType = 0
      } else {
        this.loginType = 1
      }
      callback()
    },
    /**
     * 验证字段
     * @param arr
     * @param callback
     */
    validateFields(arr, callback) {
      let promiseArray = []
      for (let item of arr) {
        let p = new Promise((resolve, reject) => {
          this.$refs['form'].validateField(item, (err) => {
            if (!err) {
              resolve();
            } else {
              reject(err);
            }
          })
        });
        promiseArray.push(p)
      }
      Promise.all(promiseArray).then(() => {
        callback()
      }).catch(err => {
        callback(err)
      })
    },
    acceptUsername(username) {
      this.model['username'] = username
    },
    // 账号密码登录
    handleLogin(rememberMe) {
      this.validateFields(['username', 'password'], (err) => {
        if (!err) {
          let loginParams = {
            username: this.model.username,
            password: this.model.password,
            remember_me: rememberMe,
          }
          const that=this
          this.Login(loginParams).then((res) => {
            this.result=JSON.parse(JSON.stringify(res.result))
            if(res.message==="登录成功"){
              this.$emit('success', res.result)
            }else {
              //重新设置密码
              this.$refs.passwordmodal.show(that.model.username);
              this.$message.warn(res.message)
            }
          }).catch((err) => {
            this.$emit('fail', err)
          });
        } else {
          this.$emit('validateFail')
        }
      })
    },

    passwordModalOk() {
      this.$emit('success', this.result)
    },

    passwordModalCancel() {
      let err={message:"密码已过期，请修改后重新登录！"}
      store.dispatch('Logout').then(() => {
        Vue.ls.remove(ACCESS_TOKEN)
      })
      this.$emit('fail', err)
    },
  }

}
</script>

<style scoped>
</style>
