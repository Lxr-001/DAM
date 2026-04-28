<template>
  <div class="account-settings-info-view">
    <a-row :gutter="16" type="flex" justify="center">
      <a-col :md="24" :lg="16" :xl="14">
        <a-form :form="form" layout="vertical" @submit="handleSubmit" class="settings-form">
          <a-form-item label="用户昵称">
            <a-input
              v-decorator="[
                'realname',
                {
                  initialValue: userInfo.userInfo && userInfo.userInfo.realname,
                  rules: [{ required: true, message: '请输入用户昵称' }]
                }
              ]"
              placeholder="请输入用户昵称"
            />
          </a-form-item>

          <a-form-item label="性别">
            <a-select
              v-decorator="[
                'sex',
                {
                  initialValue: userInfo.userInfo && userInfo.userInfo.sex,
                  rules: [{ required: true, message: '请选择性别' }]
                }
              ]"
              placeholder="请选择性别"
            >
              <a-select-option :value="1">男</a-select-option>
              <a-select-option :value="2">女</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="生日">
            <a-date-picker
              v-decorator="[
                'birthday',
                {
                  initialValue: birthdayMoment,
                  rules: [{ required: false, message: '请选择生日' }]
                }
              ]"
              style="width: 100%"
              placeholder="请选择生日"
            />
          </a-form-item>

          <a-form-item label="电子邮件">
            <a-input
              v-decorator="[
                'email',
                {
                  initialValue: userInfo.userInfo && userInfo.userInfo.email,
                  rules: [
                    { required: false, message: '请输入电子邮件' },
                    { type: 'email', message: '请输入正确的电子邮件格式' }
                  ]
                }
              ]"
              placeholder="请输入电子邮件"
            />
          </a-form-item>

          <a-form-item label="手机号码">
            <a-input
              v-decorator="[
                'phone',
                {
                  initialValue: userInfo.userInfo && userInfo.userInfo.phone,
                  rules: [
                    { required: false, message: '请输入手机号码' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' }
                  ]
                }
              ]"
              placeholder="请输入手机号码"
            />
          </a-form-item>

          <a-form-item label="座机号码">
            <a-input
              v-decorator="[
                'telephone',
                {
                  initialValue: userInfo.userInfo && userInfo.userInfo.telephone,
                  rules: [{ required: false, message: '请输入座机号码' }]
                }
              ]"
              placeholder="请输入座机号码"
            />
          </a-form-item>

          <a-form-item>
            <a-button type="primary" html-type="submit" :loading="submitting">保存修改</a-button>
            <a-button style="margin-left: 8px" @click="handleReset">重置</a-button>
          </a-form-item>
        </a-form>
      </a-col>

      <a-col :md="24" :lg="8" :xl="6" class="avatar-col">
        <div class="ant-upload-preview">
          <a-icon type="cloud-upload-o" class="upload-icon" @click="$refs.modal.edit(1)"/>
          <div class="mask" @click="$refs.modal.edit(1)">
            <a-icon type="plus"/>
          </div>
          <img :src="userInfo.userInfo && userInfo.userInfo.avatar || '/avatar2.jpg'"/>
        </div>
        <div style="text-align: center; margin-top: 8px;">
          <a-button type="link" @click="$refs.modal.edit(1)">更换头像</a-button>
        </div>
      </a-col>
    </a-row>

    <avatar-modal ref="modal" @success="handleAvatarSuccess">
    </avatar-modal>
  </div>
</template>

<script>
import {getAction, putAction} from '@/api/manage'
import AvatarModal from './AvatarModal'
import moment from 'moment'
import {USER_INFO} from '@/store/mutation-types'

