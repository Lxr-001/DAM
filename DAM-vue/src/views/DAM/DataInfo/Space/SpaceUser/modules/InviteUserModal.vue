<template>
  <a-modal
    title="邀请用户"
    :width="1100"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :maskClosable="false"
    :bodyStyle="{padding: '10px'}"
  >
    <a-row :gutter="10" class="depart-layout-row">
      <!-- 左侧部门树 -->
      <a-col :sm="24" :md="7" class="depart-left-col">
        <a-card :bordered="false" class="depart-side-card">
          <div class="select-user-tree-panel">
            <a-alert type="info" :showIcon="true">
              <div slot="message">
                当前选择：
                <span v-if="currSelected && currSelected.title">{{ currSelected.title }}</span>
                <a v-if="currSelected && currSelected.title" style="margin-left: 10px" @click="onClearSelectedDepart">取消选择</a>
              </div>
            </a-alert>
            <a-input-search
              @search="onDepartSearch"
              style="width:100%;margin-top: 10px"
              placeholder="请输入部门名称"
            />
            <div class="select-user-tree-scroll">
              <a-tree
                showLine
                :selectedKeys="selectedDepartKeys"
                :treeData="departTree"
                :expandedKeys="expandedKeys"
                :autoExpandParent="autoExpandParent"
                @expand="onExpand"
                @select="onDepartSelect"
              />
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 右侧用户列表 -->
      <a-col :sm="24" :md="17" class="depart-right-col">
        <a-card :bordered="false" class="depart-side-card">
          <!-- 查询区域 -->
          <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
              <a-row :gutter="12">
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
                  <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                  <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                </a-col>
              </a-row>
            </a-form>
          </div>

          <!-- 一键勾选按钮 -->
          <div class="select-all-row">
            <a-button
              type="primary"
              icon="check"
              @click="handleToggleSelectAll"
              :disabled="!hasSelectedDepartment()">
              {{ currentSelectAllChecked ? '取消全选' : '一键勾选' }}
            </a-button>
            <span style="margin-left: 12px; color: #888; line-height: 32px;">
              已选择 {{ selectedRowKeys.length }} 位用户
            </span>
          </div>

          <!-- table区域 -->
          <div>
            <a-empty v-if="shouldShowEmpty()">
              <span slot="description"> 请先选择一个部门，或输入用户账号/用户名称后查询 </span>
            </a-empty>
            <a-table
              v-else
              size="small"
              bordered
              rowKey="id"
              :columns="columns"
              :dataSource="dataSource"
              :pagination="ipagination"
              :loading="loading"
              :scroll="{ y: 280 }"
              :rowSelection="{selectedRowKeys: selectedRowKeys, onSelectAll: onSelectAll, onSelect: onSelect, onChange: onSelectChange}"
              @change="handleTableChange"
            >
            </a-table>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
  import { filterObj } from '@/utils/util'
  import { getAction, postAction } from '@/api/manage'
  import { queryDepartTreeList, searchByKeywords } from '@/api/api'

  export default {
    name: 'InviteUserModal',
    data() {
      return {
        title: '邀请用户',
        visible: false,
        placement: 'right',
        description: '',
        // 空间信息
        spaceId: '',
        spaceName: '',
        // 部门树数据
        departTree: [],
        selectedDepartKeys: [],
        expandedKeys: [],
        autoExpandParent: true,
        currSelected: {},
        // 查询条件
        queryParam: {},
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
            width: 140,
            dataIndex: 'username'
          },
          {
            title: '用户姓名',
            align: 'center',
            width: 140,
            dataIndex: 'realname'
          },
          {
            title: '角色',
            align: 'center',
            width: 200,
            dataIndex: 'roleNames'
          }
        ],
        // 数据集
        dataSource: [],
        // 已选择的用户（用于确定）
        selectedRowKeys: [],
        selectedRows: [],
        // 分页参数
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + '-' + range[1] + ' 共' + total + '条'
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter: {
          column: 'createTime',
          order: 'desc'
        },
        loading: false,
        currentSelectAllChecked: false,
        // 已被邀请的用户ID列表（不显示在列表中）
        invitedUserIds: [],
        url: {
          list: '/spaceUser/invitableUserWithRoles/list',
          addBatch: '/spaceUser/addBatch'
        }
      }
    },
    created() {},
    watch: {
      visible(val) {
        if (val === true) {
          // 每次打开弹窗，重置部门树选择
          this.selectedDepartKeys = []
          this.currSelected = {}
          this.onClearSelected()
          this.onClearUserTable()
        }
      }
    },
    computed: {
      modalFooter() {
        return [
          <a-button key="cancel" onClick={this.handleCancel}>取消</a-button>,
          <a-button key="ok" type="primary" onClick={this.handleOk}>确定</a-button>
        ]
      }
    },
    methods: {
      loadData(arg) {
        // 加载已被邀请的用户ID列表
        this.loadInvitedUsers()
        // 加载部门树
        this.loadDepartTree()
      },
      loadInvitedUsers() {
        // 查询当前空间已邀请的用户
        getAction('/spaceUser/listBySpaceId', { spaceId: this.spaceId, pageNo: 1, pageSize: 999 }).then(res => {
          if (res.success) {
            this.invitedUserIds = (res.result.records || []).map(item => item.userId)
          }
        })
      },
      loadDepartTree() {
        this.departTree = []
        this.expandedKeys = []
        this.autoExpandParent = true
        queryDepartTreeList().then(res => {
          if (res.success) {
            const treeList = Array.isArray(res.result) ? res.result : []
            this.departTree = treeList
            this.expandedKeys = this.collectExpandedKeys(treeList)
          }
        })
      },
      collectExpandedKeys(treeList) {
        const expandedKeys = []
        const travel = nodeList => {
          if (!Array.isArray(nodeList)) return
          nodeList.forEach(node => {
            if (node && node.children && node.children.length > 0) {
              expandedKeys.push(node.key)
              travel(node.children)
            }
          })
        }
        travel(treeList)
        return expandedKeys
      },
      onExpand(expandedKeys) {
        this.expandedKeys = expandedKeys
        this.autoExpandParent = false
      },
      onDepartSearch(value) {
        if (value) {
          searchByKeywords({ keyWord: value }).then(res => {
            if (res.success) {
              const treeList = Array.isArray(res.result) ? res.result : []
              this.departTree = treeList
              this.expandedKeys = this.collectExpandedKeys(treeList)
              this.autoExpandParent = true
            } else {
              this.$message.warning(res.message)
            }
          })
        } else {
          this.loadDepartTree()
        }
      },
      onDepartSelect(selectedKeys, e) {
        const record = (e && e.node && e.node.dataRef) ? e.node.dataRef : {}
        if (selectedKeys && selectedKeys.length > 0) {
          const currentOrgUnit = (e && e.node && e.node.eventKey) || selectedKeys[0] || record.id || record.key
          this.selectedDepartKeys = currentOrgUnit ? [currentOrgUnit] : []
          this.currSelected = Object.assign({}, record)
          this.onClearSelected()
          this.loadUserData(1)
        }
      },
      onClearSelectedDepart() {
        this.selectedDepartKeys = []
        this.currSelected = {}
        this.onClearSelected()
        this.onClearUserTable()
      },
      onClearUserTable() {
        this.dataSource = []
        this.ipagination.total = 0
        this.ipagination.current = 1
      },
      hasSelectedDepartment() {
        return Array.isArray(this.selectedDepartKeys) && this.selectedDepartKeys.length > 0
      },
      hasUserSearchKeyword() {
        const username = this.queryParam && this.queryParam.username
        const realname = this.queryParam && this.queryParam.realname
        return !!((username && String(username).trim()) || (realname && String(realname).trim()))
      },
      shouldShowEmpty() {
        return !this.hasSelectedDepartment() && !this.hasUserSearchKeyword()
      },
      loadUserData(arg) {
        // 未选择部门且未输入账号：不请求接口，不展示用户信息
        if (!this.hasSelectedDepartment() && !this.hasUserSearchKeyword()) {
          this.onClearUserTable()
          return
        }
        // 加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1
        }
        var params = this.getQueryParams()
        this.loading = true
        getAction(this.url.list, params).then(res => {
          if (res.success) {
            // 过滤掉已被邀请的用户
            let records = (res.result.records || []).filter(item => !this.invitedUserIds.includes(item.id))
            this.dataSource = records
            this.ipagination.total = res.result.total
          } else {
            this.$message.warning(res.message)
          }
        }).finally(() => {
          this.loading = false
        })
      },
      getQueryParams() {
        var param = Object.assign({}, this.queryParam, this.isorter)
        param.pageNo = this.ipagination.current
        param.pageSize = this.ipagination.pageSize
        // 部门过滤：传递 orgunit 和 departId
        const selectedOrgUnit = this.selectedDepartKeys.join(',')
        if (selectedOrgUnit) {
          param.orgunit = selectedOrgUnit
          param.departId = selectedOrgUnit
        }
        param.maxDataSecurity = 1
        return filterObj(param)
      },
      getQueryField() {
        // 字段权限控制
      },
      onSelectAll(selected, selectedRows, changeRows) {
        if (selected === true) {
          for (var a = 0; a < changeRows.length; a++) {
            const idx = this.selectedRows.findIndex(row => row.id === changeRows[a].id)
            if (idx === -1) {
              this.selectedRows.push(changeRows[a])
            }
          }
        } else {
          for (var b = 0; b < changeRows.length; b++) {
            const idx = this.selectedRows.findIndex(row => row.id === changeRows[b].id)
            if (idx !== -1) {
              this.selectedRows.splice(idx, 1)
            }
          }
        }
      },
      onSelect(record, selected) {
        if (selected === true) {
          const idx = this.selectedRows.findIndex(row => row.id === record.id)
          if (idx === -1) {
            this.selectedRows.push(record)
          }
        } else {
          const idx = this.selectedRows.findIndex(row => row.id === record.id)
          if (idx !== -1) {
            this.selectedRows.splice(idx, 1)
          }
        }
      },
      onSelectChange(selectedRowKeys, selectedRows) {
        this.selectedRowKeys = selectedRowKeys
        this.selectedRows = selectedRows
      },
      onClearSelected() {
        this.selectedRowKeys = []
        this.selectedRows = []
        this.currentSelectAllChecked = false
      },
      handleToggleSelectAll() {
        if (this.currentSelectAllChecked) {
          // 取消当前部门全选
          this.onSelectChange([], [])
          this.currentSelectAllChecked = false
        } else {
          // 勾选当前部门所有可见用户
          const allKeys = this.dataSource.map(item => item.id)
          const allRows = [...this.dataSource]
          this.onSelectChange(allKeys, allRows)
          this.currentSelectAllChecked = true
        }
      },
      searchQuery() {
        this.loadUserData(1)
      },
      searchReset() {
        this.queryParam = {}
        this.loadUserData(1)
      },
      handleTableChange(pagination, filters, sorter) {
        // 分页、排序、筛选变化时触发
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field
          this.isorter.order = 'ascend' == sorter.order ? 'asc' : 'desc'
        }
        this.ipagination = pagination
        this.loadUserData()
      },
      handleOk() {
        if (this.selectedRowKeys.length === 0) {
          this.$message.warning('请选择需要邀请的用户')
          return
        }
        // 构建批量添加的数据
        const spaceUsers = this.selectedRows.map(user => ({
          spaceId: this.spaceId,
          userId: user.id
        }))
        postAction(this.url.addBatch, spaceUsers).then(res => {
          if (res.success) {
            this.$message.success(`成功邀请 ${this.selectedRowKeys.length} 位用户加入数据空间`)
            this.$emit('ok')
            this.handleCancel()
          } else {
            this.$message.warning(`部分用户邀请失败：${res.message}`)
          }
        })
      },
      handleCancel() {
        this.visible = false
        this.onClearSelected()
        this.onClearUserTable()
        this.queryParam = {}
      }
    }
  }
</script>
<style lang="less" scoped>
  .depart-layout-row {
    display: flex;
    align-items: stretch;
    padding: 8px;
    background: #f0f2f5;
    border-radius: 2px;
    min-height: 420px;
  }

  .depart-left-col {
    flex: 0 0 28%;
    max-width: 28%;
  }

  .depart-right-col {
    flex: 0 0 72%;
    max-width: 72%;
  }

  .depart-side-card {
    height: 100%;
    background: #fff;
  }

  .select-user-tree-panel {
    background: #fff;
    padding-left: 12px;
    margin-top: 5px;
  }

  .select-user-tree-scroll {
    margin-top: 10px;
    max-height: 480px;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 8px;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 12px;
    padding-bottom: 12px;
  }

  .select-all-row {
    margin-bottom: 8px;
    display: flex;
    align-items: center;
  }

  .select-all-row .ant-btn {
    height: 32px;
  }
</style>