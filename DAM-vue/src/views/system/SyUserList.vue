<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <!--    <div class="table-page-search-wrapper">-->
    <!--      <a-form layout="inline" @keyup.enter.native="searchQuery">-->
    <!--        <a-row :gutter="24">-->

    <!--          <a-col :md="6" :sm="12">-->
    <!--            <a-form-item label="账号">-->
    <!--              &lt;!&ndash;<a-input placeholder="请输入账号查询" v-model="queryParam.username"></a-input>&ndash;&gt;-->
    <!--              <j-input placeholder="输入账号模糊查询" v-model="queryParam.username"></j-input>-->
    <!--            </a-form-item>-->
    <!--          </a-col>-->
    <!--          <a-col :md="6" :sm="8">-->
    <!--            <a-form-item label="真实名字">-->
    <!--              <a-input placeholder="请输入真实名字" v-model="queryParam.realname"></a-input>-->
    <!--            </a-form-item>-->
    <!--          </a-col>-->
    <!--          <a-col :md="6" :sm="8">-->
    <!--            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">-->
    <!--              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>-->
    <!--              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>-->
    <!--            </span>-->
    <!--          </a-col>-->
    <!--        </a-row>-->
    <!--      </a-form>-->
    <!--    </div>-->

    <!-- table区域-begin -->
    <div>
      <a-table
        ref="table"
        bordered
        size="middle"
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="avatarslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.avatar)" icon="user" />
          </div>
        </template>
        <!-- 修改操作列模板，根据当前用户判断显示文本 -->
        <span slot="action" slot-scope="text, record">
          <a href="javascript:" @click="handlePasswordAction(record)">
            {{ record.username === loginUserName ? '修改密码' : '重置密码' }}
          </a>
        </span>
      </a-table>
    </div>
    <user-modal ref="modalForm" @ok="modalFormOk"></user-modal>

    <password-modal ref="passwordmodal" @ok="passwordModalOk"></password-modal>

  </a-card>
</template>

<script>
  import Vue from 'vue'
  import UserModal from './modules/UserModal'
  import { USER_NAME } from '@/store/mutation-types'
  import PasswordModal from './modules/PasswordModal'
  import { getAction, postAction, getFileAccessHttpUrl } from '@/api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import JInput from '@/components/jeecg/JInput'
  import { message } from 'ant-design-vue'

  export default {
    name: 'SyUserList',
    mixins: [JeecgListMixin],
    components: {
      UserModal,
      PasswordModal,
      JInput
    },
    data() {
      // 获取当前登录用户账号
      const username = Vue.ls.get(USER_NAME);
      return {
        description: '这是三员管理页面',
        queryParam: {},
        // 存储当前登录用户账号
        loginUserName: username || '',
        recycleBinVisible: false,
        columns: [
          {
            title: '用户账号',
            align: 'center',
            dataIndex: 'username',
            width: 120,
            sorter: true
          },
          {
            title: '用户姓名',
            align: 'center',
            width: 100,
            dataIndex: 'realname'
          },
          {
            title: '操作',
            dataIndex: 'action',
            scopedSlots: { customRender: 'action' },
            align: 'center',
            width: 170
          }

        ],
        url: {
          list: '/sys/user/listsy',
          // 新增重置密码接口地址（根据实际后台接口修改）
          resetPassword: '/sys/user/resetPassword'
        }
      }
    },
    computed: {
    },
    created() {
    },
    methods: {
      getAvatarView: function(avatar) {
        return getFileAccessHttpUrl(avatar)
      },

      // 统一处理密码相关操作
      handlePasswordAction(record) {
        const currentUsername = record.username
        // 如果是当前登录用户，打开密码修改弹窗
        if (currentUsername === this.loginUserName) {
          this.$refs.passwordmodal.show(currentUsername)
        } else {
          // 其他用户，弹出确认重置密码提示
          this.$confirm({
            title: '确认提示',
            content: '是否确认重置密码为：123456？',
            onOk: async () => {
              // 点击确认后调用重置密码接口，传入当前行完整对象
              await this.resetUserPassword(record.username)
            },
            onCancel() {
              // 取消操作，不做处理
              message.info('已取消重置密码')
            }
          })
        }
      },

      // 调用后台重置密码接口（接收当前行完整对象作为参数）
      async resetUserPassword(username) {
      const formData = new FormData();
        formData.append('username',username);
        try {
          // 显示加载状态
          this.loading = true
          // 调用后台重置密码接口，将完整用户对象作为参数传递
          // postAction第二个参数为请求体参数，会把整个userRecord对象传给后台
          const res = await postAction(this.url.resetPassword, formData)
          if (res.success) {
            // 重置成功提示
            message.success('重置成功！')
          } else {
            // 重置失败提示
            message.error(res.message || '重置密码失败，请重试')
          }
        } catch (error) {
          // console.error('重置密码失败：', error)
          // console.error('当前操作的用户对象：', error.userRecord) // 可选：打印错误时的用户对象便于调试
          message.error('重置密码失败，请联系管理员')
        } finally {
          // 关闭加载状态
          this.loading = false
        }
      },

      handleChangePassword(username) {
        this.$refs.passwordmodal.show(username)
      },

      passwordModalOk() {
        //TODO 密码修改完成 不需要刷新页面，可以把datasource中的数据更新一下
      },

    }

  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>