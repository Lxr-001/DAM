<template>
  <div>
    <a-modal
      centered
      :title="title"
      :width="1200"
      :visible="visible"
      wrapClassName="select-user-modal"
      @ok="handleOk"
      @cancel="handleCancel"
      cancelText="关闭">
      <a-row :gutter="10" class="depart-layout-row">
        <a-col :sm="24" class="depart-left-col">
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

        <a-col :sm="24" class="depart-right-col">
          <a-card :bordered="false" class="depart-side-card">
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
                    <a-form-item label="用户名称">
                      <a-input placeholder="请输入用户名称" v-model="queryParam.realname"></a-input>
                    </a-form-item>
                  </a-col>
                  <a-col :span="8">
                    <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                      <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                      <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                    </span>
                  </a-col>
                </a-row>
              </a-form>
            </div>

            <!-- table区域-begin -->
            <div>
              <a-empty v-if="shouldShowEmpty()">
                <span slot="description"> 请先选择一个部门，或输入用户账号/用户名称后查询 </span>
              </a-empty>
              <a-table
                v-else
                size="small"
                bordered
                rowKey="id"
                :columns="columns1"
                :dataSource="dataSource1"
                :pagination="ipagination"
                :loading="loading"
                :scroll="{ y: 420 }"
                :rowSelection="{selectedRowKeys: selectedRowKeys,onSelectAll:onSelectAll,onSelect:onSelect,onChange: onSelectChange}"
                @change="handleTableChange"
              >
              </a-table>
            </div>
            <!-- table区域-end -->
          </a-card>
        </a-col>
      </a-row>


    </a-modal>
  </div>
</template>

