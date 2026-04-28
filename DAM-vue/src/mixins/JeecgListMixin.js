/**
 * 新增修改完成调用 modalFormOk方法 编辑弹框组件ref定义为modalForm
 * 高级查询按钮调用 superQuery方法  高级查询组件ref定义为superQueryModal
 * data中url定义 list为查询列表  delete为删除单条记录  deleteBatch为批量删除
 */
import { filterObj, replaceAll } from '@/utils/util'
import { deleteAction, downFile, getAction, getFileAccessHttpUrl, getFileAccessHttpUrlJszt } from '@/api/manage'
import Vue from 'vue'
import { ACCESS_TOKEN, TENANT_ID } from '@/store/mutation-types'
import store from '@/store'
import { ajaxGetDictItems } from '@api/api'

export const JeecgListMixin = {
  data() {
    return {
      /* 查询条件-请不要在queryParam中声明非字符串值的属性 */
      queryParam: {},
      securityEnums: {},
      /* 数据源 */
      dataSource: [],
      /* 分页参数 */
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
      /* 排序参数 */
      isorter: {
        column: 'createTime',
        order: 'desc'
      },
      /* 筛选参数 */
      filters: {},
      /* table加载状态 */
      loading: false,
      /* table选中keys*/
      selectedRowKeys: [],
      /* table选中records*/
      selectionRows: [],
      /* 查询折叠 */
      toggleSearchStatus: false,
      /* 高级查询条件生效状态 */
      superQueryFlag: false,
      /* 高级查询条件 */
      superQueryParams: '',
      /** 高级查询拼接方式 */
      superQueryMatchType: 'and',
      btnSubFix: ''
    }
  },
  created() {
    this.btnSubFix = replaceAll(window.location.pathname, '/', ':')
    if (!this.disableMixinCreated) {
      console.log(' -- mixin created -- ')
      this.loadData()
      //初始化字典配置 在自己页面定义
      this.initDictConfig()
    }
    ajaxGetDictItems('system_security', null).then((res) => {
      if (res.success) {
        this.securityEnums = res.result
      }
    })
  },
  computed: {
    //token header
    tokenHeader() {
      let head = { 'X-Access-Token': Vue.ls.get(ACCESS_TOKEN) }
      let tenantid = Vue.ls.get(TENANT_ID)
      if (tenantid) {
        head['tenant-id'] = tenantid
      }
      return head
    }
  },
  methods: {    //表格复选框属性
    checkBoxProps(record) {
      return {
        props: {
          disabled: record.createBy !== this.userInfo().realname //非自己创建的不可勾选
        }
      }
    },
    loadData(arg) {
      if (!this.url.list) {
        this.$message.error('请设置url.list属性!')
        return
      }
      //加载数据 若传入参数1则加载第一页的内容
      if (arg === 1) {
        this.ipagination.current = 1
      }
      var params = this.getQueryParams()//查询条件
      if (params.secretLevel === undefined || params.secretLevel === '') {
        params.secretLevel = sessionStorage.getItem('SECURITY')//前端添加密级筛选条件
      }
      this.loading = true
      getAction(this.url.list, params).then((res) => {
        if (res.success) {
          this.dataSource = res.result.records || res.result
          if (res.result.total) {
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
    initDictConfig() {
      console.log('--这是一个假的方法!')
    },
    handleSuperQuery(params, matchType) {
      //高级查询方法
      if (!params) {
        this.superQueryParams = ''
        this.superQueryFlag = false
      } else {
        this.superQueryFlag = true
        this.superQueryParams = JSON.stringify(params)
        this.superQueryMatchType = matchType
      }
      this.loadData(1)
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
      return filterObj(param)
    },
    getQueryField() {
      //TODO 字段权限控制
      var str = 'id,'
      this.columns.forEach(function(value) {
        str += ',' + value.dataIndex
      })
      return str
    },
    getQueryFieldHelper(columns) {
      let _that = this
      if (columns && columns.length > 0) {
        columns.forEach(function(value) {
          if (value.children && value.children.length > 0) {
            _that.getQueryFieldHelper(value.children)
          } else if (value.dataIndex && value.dataIndex != '' && value.dataIndex != 'action') {
            if (value.dataIndex.indexOf('_dictText') != -1) {
              let dataIndex = value.dataIndex.replace('_dictText', '')
              _that.columnfields += ',' + dataIndex
            } else {
              _that.columnfields += ',' + value.dataIndex
            }
          }
        })
      }
    },

    onSelectChange(selectedRowKeys, selectionRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectionRows = selectionRows
    },
    onClearSelected() {
      this.selectedRowKeys = []
      this.selectionRows = []
    },
    searchQuery() {
      this.loadData(1)
      this.selectedRowKeys = []
      this.selectionRows = []
    },
    superQuery() {
      this.$refs.superQueryModal.show()
    },
    searchReset() {
      this.queryParam = {}
      this.loadData(1)
    },
    batchDel: function() {
      if (!this.url.deleteBatch) {
        this.$message.error('请设置url.deleteBatch属性!')
        return
      }
      if (this.selectedRowKeys.length <= 0) {
        this.$message.warning('请选择一条记录！')
        return
      } else {
        var ids = ''
        for (var a = 0; a < this.selectedRowKeys.length; a++) {
          ids += this.selectedRowKeys[a] + ','
        }
        var that = this
        this.$confirm({
          title: '确认删除',
          content: '是否删除选中数据?',
          onOk: function() {
            that.loading = true
            deleteAction(that.url.deleteBatch, { ids: ids }).then((res) => {
              if (res.success) {
                //重新计算分页问题
                that.reCalculatePage(that.selectedRowKeys.length)
                that.$message.success(res.message)
                that.loadData()
                that.onClearSelected()
              } else {
                // 添加默认错误消息
                that.$message.warning(res.message || '删除失败，请重试')
              }
            }).finally(() => {
              that.loading = false
            })
          }
        })
      }
    },
    handleDelete: function(id) {
      if (!this.url.delete) {
        this.$message.error('请设置url.delete属性!')
        return
      }
      var that = this
      deleteAction(that.url.delete, { id: id }).then((res) => {
        if (res.success) {
          //重新计算分页问题
          that.reCalculatePage(1)
          that.$message.success(res.message)
          that.loadData()
        } else {
          // 添加默认错误消息
          that.$message.warning(res.message || '删除失败，请重试')
        }
      })
    },
    reCalculatePage(count) {
      //总数量-count
      let total = this.ipagination.total - count
      //获取删除后的分页数
      let currentIndex = Math.ceil(total / this.ipagination.pageSize)
      //删除后的分页数<所在当前页
      if (currentIndex < this.ipagination.current) {
        this.ipagination.current = currentIndex
      }
      console.log('currentIndex', currentIndex)
    },
    handleEdit: function(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
      this.$refs.modalForm.disableSubmit = false
    },
    handleAddTreeNode(num) {
      if (num === 1) {
        this.$refs.treeModal.add()
        this.$refs.treeModal.title = '新增'
      } else if (num === 2) {
        let id = this.currSelected.id
        if (!id) {
          this.$message.warning('请先点击选中上级产品目录！')
          return false
        }
        this.$refs.treeModal.add(this.currSelected.id, this.currSelected.secretLevel)
        this.$refs.treeModal.title = '新增'
      } else {
        this.$refs.treeModal.add(this.rightClickSelectedKey)
        this.$refs.treeModal.title = '新增'
      }
    },
    handleAdd: function() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
      this.$refs.modalForm.disableSubmit = false
    },
    //
    // handleShare: function() {
    //   this.$refs.userModal.show()
    //   this.$refs.userModal.title = '数据共享'
    // },
    show() {
      this.visible = true
    },
    handleCancel() {
      this.visible = false
    },
    handleDeleteTree(isLeaf) {
      // if (!isLeaf) {
      //   this.$message.warning('非叶子节点不能删除，请先删除该节点的所有叶子节点')
      //   return
      // }
      var that = this
      if (that.currSelected.id) {
        this.$confirm({
          title: '确认删除',
          content: '确定要删除此产品类别以及子节点（若有）数据吗?',
          onOk: function() {
            deleteAction(that.url.delete, { id: that.currSelected.id }).then((res) => {
              if (res.success) {
                //重新计算分页问题
                that.reCalculatePage(1)
                that.$message.success(res.message)
                that.onClearSelected()
                that.loadTree()
              } else {
                that.$message.warning(res.message)
              }
            })
          }
        })
      } else {
        that.$message.warning('请选择要删除的节点！')
      }
    },
    handleTableChange(pagination, filters, sorter) {
      console.log(pagination)
      if (Object.keys(sorter).length > 0) {
        this.isorter.column = sorter.field
        this.isorter.order = 'ascend' === sorter.order ? 'asc' : 'desc'
      }
      this.ipagination = pagination
      this.loadData()
    },
    handleToggleSearch() {
      this.toggleSearchStatus = !this.toggleSearchStatus
    },
    // 给popup查询使用(查询区域不支持回填多个字段，限制只返回一个字段)
    getPopupField(fields) {
      return fields.split(',')[0]
    },
    modalFormOk() {
      // 新增/修改 成功时，重载列表
      this.loadData()
      //清空列表选中
      this.onClearSelected()
    },
    handleDetail: function(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '详情'
      this.$refs.modalForm.disableSubmit = true
    },
    /* 导出 */
    handleExportXls2() {
      let paramsStr = encodeURI(JSON.stringify(this.getQueryParams()))
      let url = `${window._CONFIG['domianURL']}/${this.url.exportXlsUrl}?paramsStr=${paramsStr}`
      window.location.href = url
    },

    getSecurityText(security) {
      let texts = ['未知', '公开', '内部', '秘密', '机密', '绝密']
      return texts[security]
    },

    handleExportXls(excelSecretLevel, fileName) {
      if (!fileName || typeof fileName != 'string') {
        fileName = '导出文件'
      }
      if (excelSecretLevel === undefined || excelSecretLevel === '') {
        this.$message.warning('请选择导出数据的密级限制！')
        return
      }
      let param = this.getQueryParams()
      param.secretLevel = excelSecretLevel
      fileName = '(' + this.getSecurityText(excelSecretLevel) + ') ' + fileName
      if (this.selectedRowKeys && this.selectedRowKeys.length > 0) {
        param['selections'] = this.selectedRowKeys.join(',')
      }
      console.log('导出参数', param)
      this.generateDownloadContent(this.url.exportXlsUrl, param, fileName + '.xls')
    },
    generateDownloadContent: function(url, param, fileName) {
      downFile(url, param).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(new Blob([data]), fileName)
        } else {
          let url = window.URL.createObjectURL(new Blob([data]))
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = url
          link.setAttribute('download', fileName)
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
        }
      })
    },

    //按保密要求修改 自动计算文件密级，不再使用页面选择密级时 使用下边两个方法，
    //新增了下边两个方法（handleExportXlsCalculateSecurity、generateDownloadContentWithSecurity）
    handleExportXlsCalculateSecurity(fileName) {
      if (!fileName || typeof fileName != 'string') {
        fileName = '导出文件'
      }

      let param = this.getQueryParams()
      if (this.selectedRowKeys && this.selectedRowKeys.length > 0) {
        param['selections'] = this.selectedRowKeys.join(',')
      } else {
        this.$message.warning('请选择导出数据')
        return
      }
      console.log('导出参数', param)
      getAction(this.url.calculateSecurity, param).then(res => {
        if (res.success) {
          fileName = '(' + this.getSecurityText(res.result) + ') ' + fileName
          this.generateDownloadContentWithSecurity(this.url.exportXlsUrl, param, fileName + '.xls', res.result)
        } else {
          this.$error({ title: '计算密级失败', content: res.message })
        }
      }).finally(() => {

      })
    },
    generateDownloadContentWithSecurity: function(url, param, fileName, securityLevel) {
      param.securityLevelByCalculate = securityLevel
      downFile(url, param).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(new Blob([data]), fileName)
        } else {
          let url = window.URL.createObjectURL(new Blob([data]))
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = url
          link.setAttribute('download', fileName)
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
        }
      })
    },

    // 下载模板方法，避免自动触发下载
    handleExportXlsTemplate(fileName) {
      // window.location.href = '/templates/' + fileName + '.xls'
      const link = document.createElement('a')
      link.href = '/templates/' + fileName + '.xls'
      link.download = fileName + '.xls'
      link.style.display = 'none'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    },
    /* 导入 */



    handleImportExcel(info) {
      this.loading = true
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (info.file.status === 'done') {
        this.loading = false
        if (info.file.response.success) {
          // this.$message.success(`${info.file.name} 文件上传成功`);
          if (info.file.response.code === 201) {
            let { message, result: { msg, fileUrl, fileName } } = info.file.response
            let href = window._CONFIG['domianURL'] + fileUrl
            this.$warning({
              title: message,
              content: (<div>
                  <span>{msg}</span><br />
                  <span>具体详情请 <a href={href} target="_blank" download={fileName}>点击下载</a> </span>
                </div>
              )
            })
          } else {
            //此处弹出提示

            window.alert(info.file.response.message)
            // this.$message.success(info.file.response.message || `${info.file.name} 文件上传成功`, 10)
          }
          this.loadData()
        } else {
          window.alert(info.file.response.message)
          // this.$message.error(`${info.file.name} ${info.file.response.message}.`)
        }
      } else if (info.file.status === 'error') {
        this.loading = false
        if (info.file.response.status === 500) {
          let data = info.file.response
          const token = Vue.ls.get(ACCESS_TOKEN)
          if (token && data.message.includes('Token失效')) {
            this.$error({
              title: '登录已过期',
              content: '很抱歉，登录已过期，请重新登录',
              okText: '重新登录',
              mask: false,
              onOk: () => {
                store.dispatch('Logout').then(() => {
                  Vue.ls.remove(ACCESS_TOKEN)
                  window.location.reload()
                })
              }
            })
          }
        } else {
          this.$message.error(`${info.file.name}文件上传失败: ${info.file.msg} `, 10)
        }
      }
    },

    /* 图片预览 */
    getImgView(text) {
      if (text && text.indexOf(',') > 0) {
        text = text.substring(0, text.indexOf(','))
      }
      return getFileAccessHttpUrl(text)
    },
    /* 文件下载 */
    downloadFile(text) {
      if (!text) {
        this.$message.warning('未知的文件')
        return
      }
      if (text.indexOf(',') > 0) {
        text = text.substring(0, text.indexOf(','))
      }
      let url = getFileAccessHttpUrl(text)
      window.open(url)
    },
    /**
     * 文件下载2--jszt后端
     */
    downloadFileJszt(text){
      if (!text) {
        this.$message.warning('未知的文件')
        return
      }
      if (text.indexOf(',') > 0) {
        text = text.substring(0, text.indexOf(','))
      }
      let url = getFileAccessHttpUrlJszt(text)
      window.open(url)
    },

  }

}