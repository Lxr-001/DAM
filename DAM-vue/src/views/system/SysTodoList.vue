<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div>
    <div>
      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading || tableLoading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange">

      <template slot="htmlSlot" slot-scope="text">
        <div v-html="text"></div>
      </template>
      <template slot="imgSlot" slot-scope="text,record">
        <span v-if="!text" style="font-size: 12px;font-style: italic;">无图片</span>
        <img v-else :src="getImgView(text)" :preview="record.id" height="25px" alt=""
             style="max-width:80px;font-size: 12px;font-style: italic;" />
      </template>
      <template slot="fileSlot" slot-scope="text">
        <span v-if="!text" style="font-size: 12px;font-style: italic;">无文件</span>
        <a-button
          v-else
          :ghost="true"
          type="primary"
          icon="download"
          size="small"
          @click="downloadFile(text)">
          下载
        </a-button>
      </template>

      <span slot="action" slot-scope="text, record">
<!--          <a @click="handleEdit(record)">编辑</a>-->
        <!--          <a-divider type="vertical" />-->
        <!-- 关键修改：添加loadingBtn[record.id]禁用条件 + 加载动画 -->
          <a :class="{'disabled':record.status !==1 || loadingBtn[record.id]}" @click="approve(record)">
            通过
            <a-icon type="loading" v-if="loadingBtn[record.id]" style="margin-left: 4px; font-size: 12px;" />
          </a>
          <a-divider type="vertical" />
        <!-- 关键修改：添加loadingBtn[record.id]禁用条件 + 加载动画 -->
          <a :class="{'disabled':record.status !==1 || loadingBtn[record.id]}" @click="refuse(record)">
            拒绝
            <a-icon type="loading" v-if="loadingBtn[record.id]" style="margin-left: 4px; font-size: 12px;" />
          </a>

        </span>

      </a-table>
    </div>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import { httpAction } from '@api/manage'
  import { EventBus } from '@/Bus'

  export default {
    name: 'SysTodoList',
    mixins: [JeecgListMixin, mixinDevice],
    components: {},
    data() {
      return {
        description: '待办事项管理页面',
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
            title: '类型',
            align: 'center',
            dataIndex: 'type_dictText'
          },
          {
            title: '内容',
            align: 'center',
            dataIndex: 'content'
          },
          {
            title: '状态',
            align: 'center',
            dataIndex: 'status_dictText'
          },
          {
            title: '申请人',
            align: 'center',
            dataIndex: 'createBy'
          },
          {
            title: '审核人',
            align: 'center',
            dataIndex: 'auditor'
          },
          // {
          //   title: '审核意见',
          //   align: 'center',
          //   dataIndex: 'auditOpinion'
          // },
          {
            title: '审核时间',
            align: 'center',
            dataIndex: 'auditTime'
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
          list: '/system/sysTodo/list',
          // delete: '/system/sysTodo/delete',
          // deleteBatch: '/system/sysTodo/deleteBatch',
          // exportXlsUrl: '/system/sysTodo/exportXls',
          // importExcelUrl: 'system/sysTodo/importExcel',
          edit: '/system/sysTodo/edit'
        },
        dictOptions: {},
        superFieldList: [],
        // 关键新增：按钮加载状态（按行ID存储，支持多按钮独立控制）
        loadingBtn: {},
        // 关键新增：表格全局加载状态
        tableLoading: false
      }
    },
    created() {
      this.getSuperFieldList()
    },
    computed: {
      importExcelUrl: function() {
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`
      }
    },
    methods: {
      initDictConfig() {
      },
      getSuperFieldList() {
        let fieldList = []
        fieldList.push({ type: 'string', value: 'type', text: '类型', dictCode: '' })
        fieldList.push({ type: 'Text', value: 'content', text: '内容', dictCode: '' })
        fieldList.push({ type: 'int', value: 'status', text: '状态', dictCode: 'audit_status' })
        fieldList.push({ type: 'string', value: 'auditor', text: '审核人', dictCode: '' })
        // fieldList.push({ type: 'string', value: 'auditOpinion', text: '审核意见', dictCode: '' })
        fieldList.push({ type: 'datetime', value: 'auditTime', text: '审核时间' })
        this.superFieldList = fieldList
      },
      approve: function(record) {
        this.audit(record, 2)
      },
      refuse: function(record) {
        this.audit(record, 3)
      },
      audit(record, val) {
        // 关键修改1：设置当前行按钮加载状态为true（响应式更新）
        this.$set(this.loadingBtn, record.id, true)
        // 关键修改2：设置表格全局加载状态，增强用户感知
        this.tableLoading = true

        this.model = Object.assign({}, record)
        const that = this
        let httpurl = ''
        let method = ''
        httpurl += this.url.edit
        method = 'put'
        this.model.status = val
        httpAction(httpurl, this.model, method).then((res) => {
          if (res.success) {
            that.$message.success(res.message)
            that.$emit('ok')
          } else {
            that.$message.warning(res.message)
          }
        }).finally(() => {
          // 核心修改：接口完成后等待2秒再重置状态、刷新列表
          setTimeout(() => {
            this.$delete(this.loadingBtn, record.id) // 清除按钮加载状态
            this.tableLoading = false // 清除表格加载状态
            this.loadData() // 刷新列表
            EventBus.$emit('reLoadUser')
          }, 2000) // 2000毫秒 = 2秒
        })
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';

  .disabled {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: none; /* 禁用鼠标事件*/
  }
</style>