<script>
  import {filterObj} from '@/utils/util'
  import {getAction} from '@/api/manage'
  import { queryDepartTreeList, searchByKeywords } from '@/api/api'

  export default {
    name: "SelectUserModal",
    data() {
      return {
        title: "添加已有用户",
        names: [],
        visible: false,
        maxDataSecurity : 1,
        placement: 'right',
        description: '',
        // 部门树数据
        departTree: [],
        selectedDepartKeys: [],
        expandedKeys: [],
        autoExpandParent: true,
        currSelected: {},
        // 查询条件
        queryParam: {},
        // 表头
        columns1: [
          {
            title: '序号',
            dataIndex: '',
            key: 'rowIndex',
            width: 50,
            align: "center",
            customRender: function (t, r, index) {
              return parseInt(index) + 1;
            }
          },
          {
            title: '用户账号',
            align: "center",
            width: 100,
            dataIndex: 'username'
          },
          {
            title: '用户名称',
            align: "center",
            width: 100,
            dataIndex: 'realname'
          },
          {
            title: '性别',
            align: "center",
            width: 100,
            dataIndex: 'sex_dictText'
          },
          // {
          //   title: '电话',
          //   align: "center",
          //   width: 100,
          //   dataIndex: 'phone'
          // },
          {
            title: '密级',
            align: "center",
            width: 100,
            dataIndex: 'security',
            customRender: function (text) {
              if (text == 1) {
                return "非密";
              } else if (text == 2) {
                return "非密";
              }else if (text == 3) {
                return "一般";
              }else if (text == 4) {
                return "重要";
              }else if (text == 5) {
                return "核心";
              } else {
                return text;
              }
            }
          },
          // {
          //   title: '部门',
          //   align: "center",
          //   width: 150,
          //   dataIndex: 'orgCode'
          // }
        ],
        columns2: [
          {
            title: '用户账号',
            align: "center",
            dataIndex: 'username',

          },
          {
            title: '用户名称',
            align: "center",
            dataIndex: 'realname',
          },
          {
            title: '操作',
            dataIndex: 'action',
            align: "center",
            width: 100,
            scopedSlots: {customRender: 'action'},
          }
        ],
        //数据集
        dataSource1: [],
        dataSource2: [],
        // 分页参数
        ipagination: {
          current: 1,
          pageSize: 10,
          pageSizeOptions: ['10', '20', '30'],
          showTotal: (total, range) => {
            return range[0] + "-" + range[1] + " 共" + total + "条"
          },
          showQuickJumper: true,
          showSizeChanger: true,
          total: 0
        },
        isorter: {
          column: 'createTime',
          order: 'desc',
        },
        loading: false,
        selectedRowKeys: [],
        selectedRows: [],
        url: {
          list: "/sys/user/list",
          listInSecurity: "/sys/user/listInSecurity",

        }
      }
    },
    created() {
      // 弹窗内列表数据依赖“已选择部门”，这里不主动加载用户列表
    },
    watch:{
      visible:{
        handler(val) {
          if(val == true){
            this.loadDepartTree()
            // 未选择部门时不展示任何用户数据
            this.onClearUserTable()
          }
        },
        immediate: true,
        deep: false
      },
    },
    methods: {
      searchQuery() {
        this.loadData(1);
      },
      searchReset() {
        this.queryParam = {};
        this.loadData(1);
      },
      handleCancel() {
        this.visible = false;
      },
      handleOk() {
        this.dataSource2 = this.selectedRowKeys;
        console.log("data:" + this.dataSource2);
        this.$emit("selectFinished", this.dataSource2);
        this.visible = false;
      },
      add() {
        this.visible = true;
      },
      loadDepartTree() {
        this.departTree = []
        this.expandedKeys = []
        this.autoExpandParent = true
        queryDepartTreeList().then((res) => {
          if (res.success) {
            const treeList = Array.isArray(res.result) ? res.result : []
            this.departTree = treeList
            this.expandedKeys = this.collectExpandedKeys(treeList)
          }
        })
      },
      /**
       * 收集默认展开节点 key（沿用 DepartList 的“展开有子节点的节点”思路）
       * @param {Array} treeList 部门树
       * @returns {Array<string>} expandedKeys
       */
      collectExpandedKeys(treeList) {
        const expandedKeys = []
        const travel = (nodeList) => {
          if (!Array.isArray(nodeList)) return
          nodeList.forEach((node) => {
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
          searchByKeywords({ keyWord: value }).then((res) => {
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
          // a-tree 的节点 id 通常在 eventKey/selectedKeys 中
          const currentOrgUnit = (e && e.node && e.node.eventKey) || selectedKeys[0] || record.id || record.key
          this.selectedDepartKeys = currentOrgUnit ? [currentOrgUnit] : []
          this.currSelected = Object.assign({}, record)
          this.onClearSelected()
          this.loadData(1)
        }
      },
      onClearSelectedDepart() {
        this.selectedDepartKeys = []
        this.currSelected = {}
        this.onClearSelected()
        this.onClearUserTable()
      },
      /**
       * 清空右侧表格数据（用于“未选择部门不展示任何用户信息”）
       */
      onClearUserTable() {
        this.dataSource1 = []
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
      loadData(arg) {
        // 未选择部门且未输入账号：不请求接口，不展示用户信息
        if (!this.hasSelectedDepartment() && !this.hasUserSearchKeyword()) {
          this.onClearUserTable()
          return
        }
        //加载数据 若传入参数1则加载第一页的内容
        if (arg === 1) {
          this.ipagination.current = 1;
        }
        var params = this.getQueryParams();//查询条件
        params.maxDataSecurity = this.maxDataSecurity;
        getAction(this.url.listInSecurity, params).then((res) => {
          if (res.success) {
            // this.dataSourceAll = res.result.records;
            // this.dataSourceOutCome = dataSourceAll.filter(row => row.security >= this.max);
            this.dataSource1 = res.result.records;
            this.ipagination.total = res.result.total;
          }
        })
      },
      getQueryParams() {
        var param = Object.assign({}, this.queryParam, this.isorter);
        param.field = this.getQueryField();
        param.pageNo = this.ipagination.current;
        param.pageSize = this.ipagination.pageSize;
        // 部门过滤：历史页面使用 orgunit（全小写），同时兼容 departId（两者都带上，避免接口字段差异）
        const selectedOrgUnit = this.selectedDepartKeys.join(',')
        if (selectedOrgUnit) {
          param.orgunit = selectedOrgUnit
          param.departId = selectedOrgUnit
        }
        return filterObj(param);
      },
      getQueryField() {
        //TODO 字段权限控制
      },
      onSelectAll(selected, selectedRows, changeRows) {
        if (selected === true) {
          for (var a = 0; a < changeRows.length; a++) {
            this.dataSource2.push(changeRows[a]);
          }
        } else {
          for (var b = 0; b < changeRows.length; b++) {
            this.dataSource2.splice(this.dataSource2.indexOf(changeRows[b]), 1);
          }
        }
        // console.log(selected, selectedRows, changeRows);
      },
      onSelect(record, selected) {
        if (selected === true) {
          this.dataSource2.push(record);
        } else {
          var index = this.dataSource2.indexOf(record);
          //console.log();
          if (index >= 0) {
            this.dataSource2.splice(this.dataSource2.indexOf(record), 1);
          }

        }
      },
      onSelectChange(selectedRowKeys, selectedRows) {
        this.selectedRowKeys = selectedRowKeys;
        this.selectionRows = selectedRows;
      },
      onClearSelected() {
        this.selectedRowKeys = [];
        this.selectionRows = [];
      },
      handleDelete: function (record) {
        this.dataSource2.splice(this.dataSource2.indexOf(record), 1);
      },
      handleTableChange(pagination, filters, sorter) {
        //分页、排序、筛选变化时触发
        console.log(sorter);
        //TODO 筛选
        if (Object.keys(sorter).length > 0) {
          this.isorter.column = sorter.field;
          this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
        }
        this.ipagination = pagination;
        this.loadData();
      }
    }
  }
</script>
<style lang="less" scoped>
  /* 与 DepartList.vue 一致：两列 + gutter，行背景浅灰才能显出中间间隙（白卡片之间的灰缝） */
  ::v-deep .select-user-modal {
    .ant-modal-body {
      padding: 12px 16px;
      background: #f0f2f5;
    }
  }

  .depart-layout-row {
    display: flex;
    align-items: stretch;
    padding: 8px;
    background: #f0f2f5;
    border-radius: 2px;
  }

  .depart-left-col {
    flex: 0 0 40%;
    max-width: 40%;
  }

  .depart-right-col {
    flex: 0 0 60%;
    max-width: 60%;
  }

  .depart-side-card {
    height: 100%;
    background: #fff;
  }

  .select-user-tree-panel {
    background: #fff;
    padding-left: 16px;
    margin-top: 5px;
  }

  .select-user-tree-scroll {
    margin-top: 10px;
    max-height: 520px;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 8px;
  }

  @media (max-width: 991px) {
    .depart-left-col,
    .depart-right-col {
      flex: 0 0 100%;
      max-width: 100%;
    }
  }

  .ant-card-body .table-operator {
    margin-bottom: 18px;
  }

  .ant-table-tbody .ant-table-row td {
    padding-top: 15px;
    padding-bottom: 15px;
  }

  .anty-row-operator button {
    margin: 0 5px
  }

  .ant-btn-danger {
    background-color: #ffffff
  }

  .ant-modal-cust-warp {
    height: 100%
  }

  .ant-modal-cust-warp .ant-modal-body {
    height: calc(100% - 110px) !important;
    overflow-y: auto
  }

  .ant-modal-cust-warp .ant-modal-content {
    height: 90% !important;
    overflow-y: hidden
  }
</style>