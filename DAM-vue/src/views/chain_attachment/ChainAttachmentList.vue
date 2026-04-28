<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24"></a-row>
      </a-form>
    </div>
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button icon="plus" type="primary" @click="handleAdd">新增</a-button>

      <!-- 高级查询区域 -->
      <!-- <j-super-query
        :fieldList="superFieldList"
        ref="superQueryModal"
        @handleSuperQuery="handleSuperQuery"
      ></j-super-query>-->
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete" />
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作
          <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
        <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        :columns="columns"
        :dataSource="dataSource"
        :loading="loading"
        :pagination="ipagination"
        :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        :scroll="{ x: true }"
        bordered
        class="j-table-force-nowrap"
        rowKey="id"
        size="middle"
        @change="handleTableChange"
      >
        <template slot="link" slot-scope="text , record">
          <a v-if="hasPermission(record)" @click="download(record.fileKey)">{{ text }}</a>
          <span v-else class="text-muted">{{ text }} (无权限访问)</span>
        </template>
        <template slot="htmlSlot" slot-scope="text">
          <div v-html="text"></div>
        </template>
        <template slot="imgSlot" slot-scope="text,record">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
          <img
            v-else
            :preview="record.id"
            :src="getImgView(text)"
            alt
            height="25px"
            style="max-width:80px;font-size: 12px;font-style: italic;"
          />
        </template>
        <template slot="fileSlot" slot-scope="text">
          <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
          <a-button
            v-else
            :ghost="true"
            icon="download"
            size="small"
            type="primary"
            @click="downloadFile(text)"
          >下载
          </a-button>
        </template>

        <span slot="action" slot-scope="text, record">
          <template v-if="hasPermission(record)">
            <a @click="handleEdit(record)">编辑</a>

            <a-divider type="vertical" />
            <a-dropdown>
              <a class="ant-dropdown-link">
                更多
                <a-icon type="down" />
              </a>
              <a-menu slot="overlay">
                <a-menu-item>
                  <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                    <a>删除</a>
                  </a-popconfirm>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </template>
          <span v-else class="text-muted">无权限操作</span>
        </span>
      </a-table>
    </div>

    <chain-attachment-modal ref="modalForm" :parentSecurity="currentParentSecurity"
                            @ok="modalFormOk"></chain-attachment-modal>
  </a-card>
</template>

<script>
import { filterObj } from '@/utils/util'
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import ChainAttachmentModal from './modules/ChainAttachmentModal'
import { USER_INFO } from '@/store/mutation-types'
import { getAction } from '@api/manage'