export default {
  components: {
    AvatarModal
  },
  data() {
    return {
      form: this.$form.createForm(this),
      userInfo: {},
      submitting: false,
      birthdayMoment: null
    }
  },
  created() {
    this.getUserInfo()
  },
  watch: {
    // 监听userInfo变化，更新birthdayMoment
    userInfo: {
      handler(val) {
        if (val && val.userInfo && val.userInfo.birthday) {
          this.birthdayMoment = moment(val.userInfo.birthday)
        } else {
          this.birthdayMoment = undefined
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    // 获取当前登录用户信息
    getUserInfo() {
      // 从本地存储中获取用户信息
      let userInfo = JSON.parse(localStorage.getItem(USER_INFO))
      if (userInfo) {
        this.userInfo = userInfo
        // 设置生日的moment对象
        if (userInfo.userInfo && userInfo.userInfo.birthday) {
          this.birthdayMoment = moment(userInfo.userInfo.birthday)
        }
        console.log("localStorage userInfo: ", this.userInfo)
      } else {
        // 如果本地没有，则从服务器获取
        getAction('/sys/user/getUserInfo').then(res => {
          if (res.success) {
            this.userInfo = res.result
            // 设置生日的moment对象
            if (res.result.userInfo && res.result.userInfo.birthday) {
              this.birthdayMoment = moment(res.result.userInfo.birthday)
            }
            console.log("API userInfo: ", this.userInfo)
          }
        })
      }
    },

    // 提交表单
    handleSubmit(e) {
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          this.submitting = true

          // 处理生日格式
          if (values.birthday) {
            values.birthday = values.birthday.format('YYYY-MM-DD')
          }

          // 构建更新对象，必须包含id
          const updateParams = {
            id: this.userInfo.userInfo && this.userInfo.userInfo.id,
            ...values
          }

          // 调用更新API
          putAction('/sys/user/editBaseSetting', updateParams).then(res => {
            this.submitting = false
            if (res.success) {
              this.$message.success('基本信息更新成功')

              // 更新本地缓存
              let userInfo = JSON.parse(localStorage.getItem(USER_INFO))
              if (userInfo && userInfo.userInfo) {
                Object.assign(userInfo.userInfo, values) // 更新嵌套对象
                localStorage.setItem(USER_INFO, JSON.stringify(userInfo))
                this.userInfo = userInfo
              }
            } else {
              this.$message.error(res.message || '保存失败')
            }
          }).catch(err => {
            this.submitting = false
            this.$message.error('保存失败：' + (err.message || '未知错误'))
          })
        }
      })
    },

    // 重置表单
    handleReset() {
      this.form.resetFields()
    },

    // 处理头像上传成功
    handleAvatarSuccess(url) {
      // 更新头像
      const updateParams = {
        id: this.userInfo.userInfo && this.userInfo.userInfo.id,
        avatar: url
      }

      putAction('/sys/user/edit', updateParams).then(res => {
        if (res.success) {
          this.$message.success('头像更新成功')

          // 更新本地缓存
          let userInfo = JSON.parse(localStorage.getItem(USER_INFO))
          if (userInfo && userInfo.userInfo) {
            userInfo.userInfo.avatar = url // 更新嵌套对象中的头像
            localStorage.setItem(USER_INFO, JSON.stringify(userInfo))
            this.userInfo = userInfo
          }
        } else {
          this.$message.error(res.message || '头像更新失败')
        }
      }).catch(err => {
        this.$message.error('头像更新失败：' + (err.message || '未知错误'))
      })
    }
  }
}
</script>

<style lang="less" scoped>
.account-settings-info-view {
  min-height: 100vh;
  padding: 40px 24px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  background-color: #f0f2f5;

  .settings-form {
    background: #fff;
    padding: 32px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    ::v-deep .ant-form-item {
      margin-bottom: 24px;

      .ant-form-item-label {
        padding-bottom: 8px;
        label {
          font-weight: 500;
          color: rgba(0, 0, 0, 0.85);
        }
      }
    }

    ::v-deep .ant-form-item:last-child {
      margin-bottom: 0;
      padding-top: 8px;
    }
  }

  .avatar-col {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-left: 24px;

    @media (max-width: 992px) {
      padding-left: 0;
      margin-top: 32px;
    }
  }
}

.avatar-upload-wrapper {
  height: 200px;
  width: 100%;
}

.ant-upload-preview {
  position: relative;
  margin: 0 auto;
  width: 100%;
  max-width: 180px;
  border-radius: 50%;
  box-shadow: 0 0 4px #ccc;

  .upload-icon {
    position: absolute;
    top: 0;
    right: 10px;
    font-size: 1.4rem;
    padding: 0.5rem;
    background: rgba(222, 221, 221, 0.7);
    border-radius: 50%;
    border: 1px solid rgba(0, 0, 0, 0.2);
    cursor: pointer;
  }

  .mask {
    opacity: 0;
    position: absolute;
    background: rgba(0, 0, 0, 0.4);
    cursor: pointer;
    transition: opacity 0.4s;

    &:hover {
      opacity: 1;
    }

    i {
      font-size: 2rem;
      position: absolute;
      top: 50%;
      left: 50%;
      margin-left: -1rem;
      margin-top: -1rem;
      color: #d6d6d6;
    }
  }

  img, .mask {
    width: 100%;
    max-width: 180px;
    height: 100%;
    border-radius: 50%;
    overflow: hidden;
  }
}
</style>