<template>
  <div class="page-header-index-wide page-header-wrapper-grid-content-main">
    <a-row :gutter="24" type="flex" justify="center">
      <a-col :md="24" :lg="12">
        <a-card :bordered="false" style="text-align: center">
          <div class="account-center-avatarHolder">
            <div class="avatar">
              <img :src="getAvatar()" />
            </div>
            <div class="username">{{ nickname }}</div>
          </div>
          <div class="account-center-detail">
            <p>
              <span><i class="title"></i>生日：</span>
              <span>{{ formattedBirthday || '未知' }}</span>
            </p>
            <p>
              <i class="group"></i>电子邮件：{{ (userInfo.userInfo && userInfo.userInfo.email) || '未设置' }}
            </p>
            <p>
              <i class="address"></i>手机号：{{ (userInfo.userInfo && userInfo.userInfo.phone) || '未设置' }}
            </p>
            <p>
              <i class="address"></i>座机号：{{ (userInfo.userInfo && userInfo.userInfo.telephone) || '未设置' }}
            </p>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import { getAction } from '@/api/manage'
import { mapGetters } from 'vuex'
import { getFileAccessHttpUrl } from '@/api/manage'
import { USER_INFO } from '@/store/mutation-types'
import moment from 'moment/moment'

export default {
  name: 'Center',
  data() {
    return {
      // 用户基本信息
      userInfo: {
        userInfo: {}
      },
      birthdayMoment: null,
      // API地址
      url: {
        userInfo: '/sys/user/getUserInfo'
      }
    }
  },
  computed: {
    ...mapGetters({
      nickname: 'nickname',
      avatar: 'avatar'
    }),
    formattedBirthday() {
      if (this.birthdayMoment) {
        return this.birthdayMoment.format('YYYY-MM-DD')
      }
      return null
    }
  },
  methods: {
    getAvatar() {
      return getFileAccessHttpUrl(this.avatar)
    },
    // 加载用户信息
    getUserInfo() {
      // 从本地存储中获取用户信息
      let userInfo = JSON.parse(localStorage.getItem(USER_INFO))
      if (userInfo) {
        this.userInfo = userInfo
        // 设置生日的moment对象
        if (userInfo.userInfo && userInfo.userInfo.birthday) {
          this.birthdayMoment = moment(userInfo.userInfo.birthday)
        }
        console.log('localStorage userInfo: ', this.userInfo)
      } else {
        // 如果本地没有，则从服务器获取
        getAction('/sys/user/getUserInfo').then(res => {
          if (res.success) {
            this.userInfo = res.result
            // 设置生日的moment对象
            if (res.result.userInfo && res.result.userInfo.birthday) {
              this.birthdayMoment = moment(res.result.userInfo.birthday)
            }
            console.log('API userInfo: ', this.userInfo)
          }
        })
      }
    }
  },
  created() {
    this.getUserInfo()
  }
}
</script>

<style lang="less" scoped>
.page-header-wrapper-grid-content-main {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;

  .account-center-avatarHolder {
    text-align: center;
    margin-bottom: 24px;

    & > .avatar {
      margin: 0 auto;
      width: 104px;
      height: 104px;
      margin-bottom: 20px;
      border-radius: 50%;
      overflow: hidden;
      img {
        height: 100%;
        width: 100%;
      }
    }

    .username {
      color: rgba(0, 0, 0, 0.85);
      font-size: 20px;
      line-height: 28px;
      font-weight: 500;
      margin-bottom: 4px;
    }
  }

  .account-center-detail {
    text-align: center;
    p {
      margin-bottom: 8px;
      padding-left: 0;
      position: relative;
      display: flex;
      justify-content: center;
      align-items: center;
      white-space: nowrap;  // 添加这行确保不换行

      span {
        display: inline-block;
      }
    }

    i {
      position: static;
      margin-right: 8px;
    }
  }
}
</style>