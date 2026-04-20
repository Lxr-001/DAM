<template>
  <a-card :bordered="false">
    <!-- 返回按钮 -->
    <div class="space-user-back">
      <a @click="handleBack">
        <a-icon type="left" />
        返回
      </a>
    </div>

    <!-- 空间信息展示 -->
    <div class="space-user-info">
      <a-tag color="blue">{{ spaceTypeText }}</a-tag>
      <span class="space-name">{{ spaceName }}</span>
    </div>

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="用户账号">
              <a-input placeholder="请输入用户账号" v-model="queryParam.username"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="用户姓名">
              <a-input placeholder="请输入用户姓名" v-model="queryParam.realname"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <span style="float: left; overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="handleInviteUser">邀请用户</a-button>
      <a-button
        icon="delete"
        :disabled="selectedRowKeys.length === 0"
        @click="handleRemoveUsers">移除用户</a-button>
      <a-button
        type="primary"
        icon="check"
        :disabled="dataSource.length === 0"
        @click="handleToggleSelectAll">{{ selectAllChecked ? '取消全选' : '一键勾选' }}</a-button>
    </div>

    <!-- table区域 -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        rowKey="id"
        class="j-table-force-nowrap"
        :scroll="{x:true}"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange"
        v-bind="tableProps">
      </a-table>
    </div>

    <!-- 邀请用户弹窗 -->
    <invite-user-modal ref="inviteModal" @ok="modalFormOk"></invite-user-modal>
  </a-card>
</template>

