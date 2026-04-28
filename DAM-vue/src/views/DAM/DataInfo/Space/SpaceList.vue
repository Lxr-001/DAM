<template>
  <a-row :gutter="12" class="space-layout-row">
    <a-col :sm="24" :md="8" :lg="6" class="space-left-col">
      <a-card :bordered="false" class="space-side-card">
        <div class="space-tree-panel">
          <a-alert type="info" :showIcon="true">
            <div slot="message">
              当前选择：<span v-if="currSelected.title">{{ currSelected.title }}</span>
              <a v-if="currSelected.title" style="margin-left: 10px" @click="onClearDepartSelected">取消选择</a>
            </div>
          </a-alert>
          <a-input-search
            style="width:100%;margin-top: 10px"
            placeholder="请输入部门名称"
            @search="onSearchDepart"
          />
          <div class="space-tree-scroll">
            <a-tree
              :selectedKeys="selectedDepartKeys"
              :treeData="departTree"
              :expandedKeys="expandedDepartKeys"
              :autoExpandParent="autoExpandParent"
              @select="onSelectDepart"
              @expand="onExpandDepart"
            />
          </div>
        </div>
      </a-card>
    </a-col>

    <a-col :sm="24" :md="16" :lg="18" class="space-right-col">
      <a-card :bordered="false" class="space-side-card">
        <div class="space-filter-bar">
          <a-icon type="filter" />
          <span class="space-filter-label">筛选条件：</span>
          <a-select v-model="filters.type" style="width: 140px" @change="onFilterChange">
            <a-select-option value="all">全部类型</a-select-option>
            <a-select-option value="public">公共</a-select-option>
            <a-select-option value="private">私有</a-select-option>
            <a-select-option value="project">项目</a-select-option>
          </a-select>
          <!-- <a-select v-model="filters.status" style="width: 140px" @change="onFilterChange">
            <a-select-option value="all">全部状态</a-select-option>
            <a-select-option value="normal">正常</a-select-option>
            <a-select-option value="full">已满</a-select-option>
            <a-select-option value="expiring">即将满载</a-select-option>
          </a-select> -->
        </div>

        <div class="space-header">
          <div class="space-header-left">
            <h3 class="space-title">
              全部空间 <span class="space-count">({{ filteredDataSource.length }})</span>
            </h3>
            <span v-if="selectedRowKeys.length > 0" class="space-selected-tip">已选择 {{ selectedRowKeys.length }} 个空间</span>
          </div>
          <div class="space-header-actions">
            <div class="space-action-rect">
              <a-radio-group v-model="viewMode" button-style="solid" class="space-view-toggle">
                <a-radio-button value="grid"><a-icon type="appstore" /></a-radio-button>
                <a-radio-button value="list"><a-icon type="unordered-list" /></a-radio-button>
              </a-radio-group>
            </div>
            <div class="space-action-rect">
              <a-button type="primary" icon="plus" block class="space-add-btn" @click="handleAdd">新增</a-button>
            </div>
          </div>
        </div>

        <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
          <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择
          <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
          <a style="margin-left: 24px" @click="onClearSelected">清空</a>
        </div>

        <template v-if="viewMode === 'grid'">
          <a-row :gutter="[12, 12]">
            <a-col v-for="item in gridDataSource" :key="item.id" :xs="24" :sm="12" :xl="8">
              <a-card size="small" class="space-item-card" @dblclick="handleGoToSpaceDataList(item)">
                <div class="space-item-top">
                  <a-checkbox
                    :checked="selectedRowKeys.includes(item.id)"
                    @change="(e) => handleGridSelect(item.id, e.target.checked)"
                  />
                  <a-dropdown :trigger="['click']">
                    <a class="space-more-trigger" @click="(e) => e.preventDefault()">
                      <a-icon type="ellipsis" />
                    </a>
                    <a-menu slot="overlay" @click="({ key }) => handleCardAction(item, key)">
                      <a-menu-item key="edit">编辑</a-menu-item>
                      <a-menu-item key="delete">删除</a-menu-item>
                    </a-menu>
                  </a-dropdown>
                </div>
                <div class="space-item-title-row">
                  <div class="space-item-title">{{ item.spaceName || '-' }}</div>
                  <a-tag :color="getSpaceTypeTagColor(item.spaceType)">{{ item.spaceType || '未分类' }}</a-tag>
                </div>
                <div class="space-item-id">ID: {{ item.spaceCode || item.id || '-' }}</div>
                <div class="space-item-capacity-wrap">
                  <div class="space-item-capacity-label">容量使用</div>
                  <div class="space-item-capacity-text">{{ getCapacityInfo(item).usedLabel }} / {{ getCapacityInfo(item).totalLabel }}</div>
                </div>
                <div class="space-cylinder-track">
                  <div class="space-cylinder-fill" :style="{ width: getCapacityInfo(item).percent + '%' }"></div>
                </div>
                <div class="space-item-meta-row">
                  <span class="space-user-icon" @click.stop="handleGoToSpaceUser(item)"><a-icon type="team" /> {{ Number(item.userCount) || 0 }} 成员</span>
                  <span><a-icon type="calendar" /> {{ formatDate(item.createTime) }}</span>
                </div>
                <div class="space-item-creator">
                  创建用户：{{ item.createBy || '-' }}
                </div>
              </a-card>
            </a-col>
          </a-row>
          <div class="grid-pagination-wrap">
            <a-pagination
              size="small"
              :current="ipagination.current"
              :pageSize="ipagination.pageSize"
              :total="gridPaginationTotal"
              :showQuickJumper="ipagination.showQuickJumper"
              :showSizeChanger="ipagination.showSizeChanger"
              :pageSizeOptions="ipagination.pageSizeOptions"
              @change="handleGridPageChange"
              @showSizeChange="handleGridPageSizeChange"
            />
          </div>
        </template>

        <a-table
          v-else
          ref="table"
          size="middle"
          :scroll="{x:true}"
          bordered
          rowKey="id"
          :columns="columns"
          :dataSource="filteredDataSource"
          :pagination="ipagination"
          :loading="loading"
          :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
          class="j-table-force-nowrap"
          @change="handleTableChange"
        >
          <span slot="action" slot-scope="text, record">
            <a @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                <a>删除</a>
            </a-popconfirm>
          </span>
        </a-table>
      </a-card>
    </a-col>

    <space-modal ref="modalForm" @ok="modalFormOk"></space-modal>
  </a-row>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import { mixinDevice } from '@/utils/mixin'
  import {JeecgListNoLoadDataMixin} from "@/mixins/JeecgListNoLoadDataMixin";
  import { getAction } from '@/api/manage'
  import SpaceModal from './modules/SpaceModal'
  import { queryDepartTreeList, searchByKeywords } from '@/api/api'

  export default {
    name: 'SpaceList',
    mixins:[JeecgListNoLoadDataMixin, mixinDevice],
    components: {
      SpaceModal
    },
    data () {
      return {
        description: '数据空间管理页面',
        autoExpandParent: true,
        expandedDepartKeys: [],
        departTree: [],
        selectedDepartKeys: [],
        currSelected: {},
        departId: '',
        viewMode: 'grid',
        filters: {
          type: 'all',
          status: 'all',
          quickFilter: ''
        },
        // 表头
        columns: [
          {
            title:'空间名称',
            align:"center",
            dataIndex: 'spaceName'
          },
          {
            title:'空间类型',
            align:"center",
            dataIndex: 'spaceType'
          },
          {
            title:'空间容量',
            align:"center",
            dataIndex: 'spaceCapacity'
          },
          // {
          //   title:'成员数',
          //   align:"center",
          //   dataIndex: 'userCount'
          // },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            fixed:"right",
            width:147,
            scopedSlots: { customRender: 'action' }
          }
        ],
        url: {
          list: "/space/list",
          delete: "/space/delete",
          deleteBatch: "/space/deleteBatch",
          exportXlsUrl: "/space/exportXls",
          importExcelUrl: "space/importExcel",
          
        },
        dictOptions:{},
        superFieldList:[],
      }
    },
    created() {
    this.getSuperFieldList();
    this.loadDepartTree();
    },
    mounted() {
      // 检查是否需要刷新空间列表（从SpaceUser页面返回）
      if (sessionStorage.getItem('needRefreshSpaceList') === 'true') {
        sessionStorage.removeItem('needRefreshSpaceList')
        this.loadData()
      }
      // 从SpaceUser页面返回后，恢复之前选择的部门
      this.restoreLastSelectedDepart()
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
      filteredDataSource() {
        return (this.dataSource || []).filter((item) => {
          const typeMatched = this.filters.type === 'all' || this.matchSpaceType(item.spaceType, this.filters.type)
          const statusMatched = this.filters.status === 'all' || this.matchSpaceStatus(item, this.filters.status)
          const quickMatched = !this.filters.quickFilter || this.matchQuickFilter(item, this.filters.quickFilter)
          return typeMatched && statusMatched && quickMatched
        })
      },
      gridPaginationTotal() {
        const filtered = this.filteredDataSource || []
        const total = Number(this.ipagination && this.ipagination.total ? this.ipagination.total : 0)
        return total === 0 ? filtered.length : total
      },
      gridDataSource() {
        const filtered = this.filteredDataSource || []
        const pageSize = Number(this.ipagination && this.ipagination.pageSize ? this.ipagination.pageSize : 10)
        const current = Number(this.ipagination && this.ipagination.current ? this.ipagination.current : 1)
        const total = Number(this.ipagination && this.ipagination.total ? this.ipagination.total : 0)

        // 如果当前分页总数为 0 或等于当前筛选后的总长度，说明当前数据是“前端全量数据”，需要卡片自行切片分页
        const shouldClientSlice = total === 0 || total === filtered.length
        if (!shouldClientSlice || filtered.length <= pageSize) {
          return filtered
        }
        const startIndex = (current - 1) * pageSize
        const endIndex = startIndex + pageSize
        return filtered.slice(startIndex, endIndex)
      }
    },
    methods: {
      initDictConfig(){
      },
      loadDepartTree() {
        queryDepartTreeList().then((res) => {
          if (res.success) {
            this.departTree = res.result || []
            this.expandedDepartKeys = this.collectExpandedKeys(this.departTree)
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      restoreLastSelectedDepart() {
        const lastDepartId = sessionStorage.getItem('lastSelectedDepartId')
        if (lastDepartId) {
          this.selectedDepartKeys = [lastDepartId]
          const lastDepartName = sessionStorage.getItem('lastSelectedDepartName') || ''
          this.currSelected = { id: lastDepartId, title: lastDepartName, departName: lastDepartName }
          this.selectTreeNode(lastDepartId)
          sessionStorage.removeItem('lastSelectedDepartId')
          sessionStorage.removeItem('lastSelectedDepartName')
        }
      },
      collectExpandedKeys(tree) {
        const keys = []
        const walk = (nodes = []) => {
          nodes.forEach((node) => {
            if (node && node.key) {
              keys.push(node.key)
            }
            if (node && node.children && node.children.length > 0) {
              walk(node.children)
            }
          })
        }
        walk(tree)
        return keys
      },
      onExpandDepart(expandedKeys) {
        this.expandedDepartKeys = expandedKeys
        this.autoExpandParent = false
      },
      onSearchDepart(value) {
        if (!value) {
          this.loadDepartTree()
          return
        }
        searchByKeywords({ keyWord: value }).then((res) => {
          if (res.success) {
            this.departTree = res.result || []
            this.expandedDepartKeys = this.collectExpandedKeys(this.departTree)
          } else {
            this.$message.warning(res.message)
          }
        })
      },
      onSelectDepart(selectedKeys, event) {
        this.selectedDepartKeys = selectedKeys
        const record = (event && event.node && event.node.dataRef) || {}
        this.currSelected = record
        const selectedDepartId = (record && (record.id || record.key)) || (selectedKeys && selectedKeys.length ? selectedKeys[0] : '')
        this.selectTreeNode(selectedDepartId)
        this.queryParam.departName = record.departName || record.title || ''
      },
      onClearDepartSelected() {
        this.currSelected = {}
        this.selectedDepartKeys = []
        this.departId = ''
        this.queryParam.departId = ''
        this.queryParam.departName = ''
        this.dataSource = []
        this.onClearSelected()
      },
      selectTreeNode(depart){
        this.departId = (depart && depart.id) || depart || ''
        this.queryParam.departId = this.departId
        this.loadSpaceData()
      },
      loadSpaceData() {
        this.onClearSelected()
        this.loading = true
        if (this.departId !== null && this.departId !== '' && this.departId !== undefined) {
          getAction(this.url.list, { departId: this.departId }).then((res) => {
            if (res.success) {
              this.dataSource = res.result.records || res.result
            } else {
              this.$message.warning(res.message)
            }
          }).finally(() => {
            this.loading = false
          })
        } else {
          this.loading = false
        }
      },
      handleAdd: function () {
        if (!this.departId) {
          this.$message.warning('请选择一个部门')
          return
        }
        this.$refs.modalForm.departId = this.departId
        this.$refs.modalForm.title = "新增"
        this.$refs.modalForm.disableSubmit = false
        this.$refs.modalForm.add()
      },
      onFilterChange() {
        this.onClearSelected()
      },
      toggleQuickFilter(type) {
        this.filters.quickFilter = this.filters.quickFilter === type ? '' : type
        this.onFilterChange()
      },
      matchSpaceType(spaceType, targetType) {
        if (!spaceType) {
          return false
        }
        const text = String(spaceType).toLowerCase()
        const map = {
          private: ['private', '私有'],
          public: ['public', '公共'],
          project: ['project', '项目'],
          department: ['department', '部门']
        }
        return (map[targetType] || []).some((item) => text.includes(item))
      },
      getSpaceTypeTagColor(spaceType) {
        if (!spaceType) {
          return 'default'
        }
        const text = String(spaceType).toLowerCase()
        if (text.includes('公共') || text.includes('public')) {
          return 'blue'
        }
        if (text.includes('私有') || text.includes('private')) {
          return 'purple'
        }
        if (text.includes('项目') || text.includes('project')) {
          return 'green'
        }
        return 'default'
      },
      matchSpaceStatus(item, targetStatus) {
        const used = Number(item && item.spaceCapacity) || 0
        const userCount = Number(item && item.userCount) || 0
        if (targetStatus === 'full') {
          return used >= 100 || userCount >= 100
        }
        if (targetStatus === 'expiring') {
          return (used >= 80 && used < 100) || (userCount >= 80 && userCount < 100)
        }
        return used < 80 && userCount < 80
      },
      matchQuickFilter(item, quickFilter) {
        if (quickFilter === 'favorite') {
          return !!item.isFavorite
        }
        if (quickFilter === 'managed') {
          return !!item.departId
        }
        if (quickFilter === 'participated') {
          return Number(item.userCount) > 1
        }
        return true
      },
      handleGridSelect(id, checked) {
        if (checked) {
          if (!this.selectedRowKeys.includes(id)) {
            this.selectedRowKeys = [...this.selectedRowKeys, id]
          }
        } else {
          this.selectedRowKeys = this.selectedRowKeys.filter((key) => key !== id)
        }
      },
      handleGridPageChange(page, pageSize) {
        const filteredLength = (this.filteredDataSource || []).length
        const total = Number(this.ipagination && this.ipagination.total ? this.ipagination.total : 0)

        this.ipagination.current = page
        this.ipagination.pageSize = pageSize

        // 前端全量数据切片分页时，不需要重新请求；后端分页时需要触发 loadData
        const shouldClientSlice = total === 0 || total === filteredLength
        if (!shouldClientSlice) {
          this.loadData()
        }
      },
      handleGridPageSizeChange(current, size) {
        const filteredLength = (this.filteredDataSource || []).length
        const total = Number(this.ipagination && this.ipagination.total ? this.ipagination.total : 0)

        this.ipagination.current = current
        this.ipagination.pageSize = size

        const shouldClientSlice = total === 0 || total === filteredLength
        if (!shouldClientSlice) {
          this.loadData()
        }
      },
      handleGoToSpaceUser(item) {
        // 保存当前选择的部门信息到sessionStorage
        if (this.selectedDepartKeys && this.selectedDepartKeys.length > 0) {
          sessionStorage.setItem('lastSelectedDepartId', this.selectedDepartKeys[0])
          sessionStorage.setItem('lastSelectedDepartName', this.currSelected.departName || this.currSelected.title || '')
        }
        this.$router.push({
          path: '/space/spaceUser',
          query: {
            spaceId: item.id,
            spaceName: item.spaceName,
            spaceType: item.spaceType,
            departId: item.departId,
            departName: item.departName
          }
        })
      },
      handleGoToSpaceDataList(item) {
        this.$router.push({
          path: '/SpaceDataList',
          query: {
            spaceId: item.id,
            spaceName: item.spaceName,
            spaceType: item.spaceType,
            departId: this.currSelected.id || this.currSelected.key || this.departId || item.departId,
            departName: this.currSelected.title || this.departName || item.departName || '',
            createBy: item.createBy
          }
        })
      },
      handleCardAction(item, action) {
        if (action === 'edit') {
          this.handleEdit(item)
          return
        }
        if (action === 'delete') {
          this.$confirm({
            title: '确定删除吗?',
            content: `确认删除空间「${item.spaceName || '-'}」吗？`,
            centered: true,
            onOk: () => this.handleDelete(item.id)
          })
        }
      },
      getCapacityInfo(item) {
        const defaultValue = {
          used: 0,
          total: 100,
          percent: 0,
          usedLabel: '0GB',
          totalLabel: '100GB'
        }
        if (!item) {
          return defaultValue
        }
        const rawCapacity = item.spaceCapacity
        let used = Number(item.usedCapacity || item.spaceUsed || item.capacityUsed || 0)
        let total = Number(item.totalCapacity || item.spaceTotal || item.capacityTotal || 0)

        if ((!used || !total) && typeof rawCapacity === 'string' && rawCapacity.includes('/')) {
          const [rawUsed, rawTotal] = rawCapacity.split('/')
          used = Number(String(rawUsed).replace(/[^\d.]/g, '')) || used
          total = Number(String(rawTotal).replace(/[^\d.]/g, '')) || total
        }

        if (!used && !total) {
          const singleCapacity = Number(String(rawCapacity || '').replace(/[^\d.]/g, ''))
          if (singleCapacity > 0) {
            used = singleCapacity
            total = Math.max(singleCapacity, 100)
          }
        }
        if (used < 0) {
          used = 0
        }
        if (total <= 0) {
          total = 100
        }
        if (used > total) {
          total = used
        }
        const percent = Math.max(0, Math.min(100, Number(((used / total) * 100).toFixed(2))))
        return {
          used,
          total,
          percent,
          usedLabel: `${used}GB`,
          totalLabel: `${total}GB`
        }
      },
      formatDate(dateValue) {
        if (!dateValue) {
          return '-'
        }
        const date = new Date(dateValue)
        if (Number.isNaN(date.getTime())) {
          return '-'
        }
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'departId',text:'部门ID',dictCode:''})
        fieldList.push({type:'string',value:'departName',text:'部门名称',dictCode:''})
        fieldList.push({type:'string',value:'spaceName',text:'空间名称',dictCode:''})
        fieldList.push({type:'string',value:'spaceType',text:'空间类型',dictCode:''})
        fieldList.push({type:'string',value:'spaceCapacity',text:'空间容量',dictCode:''})
        fieldList.push({type:'string',value:'userCount',text:'成员数',dictCode:''})
        this.superFieldList = fieldList
      },





    }
  }
</script>
<style scoped>
  /* 视口内铺满：减去顶栏/页签等区域，可按实际布局微调减数 */
  .space-layout-row {
    display: flex;
    align-items: stretch;
    min-height: calc(100vh - 148px);
  }

  .space-left-col,
  .space-right-col {
    display: flex;
    flex-direction: column;
    min-height: 0;
  }

  .space-side-card {
    width: 100%;
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
  }

  .space-side-card >>> .ant-card-body {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
  }

  .space-tree-panel {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 0;
  }

  .space-tree-scroll {
    margin-top: 10px;
    flex: 1;
    min-height: 200px;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 8px;
  }

  /* 框选（视图切换）整体与「新增」同外框长宽；内部两格均分 */
  .space-header-actions {
    --space-action-w: 96px;
    --space-action-h: 32px;
  }

  .space-action-rect {
    width: var(--space-action-w);
    height: var(--space-action-h);
    box-sizing: border-box;
    flex-shrink: 0;
  }

  /* class 与 ant-radio-group 在同一根节点上，直接作为横向 flex 容器 */
  .space-view-toggle {
    width: 100%;
    height: 100%;
    display: flex !important;
    flex-direction: row;
    align-items: stretch;
  }

  .space-view-toggle >>> .ant-radio-button-wrapper {
    flex: 1;
    min-width: 0;
    height: var(--space-action-h);
    line-height: calc(var(--space-action-h) - 2px);
    padding: 0 4px;
    box-sizing: border-box;
    display: inline-flex !important;
    align-items: center;
    justify-content: center;
  }

  .space-view-toggle >>> .ant-radio-button-wrapper .anticon {
    font-size: 16px;
  }

  .space-add-btn {
    height: 100%;
    padding: 0 8px;
  }

  .space-filter-bar {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 16px;
  }

  .space-filter-label {
    color: #595959;
  }

  .space-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    gap: 8px;
  }

  .space-header-left {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .space-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
  }

  .space-count {
    color: #8c8c8c;
    font-weight: 400;
  }

  .space-selected-tip {
    color: #595959;
  }

  .space-header-actions {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
    justify-content: flex-end;
  }

  .space-item-card {
    min-height: 250px;
    border-radius: 14px;
  }

  .space-item-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
  }

  .space-more-trigger {
    color: rgba(0, 0, 0, 0.65);
    font-size: 20px;
    line-height: 1;
  }

  .space-item-title-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }

  .space-item-title {
    font-weight: 600;
    font-size: 16px;
    max-width: calc(100% - 90px);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .space-item-id {
    color: #8c8c8c;
    margin-bottom: 8px;
    font-size: 13px;
  }

  .space-item-capacity-wrap {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    color: #262626;
  }

  .space-item-capacity-label {
    font-size: 16px;
  }

  .space-item-capacity-text {
    font-size: 16px;
    font-weight: 500;
  }

  .space-cylinder-track {
    width: 100%;
    height: 12px;
    border-radius: 999px;
    background: #e9e9e9;
    overflow: hidden;
    margin-bottom: 10px;
  }

  .space-cylinder-fill {
    height: 100%;
    border-radius: 999px;
    background: #09062c;
    transition: width 0.3s ease;
  }

  .space-item-meta-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #595959;
    padding-top: 6px;
    border-top: 1px solid #f0f0f0;
  }

  .space-user-icon {
    cursor: pointer;
    transition: color 0.3s;
  }

  .space-user-icon:hover {
    color: #1890ff;
  }

  .space-item-creator {
    margin-top: 10px;
    color: #8c8c8c;
  }

  .grid-pagination-wrap {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }

  @media (max-width: 991px) {
    .space-layout-row {
      display: block;
    }
  }

  @import '~@assets/less/common.less';
</style>