export default {
  name: 'ChainAttachmentList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    ChainAttachmentModal
  },
  data() {
    return {
      pid: '',
      userSecurity: 0, // 当前用户密级
      currentParentSecurity: 0, // 当前挂载数据的密级
      description: '产业链附件管理页面',
      disableMixinCreated: true,
      // 表头
      columns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: function(t, r, index) {
            return parseInt(index) + 1
          }
        },
        {
          title: '文件名称',
          align: 'center',
          dataIndex: 'fileName',
          scopedSlots: {
            customRender: 'link'
          }
        },
        {
          title: '密级',
          align: 'center',
          dataIndex: 'security_dictText'
        },
        {
          title: '备注',
          align: 'center',
          dataIndex: 'remark'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 147,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/chainAttachment/list',
        delete: '/chainAttachment/delete',
        deleteBatch: '/chainAttachment/deleteBatch',
        exportXlsUrl: '/chainAttachment/exportXls',
        importExcelUrl: 'chainAttachment/importExcel'

      },
      dictOptions: {},
      superFieldList: []
    }
  },
  created() {
    this.getUserSecurity()
    this.getSuperFieldList()
  },
  watch: {
    // 监听数据源变化，过滤高密级附件
    dataSource: {
      handler(newVal) {
        if (newVal && this.userSecurity > 0) {
          return this.filterBySecurityLevel(newVal)
        }
        return newVal
      },
      deep: true
    }
  },
  computed: {
    importExcelUrl: function() {
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
    }
  },
  methods: {
    // 获取当前用户密级
    getUserSecurity() {
      try {
        // 从本地存储获取用户信息
        const userInfoStr = localStorage.getItem(USER_INFO)
        if (userInfoStr) {
          const userInfo = JSON.parse(userInfoStr)
          this.userSecurity = parseInt(userInfo.security || 0)
        }
      } catch (error) {
        console.error('获取用户密级失败:', error)
      }
    },

    // 根据用户密级过滤附件
    filterBySecurityLevel(data) {
      return data.filter(item => {
        const itemSecurity = parseInt(item.security || 0)
        return itemSecurity <= this.userSecurity
      })
    },

    // 检查用户是否有权限操作该附件
    hasPermission(record) {
      const itemSecurity = parseInt(record.security || 0)
      return itemSecurity <= this.userSecurity
    },

    openAttachment: function(fileName) {
      var url = window._CONFIG['domianURL'] + '/showStaticDir' + '/' + fileName // 要预览文件的访问地址
      //url = this.codeBase64Encode(url)
      console.log('url', window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(url))
      window.open(window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(url))
    },

    download: function(fileKey) {
      // 调用自定义下载接口，实现文件名重命名
      var url = window._CONFIG['domianURL'] + '/chainAttachment/download?fileKey=' + encodeURIComponent(fileKey)
      window.open(url)
    },
    getQueryParams() {
      //获取查询条件
      let sqp = {}
      if (this.superQueryParams) {
        sqp['superQueryParams'] = encodeURI(this.superQueryParams)
        sqp['superQueryMatchType'] = this.superQueryMatchType
      }
      var param = Object.assign(sqp, this.queryParam, this.isorter, this.filters)
      param.field = this.getQueryField()
      param.pageNo = this.ipagination.current
      param.pageSize = this.ipagination.pageSize
      param.pid = this.pid
      return filterObj(param)
    },
    handleAdd() {
      console.log('添加附件，传递parentSecurity:', this.currentParentSecurity)
      // 确保modalForm组件存在
      if (this.$refs.modalForm) {
        // 先设置parentSecurity属性，再调用show方法
        this.$refs.modalForm.parentSecurity = this.currentParentSecurity
        this.$refs.modalForm.show(this.pid)
      }
    },
    handleEdit: function(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
      this.$refs.modalForm.disableSubmit = false
    },
    // 接收挂载数据的ID和密级，用于后续操作
    show(record) {
      console.log('record: ', record)
      this.pid = record.id
      this.currentParentSecurity = record.secretLevel || 0
      console.log('ChainAttachmentList show:', record.id, 'secretLevel:', this.currentParentSecurity)
      // 搜索时可以考虑将密级限制作为查询条件的一部分
      this.searchQuery()
    },

    // 重写查询方法，在前端过滤高密级附件
    searchQuery() {
      const that = this
      that.loading = true
      if (!that.url.list) {
        that.$message.error('请设置url.list属性!')
        return
      }

      var params = that.getQueryParams() // 查询条件
      getAction(that.url.list, params).then((res) => {
        if (res.success) {
          // 根据用户密级过滤数据
          let filteredData = res.result.records
          if (this.userSecurity > 0) {
            filteredData = this.filterBySecurityLevel(filteredData)
          }
          that.dataSource = filteredData
          that.ipagination.total = res.result.total
        }
      }).finally(() => {
        that.loading = false
        that.selectedRowKeys = []
      })
    },

    initDictConfig() {
    },

    getSuperFieldList() {
      let fieldList = []
      fieldList.push({ type: 'string', value: 'fileName', text: '文件名称', dictCode: '' })
      fieldList.push({ type: 'int', value: 'security', text: '密级', dictCode: 'security' })
      fieldList.push({ type: 'string', value: 'remark', text: '备注', dictCode: '' })
      this.superFieldList = fieldList
    }
  }
}
</script>
<style scoped>
@import "~@assets/less/common.less";

.text-muted {
  color: #8c8c8c;
  font-size: 12px;
}
</style>