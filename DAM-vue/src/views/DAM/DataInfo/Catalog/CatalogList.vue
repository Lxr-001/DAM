<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <!-- <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div> -->
    <!-- 查询区域-END -->

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <!-- 高级查询区域 -->
      <!-- <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query> -->
      <!-- <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown> -->
    </div>

    <!-- table区域-begin -->
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
        :expandedRowKeys="expandedRowKeys"
        @change="handleTableChange"
        @expand="handleExpand"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange,type:'radio'}"
        v-bind="tableProps">

        <span slot="catalogName" slot-scope="text, record" class="catalog-name-cell">
          <a-icon
            v-if="hasTreeChildren(record)"
            :type="isCatalogRowExpanded(record) ? 'folder-open' : 'folder'"
            class="catalog-folder-icon"
          />
          {{ text != null && text !== '' ? text : record.catalogName }}
        </span>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDeleteNode(record)" placement="topLeft">
            <a>删除</a>
          </a-popconfirm>
          <a-divider type="vertical" />
          <a @click="handleAddChild(record)">添加下级</a>
        </span>

      </a-table>
    </div>

    <catalog-modal ref="modalForm" @ok="modalFormOk"></catalog-modal>
  </a-card>
</template>

<script>

  import { getAction, deleteAction } from '@/api/manage'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import CatalogModal from './modules/CatalogModal'
  import {filterMultiDictText} from '@/components/dict/JDictSelectUtil'
  import { filterObj } from '@/utils/util';

  export default {
    name: "CatalogList",
    mixins:[JeecgListMixin],
    components: {
      CatalogModal
    },
    data () {
      return {
        description: '数据目录管理页面',
        orderNumberField:"orderNumber",
        // 表头
        columns: [
          {
            title:'数据名录',
            align:"left",
            dataIndex: 'catalogName',
            scopedSlots: { customRender: 'catalogName' },
          },
          {
            title:'序号',
            align:"left",
            dataIndex: 'orderNumber'
          },
          {
            title:'存储',
            align:"left",
            dataIndex: 'storage'
          },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:200,
            scopedSlots: { customRender: 'action' },
          }
        ],
        url: {
          list: "/Catalog/rootList",
          childList: "/Catalog/childList",
          getChildListBatch: "/Catalog/getChildListBatch",
          delete: "/Catalog/delete",
          deleteBatch: "/Catalog/deleteBatch",
        },
        expandedRowKeys:[],
        hasChildrenField:"hasChild",
        pidField:"pid",
        dictOptions: {},
        loadParent: false,
        superFieldList:[],
      }
    },
    created() {
      this.getSuperFieldList();
    },
    computed: {
      importExcelUrl(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
      tableProps() {
        let _this = this
        return {
          // 列表项是否可选择
          rowSelection: {
            selectedRowKeys: _this.selectedRowKeys,
            onChange: (selectedRowKeys) => _this.selectedRowKeys = selectedRowKeys
          }
        }
      }
    },
    methods: {
      /**
       * 判断树节点是否含有子节点（用于显示文件夹图标）
       * @param {Record<string, any>} record 表格行数据
       * @returns {boolean} 是否有子节点
       */
      hasTreeChildren(record) {
        if (!record) return false
        if (String(record[this.hasChildrenField]) === '1') return true
        const children = record.children
        return Array.isArray(children) && children.length > 0
      },
      /**
       * 当前行是否处于表格展开状态（兼容 id 为 number / string）
       * @param {Record<string, any>} record 表格行数据
       * @returns {boolean}
       */
      isCatalogRowExpanded(record) {
        if (!record || record.id == null) return false
        const rowId = record.id
        return this.expandedRowKeys.some(
          (key) => key === rowId || String(key) === String(rowId),
        )
      },
      /**
       * 将列表按 pid 再按序号（orderNumber）升序排序。
       * 主要用于 child 批量返回时，确保同一父级下的子节点展示顺序一致。
       * @param {Array<any>} dataList 原始列表
       * @returns {Array<any>} 排序后的新数组
       */
      sortByPidAndOrderNumber(dataList) {
        if (!Array.isArray(dataList) || dataList.length === 0) return [];
        const orderNumberKey = this.orderNumberField;
        const pidKey = this.pidField;
        return dataList.slice().sort((a, b) => {
          const pidA = a[pidKey];
          const pidB = b[pidKey];

          // 同 pid 时：按序号升序
          if (pidA === pidB) {
            const orderA = Number(a[orderNumberKey]);
            const orderB = Number(b[orderNumberKey]);
            return (Number.isNaN(orderA) ? 0 : orderA) - (Number.isNaN(orderB) ? 0 : orderB);
          }

          // pid 不同：保证结果稳定（优先数值比较，否则字符串比较）
          const pidANum = Number(pidA);
          const pidBNum = Number(pidB);
          if (!Number.isNaN(pidANum) && !Number.isNaN(pidBNum)) {
            return pidANum - pidBNum;
          }
          return String(pidA).localeCompare(String(pidB), 'zh-Hans-CN', { numeric: true });
        });
      },
      /**
       * 将同一 pid 下的子节点按序号升序排序。
       * @param {Array<any>} dataList 原始列表
       * @returns {Array<any>} 排序后的新数组
       */
      sortByOrderNumber(dataList) {
        if (!Array.isArray(dataList) || dataList.length === 0) return [];
        const orderNumberKey = this.orderNumberField;
        return dataList.slice().sort((a, b) => {
          const orderA = Number(a[orderNumberKey]);
          const orderB = Number(b[orderNumberKey]);
          return (Number.isNaN(orderA) ? 0 : orderA) - (Number.isNaN(orderB) ? 0 : orderB);
        });
      },
      loadData(arg){
        if(arg==1){
          this.ipagination.current=1
        }
        this.loading = true
        let params = this.getQueryParams()
        params.hasQuery = 'true'
        getAction(this.url.list,params).then(res=>{
          if(res.success){
            let result = res.result
            if(Number(result.total)>0){
              this.ipagination.total = Number(result.total)
              // 根列表也保证同 pid（通常为同一父级）下按序号升序展示
              this.dataSource = this.sortByPidAndOrderNumber(this.getDataByResult(res.result.records))
              return this.loadDataByExpandedRows(this.dataSource)
            }else{
              this.ipagination.total=0
              this.dataSource=[]
            }
          }else{
            this.$message.warning(res.message)
          }
        }).finally(()=>{
          this.loading = false
        })
      },
      // 根据已展开的行查询数据（用于保存后刷新时异步加载子级的数据）
      loadDataByExpandedRows(dataList) {
        if (this.expandedRowKeys.length > 0) {
          return getAction(this.url.getChildListBatch,{ parentIds: this.expandedRowKeys.join(',') }).then(res=>{
            if (res.success && res.result.records.length>0) {
              //已展开的数据批量子节点
              let records = res.result.records
              const listMap = new Map();
              for (let item of records) {
                let pid = item[this.pidField];
                if (this.expandedRowKeys.join(',').includes(pid)) {
                 let mapList = listMap.get(pid);
                  if (mapList == null) {
                    mapList = [];
                  }
                  mapList.push(item);
                  listMap.set(pid, mapList);
                }
              }

              // 确保同一 pid 下的子节点按序号升序排列展示
              for (let [pid, list] of listMap.entries()) {
                listMap.set(pid, this.sortByOrderNumber(list));
              }
              let childrenMap = listMap;
              let fn = (list) => {
                if(list) {
                  list.forEach(data => {
                    if (this.expandedRowKeys.includes(data.id)) {
                      data.children = this.getDataByResult(childrenMap.get(data.id))
                      fn(data.children)
                    }
                  })
                }
              }
              fn(dataList)
            }
          })
        } else {
          return Promise.resolve()
        }
      },
      getQueryParams(arg) {
        //获取查询条件
        let sqp = {}
        let param = {}
        if(this.superQueryParams){
          sqp['superQueryParams']=encodeURI(this.superQueryParams)
          sqp['superQueryMatchType'] = this.superQueryMatchType
        }
        if(arg){
          param = Object.assign(sqp, this.isorter ,this.filters);
        }else{
          param = Object.assign(sqp, this.queryParam, this.isorter ,this.filters);
        }
        if(JSON.stringify(this.queryParam) === "{}" || arg){
          param.hasQuery = 'false'
        }else{
          param.hasQuery = 'true'
        }
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        return filterObj(param);
      },
      searchReset() {
        //重置
        this.expandedRowKeys = []
        this.queryParam = {}
        this.loadData(1);
      },
      getDataByResult(result){
        if(result){
          return result.map(item=>{
            //判断是否标记了带有子节点
            if(item[this.hasChildrenField]=='1'){
              let loadChild = { id: item.id+'_loadChild', name: 'loading...', isLoading: true }
              item.children = [loadChild]
            }
            return item
          })
        }
      },
      handleExpand(expanded, record){
        // 判断是否是展开状态
        if (expanded) {
          this.expandedRowKeys.push(record.id)
          const children = record.children
          if (Array.isArray(children) && children.length > 0 && children[0].isLoading === true) {
            let params = this.getQueryParams(1);//查询条件
            params[this.pidField] = record.id
            params.hasQuery = 'false'
            params.superQueryParams=""
            getAction(this.url.childList,params).then((res)=>{
              if(res.success){
                if(res.result.records){
                  // 展开加载的子节点也保证按序号升序展示
                  record.children = this.sortByOrderNumber(this.getDataByResult(res.result.records))
                  this.dataSource = [...this.dataSource]
                }else{
                  record.children=''
                  record[this.hasChildrenField] = '0'
                }
              }else{
                this.$message.warning(res.message)
              }
            })
          }
        }else{
          let keyIndex = this.expandedRowKeys.indexOf(record.id)
          if(keyIndex>=0){
            this.expandedRowKeys.splice(keyIndex, 1);
          }
        }
      },
      handleAddChild(record){
        this.loadParent = true
        let obj = {}
        obj[this.pidField] = record['id']
        this.$refs.modalForm.add(obj);
      },
      /**
       * 在树形数据中按 id 查找节点
       * @param {Array<Record<string, any>>} treeList 树形列表
       * @param {string|number} id 节点 id
       * @returns {Record<string, any>|null} 命中的节点
       */
      findNodeById(treeList, id) {
        if (!Array.isArray(treeList) || treeList.length === 0) return null
        for (let i = 0; i < treeList.length; i += 1) {
          const item = treeList[i]
          if (item && (item.id === id || String(item.id) === String(id))) {
            return item
          }
          const childNode = this.findNodeById(item && item.children, id)
          if (childNode) return childNode
        }
        return null
      },
      /**
       * 删除节点（父节点下有子节点时禁止删除）
       * @param {Record<string, any>|string|number} recordOrId 行数据或节点 id
       * @returns {void}
       */
      handleDeleteNode(recordOrId) {
        if(!this.url.delete){
          this.$message.error("请设置url.delete属性!")
          return
        }
        const isRecord = recordOrId && typeof recordOrId === 'object'
        const targetRecord = isRecord ? recordOrId : this.findNodeById(this.dataSource, recordOrId)
        if (targetRecord && this.hasTreeChildren(targetRecord)) {
          this.$message.warning('当前节点下，包含多个子目录，无法删除')
          return
        }
        const targetId = isRecord ? targetRecord.id : recordOrId
        var that = this;
        deleteAction(that.url.delete, {id: targetId}).then((res) => {
          if (res.success) {
             that.loadData(1)
          } else {
            that.$message.warning(res.message);
          }
        });
      },
      batchDel(){
        if(this.selectedRowKeys.length<=0){
          this.$message.warning('请选择一条记录！');
          return false;
        }else{
          let ids = "";
          let that = this;
          that.selectedRowKeys.forEach(function(val) {
            ids+=val+",";
          });
          that.$confirm({
            title:"确认删除",
            content:"是否删除选中数据?",
            onOk: function(){
              that.handleDeleteNode(ids)
              that.onClearSelected();
            }
          });
        }
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'pid',text:'父级节点',dictCode:''})
        fieldList.push({type:'string',value:'catalogName',text:'数据名录',dictCode:''})
        fieldList.push({type:'string',value:'orderNumber',text:'序号',dictCode:''})
        fieldList.push({type:'string',value:'storage',text:'存储',dictCode:''})
        this.superFieldList = fieldList
      }
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  .catalog-name-cell {
    display: inline-flex;
    align-items: center;
  }
  .catalog-folder-icon {
    margin-right: 8px;
    color: rgba(0, 0, 0, 0.45);
  }
</style>