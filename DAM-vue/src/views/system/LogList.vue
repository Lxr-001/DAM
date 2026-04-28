<template>
  <a-card :bordered="false">
    <a-card title="自动备份功能" v-if="showConfig">
      <a-checkbox style="font-size: 16px;margin-left: 0" :checked="enableAutoBackup"
                  @change="(e)=> this.enableAutoBackup = e.target.checked">
        自动备份日志
      </a-checkbox>
      <a-checkbox style="font-size: 16px;margin-left: 32px" :checked="enableAutoDelete" :disabled="!enableAutoBackup"
                  @change="(e)=> this.enableAutoDelete = e.target.checked">
        自动备份时删除超过6个月的日志
      </a-checkbox>

      <span style="font-size: 16px;margin-left: 32px">备份间隔：</span>
      <a-input-number style="width:64px" :min="1" :max="31" v-model="backupInterval" :disabled="!enableAutoBackup"
                      @change="(e)=> this.backupInterval = Math.floor(e)"/>
      <span style="font-size: 16px">天</span>

      <a-icon style="font-size: 24px;margin-left: 32px" type="folder-open"/>
      <span style="font-size: 16px;margin-left: 2px">保存路径：</span>
      <a-input v-model="backupPath" style="width: 320px;" :disabled="!enableAutoBackup"></a-input>

      <a-button style="font-size: 16px;margin-left: 32px" type="primary" @click="handleSaveLogParam">保存设置</a-button>
    </a-card>

    <!--导航区域-->
    <div>
      <a-tabs size="large" v-model="tabKey" @change="onLogTypeChange">
        <a-tab-pane v-for="tab in logTypeTabs" :key="tab.key" :tab="tab.title"></a-tab-pane>
      </a-tabs>
    </div>

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="4">
            <a-form-item label="数据密级" style="left: 10px">
              <j-dict-select-tag v-model="queryParam.security" dictCode="system_security" placeholder="请选择密级"/>
            </a-form-item>
          </a-col>

          <a-col v-if="tabKey === '1'" :md="4">
            <a-form-item label="操作类型" style="left: 10px">
              <j-dict-select-tag v-model="queryParam.operateType" dictCode="operate_type" placeholder="请选择操作类型"/>
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item label="操作人ID">
              <a-input v-model="queryParam.userid" placeholder="请输入搜索关键词"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item label="搜索操作人">
              <a-input v-model="queryParam.username" placeholder="请输入搜索关键词"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4">
            <a-form-item label="IP">
              <a-input v-model="queryParam.ip" placeholder="请输入搜索关键词"></a-input>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="6">
            <a-form-item label="操作对象">
              <a-input v-model="queryParam.objectName" placeholder="请输入搜索关键词"></a-input>
            </a-form-item>
          </a-col>


          <a-col :md="6">
            <a-form-item label="搜索日志内容">
              <a-input v-model="queryParam.logContent" placeholder="请输入搜索关键词"></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="创建时间">
              <a-range-picker
                v-model="queryParam.createTimeRange"
                :placeholder="['开始时间', '结束时间']"
                format="YYYY-MM-DD"
                style="width: 210px"
                @change="onDateChange"
                @ok="onDateOk"
              />
            </a-form-item>
          </a-col>

          <span class="table-page-search-submitButtons" style="float: left;overflow: hidden;">
            <a-col :md="6" :sm="24">
                <a-button icon="search" type="primary" @click="searchQuery">查询</a-button>
                <a-button icon="reload" style="margin-left: 8px" type="primary"
                          @click="searchReset">重置</a-button>
                <a-button icon="download" style="margin-left: 8px" type="primary"
                          @click="handleExportXls()">导出</a-button>
            </a-col>
          </span>

        </a-row>
      </a-form>
    </div>

    <!-- table区域-begin -->
    <a-table
      ref="table"
      :columns="columns"
      :dataSource="dataSource"
      :loading="loading"
      :pagination="ipagination"
      rowKey="id"
      size="middle"
      @change="handleTableChange">

      <template v-if="queryParam.logType==='2'" #expandedRowRender="record">
        <div style="margin: 0">
          <div style="margin-bottom: 5px">
            <a-badge status="success" style="vertical-align: middle;"/>
            <span style="vertical-align: middle;">请求方法:{{ record.method }}</span></div>
          <div>
            <a-badge status="processing" style="vertical-align: middle;"/>
            <span style="vertical-align: middle;">请求参数:{{ record.requestParam }}</span></div>
        </div>
      </template>

      <!-- 字符串超长截取省略号显示-->
      <span slot="logContent" slot-scope="text, record">
          <j-ellipsis :length="40" :value="text"/>
        </span>
    </a-table>
    <!-- table区域-end -->
  </a-card>
