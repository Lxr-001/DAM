import { EventBus } from '@/Bus'
import { queryPBSTctlTreeList, queryPBSTreeList } from '@api/api'
import { SkeletonProps as node } from 'ant-design-vue/lib/skeleton'
import { getAction } from '@api/manage'

export const TreeMixin = {
  data() {
    return {
      id2LevelMap: {},
      currSelected: {},
      //树上的参数
      checkedKeys: [],
      parentMap: {}, // 存储子节点到父节点的映射 { childKey: parentNode}
      selectedKeys: [],
      catalogTree: [],
      formattedTreeData: [],
      treeData: [],
      checkStrictly: true,
      autoExpandParent: true,
      iExpandedKeys: [],
      isLeaf: false,
      showSubNodesInfo: false,
      disabled: true,
      nodeSelected: false
    }
  },
  methods: {
    loadData() {
      this.refresh()
    },
    refresh() {
      this.loading = true
      this.loadTree()
    },
    setThisExpandedKeys(node) {
      if (node.children && node.children.length > 0) {
        this.iExpandedKeys.push(node.key)
        for (let a = 0; a < node.children.length; a++) {
          this.setThisExpandedKeys(node.children[a])
        }
      }
    },
    getAllKeys(node) {
      this.allTreeKeys.push(node.key)
      if (node.children && node.children.length > 0) {
        for (let a = 0; a < node.children.length; a++) {
          this.getAllKeys(node.children[a])
        }
      }
    },
    processTreeData(nodes, level = 0, map = {}) {
      return nodes.map(node => {
        // 记录当前节点的 key 和 level
        map[node.id] = level

        // 处理 isLeaf 字段转换：将字符串转换为布尔值
        // 根据后端实际返回值调整判断条件
        const isLeafBoolean = node.isLeaf === 'true' || node.isLeaf === '1'

        // 递归处理子节点
        if (node.children && node.children.length > 0) {
          node.children = this.processTreeData(node.children, level + 1, map)
        }

        // 返回处理后的节点（添加 level 属性，并转换 isLeaf 字段）
        return {
          ...node,
          level: level,
          isLeaf: isLeafBoolean, // 覆盖原 isLeaf 字段为布尔值
          originalIsLeaf: node.isLeaf, // 保留原始 isLeaf 值，以备查询时使用
          children: node.children
        }
      })
    },
    loadTree() {
      var that = this
      that.treeData = []
      that.catalogTree = []
      queryPBSTreeList({ showModel: false }).then((res) => {
        if (res.success) {
          //产品目录全选后，再添加产品目录，选中数量增多
          this.allTreeKeys = []
          for (let i = 0; i < res.result.length; i++) {
            let temp = res.result[i]
            that.treeData.push(temp)
            that.catalogTree.push(temp)
            that.setThisExpandedKeys(temp)
            that.getAllKeys(temp)
          }
          this.treeData = this.processTreeData(this.treeData, 0, this.id2LevelMap)
          this.loading = false
        }
      })
    },
    loadTctlTree() {
      var that = this
      that.treeData = []
      that.catalogTree = []
      queryPBSTctlTreeList({ showModel: false }).then((res) => {
        if (res.success) {
          //产品目录全选后，再添加产品目录，选中数量增多
          this.allTreeKeys = []
          for (let i = 0; i < res.result.length; i++) {
            let temp = res.result[i]
            that.treeData.push(temp)
            that.catalogTree.push(temp)
            that.setThisExpandedKeys(temp)
            that.getAllKeys(temp)
          }
          this.treeData = this.processTreeData(this.treeData, 0, this.id2LevelMap)
          this.loading = false
        }
      })
    },
    onExpand(expandedKeys) {
      this.iExpandedKeys = expandedKeys
      this.autoExpandParent = false
    },
    getCurrSelectedTitle() {
      return !this.currSelected.title ? '' : this.currSelected.title
    },
    // 触发onSelect事件时,为 pbs 树右侧的form表单赋值
    setValuesToForm(record) {
      this.orgCategoryDisabled = record.orgCategory === '1'
    },

    loadRightData(bid) {
      if (!this.url.list) {
        this.$message.error('请设置url.list属性!')
        return
      }
      var params = { bid: bid }//查询条件
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

    // 展开左侧菜单树形结构并在点击树的根节点时渲染右侧对应的表格
    onSelect(selectedKeys, e) {
      this.nodeSelected = true
      this.hiding = false
      let record = e.node.dataRef
      this.isLeaf = record.isLeaf
      console.log('叶子 TreeMixin', this.isLeaf)
      // record.id 需要作为查询条件
      this.nodeId = record.id
      this.showSubNodesInfo = !this.isLeaf
      if (this.showSubNodesInfo) {
        this.loadRightData(record.id)
      }
      EventBus.$emit('changeNodeId', this.nodeId)
      this.title = record.title
      this.level = this.id2LevelMap[this.nodeId]
      this.currSelected = Object.assign({}, record)
      this.model = this.currSelected
      this.selectedKeys = [record.key]
      this.model.bid = record.bid
      this.setValuesToForm(record)
      this.timer = new Date().getTime()
      //EventBus.$emit('reloadTctl')

      // this.$refs.departAuth.show(record.id)
      // this.$nextTick(() => {
      //   this.$refs.infoModal.show()
      // })
    },
    onClearSelected() {
      this.hiding = true
      this.nodeId = 'bid'
      EventBus.$emit('changeNodeId', 'bid')
      EventBus.$emit('reloadTctl')
      this.nodeSelected = false
      this.checkedKeys = []
      this.currSelected = {}
      this.selectedKeys = []
      this.timer = new Date().getTime()
    },
    // 把选中的根节点的 id 传到后端
    handleExportXlsx(excelSecretLevel, fileName) {
      if (!fileName || typeof fileName != 'string') {
        fileName = '导出文件'
      }
      if (excelSecretLevel === undefined || excelSecretLevel === '') {
        this.$message.warning('请选择导出数据的密级限制！')
        return
      }
      const idsData = this.checkedKeys.checked.join(',')
      console.log('this.checkedkeys:', idsData)

      let param = { 'ids': idsData, 'secretLevel': excelSecretLevel }
      fileName = '(' + this.getSecurityText(excelSecretLevel) + ') ' + fileName
      if (this.selectedRowKeys && this.selectedRowKeys.length > 0) {
        param['selections'] = this.selectedRowKeys.join(',')
      }
      console.log('导出参数', param)
      this.generateDownloadContent(this.url.exportXlsUrl, param, fileName + '.xls')
    },
    // 右键操作方法
    rightHandle() {
      this.dropTrigger = 'contextmenu'
      this.rightClickSelectedKey = node.node.eventKey
      this.rightClickSelectedOrgCode = node.node.dataRef.orgCode
    },
    handleCheck(checkedKeys) {
      this.checkedKeys = checkedKeys
    }
  }
}