<script>
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import InviteUserModal from './modules/InviteUserModal'
  import { getAction, deleteAction } from '@/api/manage'
  import { filterObj } from '@/utils/util'

  export default {
    name: 'SpaceUserList',
    mixins: [JeecgListMixin],
    components: {
      InviteUserModal
    },
    data() {
      return {
        description: '数据空间用户管理页面',
        // 空间信息
        spaceId: '',
        spaceName: '',
        spaceType: '',
        selectAllChecked: false,
        // 选择的行数据
        selectionRows: [],
        // 表头
        columns: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 60,
            align: 'center',
            customRender: function(t, r, index) {
              return parseInt(index) + 1
            }
          },
          {
            title: '用户账号',
            align: 'center',
            dataIndex: 'username'
          },
          {
            title: '用户姓名',
            align: 'center',
            dataIndex: 'realname'
          },
          {
            title: '部门',
            align: 'center',
            dataIndex: 'departName'
          },
          {
            title: '角色',
            align: 'center',
            dataIndex: 'roleNames',
            scopedSlots: { customRender: 'roleNames' }
          }
        ],
        url: {
          list: '/spaceUser/listBySpaceId',
          delete: '/spaceUser/delete',
          deleteBatch: '/spaceUser/deleteBatch',
          removeUser: '/spaceUser/removeUser'
        },
        dictOptions: {},
        superFieldList: []
      }
    },
    computed: {
      spaceTypeText() {
        const map = {
          'public': '公共',
          'private': '私有',
          'project': '项目',
          'department': '部门'
        }
        return map[this.spaceType] || this.spaceType || ''
      },
      tableProps() {
        return {
          rowSelection: {
            selectedRowKeys: this.selectedRowKeys,
            onChange: (selectedRowKeys) => this.selectedRowKeys = selectedRowKeys
          }
        }
      }
    },
    created() {
      this.disableMixinCreated = true
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        vm.spaceId = to.query.spaceId || ''
        vm.spaceName = to.query.spaceName || ''
        vm.spaceType = to.query.spaceType || ''
        vm.departId = to.query.departId || ''
        vm.departName = to.query.departName || ''
        vm.queryParam = {
          spaceId: vm.spaceId
        }
        if (vm.spaceId) {
          vm.loadData()
        }
      })
    },
    methods: {
      initSpaceInfo() {
        this.spaceId = this.$route.query.spaceId || ''
        this.spaceName = this.$route.query.spaceName || ''
        this.spaceType = this.$route.query.spaceType || ''
        this.departId = this.$route.query.departId || ''
        this.departName = this.$route.query.departName || ''
        this.queryParam = {
          spaceId: this.spaceId
        }
        if (this.spaceId) {
          this.loadData()
        }
      },
      handleBack() {
        // 返回并刷新空间列表
        sessionStorage.setItem('needRefreshSpaceList', 'true')
        this.$router.go(-1)
      },
      handleInviteUser() {
        this.$refs.inviteModal.visible = true
        this.$refs.inviteModal.spaceId = this.spaceId
        this.$refs.inviteModal.spaceName = this.spaceName
        this.$refs.inviteModal.loadData()
      },
      handleRemoveUsers() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请先勾选需要移除的用户')
          return
        }
        const that = this
        this.$confirm({
          title: '确认移除用户',
          content: `是否将勾选的 ${this.selectedRowKeys.length} 位用户移出「${this.spaceName}」数据空间？移除后用户将失去该数据空间的访问权限，请谨慎操作！`,
          centered: true,
          onOk() {
            const promises = []
            that.selectionRows.forEach(item => {
              promises.push(deleteAction(that.url.removeUser, { spaceId: that.spaceId, userId: item.userId }))
            })
            Promise.all(promises).then(res => {
              const successCount = res.filter(r => r.success).length
              const failCount = res.length - successCount
              if (failCount === 0) {
                that.$message.success(`已成功移除 ${successCount} 位用户`)
              } else {
                that.$message.warning(`部分用户移除失败：成功 ${successCount} 位，失败 ${failCount} 位`)
              }
              that.loadData()
              that.onClearSelected()
            }).finally(() => {
              that.loading = false
            })
          },
          onCancel() {}
        })
      },
      handleToggleSelectAll() {
        if (this.selectAllChecked) {
          this.onClearSelected()
          this.selectAllChecked = false
        } else {
          this.selectedRowKeys = this.dataSource.map(item => item.id)
          this.selectionRows = [...this.dataSource]
          this.selectAllChecked = true
        }
      },
      loadData(arg) {
        if (arg === 1) {
          this.ipagination.current = 1
        }
        if (!this.spaceId) {
          return
        }
        this.loading = true
        let params = {
          spaceId: this.spaceId,
          pageNo: this.ipagination.current,
          pageSize: this.ipagination.pageSize
        }
        getAction(this.url.list, params).then(res => {
          if (res.success) {
            this.dataSource = res.result.records || res.result || []
            if (res.result && res.result.total) {
              this.ipagination.total = res.result.total
            } else {
              this.ipagination.total = 0
            }
          } else {
            this.$message.warning(res.message)
          }
        }).finally(() => {
          this.loading = false
        })
      },
      searchQuery() {
        this.loadData(1)
      },
      searchReset() {
        this.queryParam = {
          spaceId: this.spaceId
        }
        this.loadData(1)
      },
      onSelectChange(selectedRowKeys, selectionRows) {
        this.selectedRowKeys = selectedRowKeys
        this.selectionRows = selectionRows
      },
      onClearSelected() {
        this.selectedRowKeys = []
        this.selectionRows = []
        this.selectAllChecked = false
      },
      modalFormOk() {
        this.loadData()
        this.onClearSelected()
      }
    }
  }
</script>
<style scoped>
  .space-user-back {
    margin-bottom: 16px;
  }
  .space-user-back a {
    color: #1890ff;
    cursor: pointer;
  }
  .space-user-info {
    margin-bottom: 16px;
  }
  .space-name {
    font-size: 16px;
    font-weight: 600;
    margin-left: 8px;
  }
  .table-operator {
    margin-bottom: 16px;
  }
  .table-operator .ant-btn {
    margin-right: 8px;
  }
  @import '~@assets/less/common.less';
</style>