</template>

<script>
import {filterObj} from '@/utils/util'
import {JeecgListMixin} from '@/mixins/JeecgListMixin'
import JEllipsis from '@/components/jeecg/JEllipsis'
import {downFile, getAction, postAction} from "@api/manage";

export default {
  name: 'LogList',
  mixins: [JeecgListMixin],
  components: {
    JEllipsis
  },
  data() {
    return {
      description: '这是日志管理页面',

      //自动备份配置项
      enableAutoBackup: true,
      enableAutoDelete: false,
      backupInterval: 10,
      backupPath: "/data/cape/frakasK0/log",

      tabKey: '0',
      queryParam: {
        logType: '1',
        // operateType: '',
        logContent: '',
      },

      logTypeTabs: [],
      logTypeTabsDefault: [
        {
          key: "0",
          title: "admin日志（仅调试用）"
        },
        {
          key: "1",
          title: "系统业务功能日志"
        },
        {
          key: "2",
          title: "系统管理日志"
        },
        {
          key: "3",
          title: "系统运行告警日志"
        },
        {
          key: "4",
          title: "授权管理日志"
        },
        {
          key: "5",
          title: "安全审计日志"
        },
        {
          key: "6",
          title: "数据操作日志"
        },
      ],

      columns: [],
      columnsDefault: [
        {
          title: '序号',
          dataIndex: '',
          key: 'rowIndex',
          align: 'center',
          customRender: function (t, r, index) {
            return parseInt(index) + 1
          }
        },
        {
          title: '数据密级',
          dataIndex: 'security_dictText',
          align: 'center',
        },
        {
          title: '操作人ID',
          dataIndex: 'userid',
          align: 'center',
        },
        {
          title: '操作人名称',
          dataIndex: 'username',
          align: 'center',
        },
        {
          title: '操作类型',
          dataIndex: 'operateType_dictText',
          align: 'center',
        },
        {
          title: '操作对象',
          dataIndex: 'objectName',
          align: 'center',
          scopedSlots: {customRender: 'logContent'},
        },
        {
          title: '操作结果',
          dataIndex: 'result',
          align: 'center',
        },
        {
          title: '日志内容',
          align: 'center',
          dataIndex: 'logContent',
          scopedSlots: {customRender: 'logContent'},
        },
        {
          title: 'IP',
          dataIndex: 'ip',
          align: 'center',
        },
        {
          title: '操作时间',
          dataIndex: 'createTime',
          align: 'center',
        },
      ],

      labelCol: {
        xs: {span: 1},
        sm: {span: 2}
      },
      wrapperCol: {
        xs: {span: 10},
        sm: {span: 16}
      },
      url: {
        list: '/sys/log/list',
        getLogTypeByUserRole: '/sys/log/getLogTypeByUserRole',
        exportXlsUrl: '/sys/log/exportXls',
        queryLogParam: '/sys/log/queryLogParam',
        saveLogParam: '/sys/log/saveLogParam',
      }
    }
  },
  created() {
    this.loading = true;
    getAction(this.url.getLogTypeByUserRole).then((res) => {
      if (res.success) {
        for (let i = 0; i < res.result.length; i++) {
          this.logTypeTabs.push(this.logTypeTabsDefault[res.result[i]])
        }
        const tabKey = this.logTypeTabs[0].key
        this.tabKey = tabKey
        this.onLogTypeChange(tabKey)
        this.loadData(1)
      } else {
        this.$message.warning(res.message)
      }
    }).finally(() => {
      this.loading = false
    })
    getAction(this.url.queryLogParam).then((res) => {
      if (res.success) {
        let vo = res.result
        this.enableAutoBackup = vo.autoBackup
        this.enableAutoDelete = vo.autoDelete
        this.backupInterval = vo.interval
        this.backupPath = vo.path
      }
    })
  },
  computed: {
    showConfig() {
      let show = false
      for (let i = 0; i < this.logTypeTabs.length; i++) {
        if (this.logTypeTabs[i].key === "3") {    //只有系统管理员能看到自动备份设置
          show = true
        }
      }
      return show
    }
  },
  methods: {
    handleSaveLogParam() {
      let vo = {
        autoBackup: this.enableAutoBackup,
        autoDelete: this.enableAutoDelete,
        interval: this.backupInterval,
        path: this.backupPath
      }
      postAction(this.url.saveLogParam, vo).then((res) => {
        if (res.success) {
          this.$message.success(res.message)
        } else {
          this.$message.warning(res.message)
        }
      })
    },
    onLogTypeChange(tabKey) {
      if (tabKey === "1") {
        this.columns = this.columnsDefault
      } else {
        this.columns = this.columnsDefault.filter(e => e.title !== "操作类型")
        delete this.queryParam.operateType
      }
      this.queryParam.logType = parseInt(tabKey)
      this.loadData(1)
    },
    getQueryParams() {
      var param = Object.assign({}, this.queryParam, this.isorter)
      const username = this.queryParam.username
      const logContent = this.queryParam.logContent
      const objectName = this.queryParam.objectName
      const userid = this.queryParam.userid
      const ip = this.queryParam.ip
      if (username !== undefined && username.length > 0) {
        param.username = "*" + username + "*"
      }
      if (logContent !== undefined && logContent.length > 0) {
        param.logContent = "*" + logContent + "*"
      }
      if (objectName !== undefined && objectName.length > 0) {
        param.objectName = "*" + objectName + "*"
      }
      if (userid !== undefined && userid.length > 0) {
        param.userid = "*" + userid + "*"
      }
      if (ip !== undefined && ip.length > 0) {
        param.ip = "*" + ip + "*"
      }
      param.field = this.getQueryField()
      param.pageNo = this.ipagination.current
      param.pageSize = this.ipagination.pageSize
      if (this.superQueryParams) {
        param['superQueryParams'] = encodeURI(this.superQueryParams)
        param['superQueryMatchType'] = this.superQueryMatchType
      }
      return filterObj(param)
    },
    getQueryField() {
      this.columnfields = "id";
      this.columns.push({
        title: '日志类型',
        dataIndex: 'logType_dictText',
        align: 'center',
      })
      // this.getQueryFieldHelper(this.columns);
      // this.getQueryField()
      this.columns.pop()
      return this.columnfields;
    },
    searchReset() {
      var that = this
      var logType = that.queryParam.logType
      that.queryParam = {} //清空查询区域参数
      that.queryParam.logType = logType
      that.loadData(this.ipagination.current)
    },
    handleExportXls(fileName) {
      if (!fileName || typeof fileName != "string") {
        fileName = this.logTypeTabsDefault[parseInt(this.tabKey)].title
      }
      let param = this.getQueryParams();
      console.log("导出参数", param)
      downFile(this.url.exportXlsUrl, param).then((data) => {
        if (!data) {
          this.$message.warning("文件下载失败")
          return
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(new Blob([data], {type: 'application/vnd.ms-excel'}), fileName + '.xls')
        } else {
          let url = window.URL.createObjectURL(new Blob([data], {type: 'application/vnd.ms-excel'}))
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = url
          link.setAttribute('download', fileName + '.xls')
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link); //下载完成移除元素
          window.URL.revokeObjectURL(url); //释放掉blob对象
        }
      })
    },
    onDateChange: function (value, dateString) {
      console.log(dateString[0], dateString[1])
      this.queryParam.createTime_begin = dateString[0]
      this.queryParam.createTime_end = dateString[1]
    },
    onDateOk(value) {
      console.log(value)
    }
  }
}
</script>
<style scoped>
@import '~@assets/less/common.less';
</style>