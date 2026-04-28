<template>
  <a-card :bordered="false">
    <!-- 顶部英雄导航区 -->
    <div class="demand-hero-section">
      <div class="demand-hero-inner">
        <div class="demand-hero-left">
          <div class="demand-hero-title">连接供需，数创未来</div>
          <div class="demand-hero-subtitle">供需有效撮合，实现全面定制</div>
          <div class="demand-hero-actions">
            <a-button
              ref="publishDemandButton"
              @click="handleAdd"
              type="primary"
              size="large"
              class="demand-hero-publish-btn"
            >
              发布需求
            </a-button>

            <a-radio-group ref="viewModeRadioGroup" v-model="viewMode" button-style="solid">
              <a-radio-button value="grid"><a-icon type="appstore" /></a-radio-button>
              <a-radio-button value="list"><a-icon type="unordered-list" /></a-radio-button>
            </a-radio-group>
          </div>
        </div>
      </div>
    </div>

    <!-- 查询区域 -->
    <!-- <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
        </a-row>
      </a-form>
    </div> -->
    <!-- 查询区域-END -->

    <div class="demand-content-wrapper">
      <!-- 筛选导航栏 -->
      <div class="demand-filter-bar">
        <div class="demand-filter-group">
          <span class="demand-filter-label">需求类型:</span>
          <div class="demand-filter-buttons">
            <a-button :type="filterType === 'all' ? 'primary' : 'default'" @click="handleDemandTypeFilterChange('all')">
              全部
            </a-button>
            <a-button :type="filterType === '数据资源' ? 'primary' : 'default'" @click="handleDemandTypeFilterChange('数据资源')">
              数据资源
            </a-button>
            <a-button :type="filterType === '数据产品' ? 'primary' : 'default'" @click="handleDemandTypeFilterChange('数据产品')">
              数据产品
            </a-button>
            <a-button :type="filterType === '数据服务' ? 'primary' : 'default'" @click="handleDemandTypeFilterChange('数据服务')">
              数据服务
            </a-button>
          </div>
        </div>

        <div class="demand-filter-group">
          <span class="demand-filter-label">需求状态:</span>
          <div class="demand-filter-buttons">
            <a-button :type="filterStatus === 'all' ? 'primary' : 'default'" @click="handleDemandStatusFilterChange('all')">
              全部
            </a-button>
            <a-button :type="filterStatus === '生效中' ? 'primary' : 'default'" @click="handleDemandStatusFilterChange('生效中')">
              生效中
            </a-button>
            <a-button :type="filterStatus === '已完成' ? 'primary' : 'default'" @click="handleDemandStatusFilterChange('已完成')">
              已完成
            </a-button>
            <a-button :type="filterStatus === '已过期' ? 'primary' : 'default'" @click="handleDemandStatusFilterChange('已过期')">
              已过期
            </a-button>
          </div>
        </div>

        <div class="demand-filter-group" style="margin-bottom: 0;">
          <span class="demand-filter-label">应用场景:</span>
          <div class="demand-filter-buttons">
            <a-button :type="filterScenarios === 'all' ? 'primary' : 'default'" @click="handleApplicationScenarioFilterChange('all')">
              全部
            </a-button>
            <a-button :type="filterScenarios === '共享服务' ? 'primary' : 'default'" @click="handleApplicationScenarioFilterChange('共享服务')">
              共享服务
            </a-button>
            <a-button :type="filterScenarios === '科研生产' ? 'primary' : 'default'" @click="handleApplicationScenarioFilterChange('科研生产')">
              科研生产
            </a-button>
            <a-button :type="filterScenarios === '经营管理' ? 'primary' : 'default'" @click="handleApplicationScenarioFilterChange('经营管理')">
              经营管理
            </a-button>
            <a-button :type="filterScenarios === '试验生产' ? 'primary' : 'default'" @click="handleApplicationScenarioFilterChange('试验生产')">
              试验生产
            </a-button>
            <a-button :type="filterScenarios === '运行支撑' ? 'primary' : 'default'" @click="handleApplicationScenarioFilterChange('运行支撑')">
              运行支撑
            </a-button>
          </div>
        </div>

        <div class="demand-filter-actions">
          <a-button type="link" @click="resetDemandFilters">全部重置</a-button>
        </div>
      </div>

      <!-- 操作按钮区域 -->
      <div class="table-operator">
      <!-- 高级查询区域 -->
      <!-- <j-super-query :fieldList="superFieldList" ref="superQueryModal" @handleSuperQuery="handleSuperQuery"></j-super-query>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown> -->
    </div>

    <!-- table区域-begin -->
    <div>
      <div
        class="ant-alert ant-alert-info"
        style="margin-bottom: 16px;"
        v-if="viewMode !== 'grid'"
      >
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <template v-if="viewMode === 'grid'">
        <a-row :gutter="[0, 12]">
          <a-col v-for="record in dataSource" :key="record.id" :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <a-card size="small" class="demand-item-card">
              <a-tag :class="['demand-type-tag', getDemandTypeTagClass(record.demandType)]">
                {{ record.demandType || '-' }}
              </a-tag>
              <div class="demand-title-row">
                <a class="demand-title-link" @click="handleDetail(record)">
                  {{ record.demandTitile || '-' }}
                </a>
                <span class="demand-state" :class="getDemandStateTagClass(record.demandState)">
                  {{ record.demandState || '-' }}
                </span>
              </div>
              <div class="demand-desc">
                {{ ellipsisText(record.demandDescription, 100) }}
              </div>
              <div class="demand-meta-row">
                <span class="meta-item">
                  应用场景：
                  <a-tag
                    v-if="record.applicationScenario"
                    :class="['scenario-tag', getScenarioTagClass(record.applicationScenario)]"
                    size="small"
                  >
                    {{ record.applicationScenario }}
                  </a-tag>
                  <span v-else>-</span>
                </span>
                <span class="meta-item">截止日期：{{ formatDeadlineDate(record.demandDeadline) }}</span>
              </div>
            </a-card>
          </a-col>
        </a-row>
        <div class="grid-pagination-wrap">
          <a-pagination
            size="small"
            :current="ipagination.current"
            :pageSize="ipagination.pageSize"
            :total="ipagination.total"
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
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        class="j-table-force-nowrap"
        @change="handleTableChange"
      >
        <span slot="demandTypeTag" slot-scope="text, record">
          <a-tag :class="['demand-type-tag', getDemandTypeTagClass(text)]">
            {{ text || '-' }}
          </a-tag>
        </span>

        <span slot="demandStateTag" slot-scope="text, record">
          <span class="demand-state" :class="getDemandStateTagClass(text)">
            {{ text || '-' }}
          </span>
        </span>

        <span slot="applicationScenarioTag" slot-scope="text, record">
          <a-tag
            v-if="text"
            :class="['scenario-tag', getScenarioTagClass(text)]"
            size="small"
          >
            {{ text }}
          </a-tag>
          <span v-else>-</span>
        </span>

        <span slot="action" slot-scope="text, record">
          <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical" />
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
          </a-popconfirm>
        </span>

      </a-table>
    </div>

    </div>

    <demand-modal ref="modalForm" @ok="modalFormOk"></demand-modal>
  </a-card>
</template>

<script>

  import '@/assets/less/TableExpand.less'
  import * as dayjs from 'dayjs'
  import { mixinDevice } from '@/utils/mixin'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'
  import DemandModal from './modules/DemandModal'

  /**
   * 将截止日期格式化为 YYYY-MM-DD
   * @param {string|Date|number} value 原始日期
   * @returns {string}
   */
  function formatDeadlineDate (value) {
    if (value === null || value === undefined || value === '') {
      return '-'
    }
    const d = dayjs(value)
    return d.isValid() ? d.format('YYYY-MM-DD') : '-'
  }

  export default {
    name: 'DemandList',
    mixins:[JeecgListMixin, mixinDevice],
    components: {
      DemandModal
    },
    data () {
      return {
        description: '需求大厅管理页面',
        viewMode: 'grid',

        // 顶部筛选栏状态
        filterType: 'all',
        filterStatus: 'all',
        filterScenarios: 'all',

        // 表头
        columns: [
          {
            title:'需求标题',
            align:"center",
            dataIndex: 'demandTitile'
          },
          {
            title:'需求描述',
            align:"center",
            dataIndex: 'demandDescription'
          },
          {
            title:'需求类型',
            align:"center",
            dataIndex: 'demandType',
            scopedSlots: { customRender: 'demandTypeTag' }
          },
          {
            title:'需求状态',
            align:"center",
            dataIndex: 'demandState',
            scopedSlots: { customRender: 'demandStateTag' }
          },
          {
            title:'应用场景',
            align:"center",
            dataIndex: 'applicationScenario',
            scopedSlots: { customRender: 'applicationScenarioTag' }
          },
          // {
          //   title:'细分场景',
          //   align:"center",
          //   dataIndex: 'segmentedScenario'
          // },
          {
            title:'截止日期',
            align:"center",
            dataIndex: 'demandDeadline',
            customRender: (text) => formatDeadlineDate(text)
          },
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
          list: "/demand/list",
          delete: "/demand/delete",
          deleteBatch: "/demand/deleteBatch",
          exportXlsUrl: "/demand/exportXls",
          importExcelUrl: "demand/importExcel",
          
        },
        dictOptions:{},
        superFieldList:[],
      }
    },
    created() {
    this.getSuperFieldList();
    },
    computed: {
      importExcelUrl: function(){
        return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
      },
    },
    watch: {
      viewMode() {
        this.$nextTick(() => {
          this.syncViewModeGroupSize();
        });
      },
    },
    mounted() {
      this.syncViewModeGroupSize();
      this._syncViewModeGroupSizeHandler = () => {
        this.syncViewModeGroupSize();
      };
      window.addEventListener('resize', this._syncViewModeGroupSizeHandler);
    },
    beforeDestroy() {
      if (this._syncViewModeGroupSizeHandler) {
        window.removeEventListener('resize', this._syncViewModeGroupSizeHandler);
      }
    },
    methods: {
      formatDeadlineDate,
      initDictConfig(){
      },
      ellipsisText(text, maxLength) {
        if (!text) return ''
        if (text.length <= maxLength) return text
        return `${text.slice(0, maxLength)}......`
      },
      handleGridSelect(id, checked) {
        if (checked) {
          if (!this.selectedRowKeys.includes(id)) {
            this.selectedRowKeys = [...this.selectedRowKeys, id]
          }
          const row = this.dataSource.find(item => item.id === id)
          if (row && !this.selectionRows.find(item => item.id === id)) {
            this.selectionRows = [...this.selectionRows, row]
          }
          return
        }
        this.selectedRowKeys = this.selectedRowKeys.filter(key => key !== id)
        this.selectionRows = this.selectionRows.filter(item => item.id !== id)
      },
      handleGridPageChange(page, pageSize) {
        this.ipagination.current = page
        this.ipagination.pageSize = pageSize
        this.loadData()
      },
      handleGridPageSizeChange(current, size) {
        this.ipagination.current = current
        this.ipagination.pageSize = size
        this.loadData()
      },
      handleDetail(record) {
        this.$router.push({
          name: 'DemandDetail',
          query: { id: record.id }
        })
      },
      getSuperFieldList(){
        let fieldList=[];
        fieldList.push({type:'string',value:'demandTitile',text:'需求标题',dictCode:''})
        fieldList.push({type:'string',value:'demandDescription',text:'需求描述',dictCode:''})
        fieldList.push({type:'string',value:'demandType',text:'需求类型',dictCode:''})
        fieldList.push({type:'string',value:'demandState',text:'需求状态',dictCode:''})
        fieldList.push({type:'string',value:'applicationScenario',text:'应用场景',dictCode:''})
        fieldList.push({type:'string',value:'segmentedScenario',text:'细分场景',dictCode:''})
        fieldList.push({type:'datetime',value:'demandDeadline',text:'截止日期'})
        this.superFieldList = fieldList
      },
      /**
       * 让“视图模式”按钮组整体外框尺寸与“发布需求”按钮一致。
       * 同时确保按钮仍为水平排列，避免出现上下堆叠。
       */
      syncViewModeGroupSize() {
        const publishButtonComponent = this.$refs.publishDemandButton;
        const viewModeRadioGroupComponent = this.$refs.viewModeRadioGroup;

        const publishButtonEl = publishButtonComponent && publishButtonComponent.$el
          ? publishButtonComponent.$el
          : publishButtonComponent;
        const radioGroupEl = viewModeRadioGroupComponent && viewModeRadioGroupComponent.$el
          ? viewModeRadioGroupComponent.$el
          : viewModeRadioGroupComponent;

        if (!publishButtonEl || !radioGroupEl) return;

        const publishRect = publishButtonEl.getBoundingClientRect();
        const widthPx = Math.round(publishRect.width);
        const heightPx = Math.round(publishRect.height);
        if (!widthPx || !heightPx) return;

        // 同步 radio-group 外框尺寸
        radioGroupEl.style.width = `${widthPx}px`;
        radioGroupEl.style.height = `${heightPx}px`;
        radioGroupEl.style.boxSizing = 'border-box';
        radioGroupEl.style.padding = '0';
        radioGroupEl.style.margin = '0';

        // 强制为横向 flex，避免上下堆叠
        radioGroupEl.style.display = 'inline-flex';
        radioGroupEl.style.flexDirection = 'row';
        radioGroupEl.style.flexWrap = 'nowrap';
        radioGroupEl.style.alignItems = 'stretch';

        const wrappers = radioGroupEl.querySelectorAll('.ant-radio-button-wrapper');
        if (!wrappers || wrappers.length === 0) return;

        const singleWidthPx = Math.floor(widthPx / wrappers.length);

        wrappers.forEach((wrapper) => {
          wrapper.style.width = `${singleWidthPx}px`;
          wrapper.style.height = `${heightPx}px`;
          wrapper.style.lineHeight = `${heightPx}px`;
          wrapper.style.padding = '0';
          wrapper.style.boxSizing = 'border-box';
          wrapper.style.display = 'flex';
          wrapper.style.alignItems = 'center';
          wrapper.style.justifyContent = 'center';
        });
      },

      /**
       * 更新顶部筛选栏：需求类型
       * @param {string} value 取值：all | 数据资源 | 数据产品 | 数据服务
       */
      handleDemandTypeFilterChange(value) {
        this.filterType = value
        this.applyDemandFilters()
      },

      /**
       * 更新顶部筛选栏：需求状态
       * @param {string} value 取值：all | 生效中 | 已完成 | 已过期
       */
      handleDemandStatusFilterChange(value) {
        this.filterStatus = value
        this.applyDemandFilters()
      },

      /**
       * 更新顶部筛选栏：应用场景
       * @param {string} value 取值：all | 共享服务 | 科研生产 | 经营管理 | 试验生产 | 运行支撑
       */
      handleApplicationScenarioFilterChange(value) {
        this.filterScenarios = value
        this.applyDemandFilters()
      },

      /**
       * 清空顶部筛选栏并刷新列表
       */
      resetDemandFilters() {
        this.filterType = 'all'
        this.filterStatus = 'all'
        this.filterScenarios = 'all'
        this.applyDemandFilters()
      },

      /**
       * 把顶部筛选条件映射为后端查询参数，并重新加载列表
       */
      applyDemandFilters() {
        if (!this.queryParam) this.queryParam = {}

        const demandType = this.filterType === 'all' ? '' : this.filterType
        const demandState = this.filterStatus === 'all' ? '' : this.filterStatus
        const applicationScenario = this.filterScenarios === 'all' ? '' : this.filterScenarios

        // Vue2：确保字段存在时也能保持响应式
        this.$set(this.queryParam, 'demandType', demandType)
        this.$set(this.queryParam, 'demandState', demandState)
        this.$set(this.queryParam, 'applicationScenario', applicationScenario)

        // 切换筛选条件时清空勾选
        if (typeof this.onClearSelected === 'function') {
          this.onClearSelected()
        }

        this.loadData(1)
      },

      /**
       * 获取需求类型标签的样式 class
       * @param {string} demandType 需求类型展示值
       * @returns {string} CSS class
       */
      getDemandTypeTagClass(demandType) {
        if (demandType === '数据资源') return 'resource'
        if (demandType === '数据产品') return 'product'
        if (demandType === '数据服务') return 'service'
        return ''
      },

      /**
       * 获取需求状态标签的样式 class
       * @param {string} demandState 需求状态展示值
       * @returns {string} CSS class
       */
      getDemandStateTagClass(demandState) {
        if (demandState === '生效中') return 'active'
        if (demandState === '已完成') return 'finished'
        if (demandState === '已过期') return 'expired'
        return ''
      },

      /**
       * 获取应用场景标签的样式 class
       * @param {string} applicationScenario 应用场景展示值
       * @returns {string} CSS class
       */
      getScenarioTagClass(applicationScenario) {
        if (applicationScenario === '共享服务') return 'share-service'
        if (applicationScenario === '科研生产') return 'research-production'
        if (applicationScenario === '经营管理') return 'management'
        if (applicationScenario === '试验生产') return 'test-production'
        if (applicationScenario === '运行支撑') return 'operation-support'
        return ''
      }
    }
  }
</script>

<style scoped>
  @import '~@assets/less/common.less';
  /* 顶部英雄导航区 */
  .demand-hero-section {
    background-image: url('./img/DemandBackground.png');
    background-size: 100% 100%;
    background-position: center;
    background-repeat: no-repeat;
    padding: 56px 0 120px;
    min-height: 320px;
    box-sizing: border-box;
  }

  .demand-hero-inner {
    max-width: 1800px;
    margin: 0 auto;
    padding: 0 24px;
    box-sizing: border-box;
  }

  .demand-hero-left {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .demand-hero-title {
    font-size: 32px;
    font-weight: 700;
    color: #1d2129;
    margin: 0 0 8px;
    line-height: 1.2;
  }

  .demand-hero-subtitle {
    font-size: 16px;
    color: #4e5969;
    margin: 0 0 22px;
  }

  .demand-hero-actions {
    display: flex;
    align-items: center;
    gap: 16px;
    flex-wrap: wrap;
  }

  .demand-hero-publish-btn {
    height: 44px;
    padding: 0 24px;
    font-size: 16px;
    border-radius: 4px;
  }

  .demand-item-card {
    border-radius: 6px;
  }
  .demand-item-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 10px;
  }
  .demand-type-tag {
    display: inline-block;
    padding: 4px 8px;
    border-radius: 4px 4px 0 0;
    font-size: 12px;
    font-weight: 500;
    color: #ffffff;
    margin-bottom: 12px;
    border: none;
  }

  .demand-type-tag.resource,
  .demand-type-tag.product {
    background-color: #ff7d00;
    border-color: #ff7d00;
  }

  .demand-type-tag.service {
    /* 需求大厅修改要求：数据服务标签颜色改为与数据产品一致 */
    background-color: #ff7d00;
    border-color: #ff7d00;
  }
  .demand-title-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
  }
  .demand-title-link {
    max-width: calc(100% - 100px);
    display: inline-block;
    color: #1d2129;
    font-size: 18px;
    font-weight: 600;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .demand-state {
    color: #86909c;
    font-size: 14px;
    display: inline-flex;
    align-items: center;
  }

  .demand-state::before {
    content: '';
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background-color: currentColor;
    margin-right: 6px;
    display: inline-block;
  }

  .demand-state.active {
    color: #00b42a;
  }

  .demand-state.finished {
    color: #165dff;
  }

  .demand-state.expired {
    color: #ff4d4f;
  }
  .demand-desc {
    color: #4e5969;
    font-size: 14px;
    line-height: 1.5;
    margin-bottom: 16px;
  }
  .demand-meta-row {
    display: flex;
    flex-wrap: wrap;
    gap: 24px;
    margin-bottom: 10px;
    color: #4e5969;
    font-size: 13px;
  }
  .demand-actions {
    text-align: right;
  }
  .grid-pagination-wrap {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
  }

  /* 顶部筛选栏 */
  .demand-content-wrapper {
    width: 90%;
    max-width: 1800px;
    margin: 0 auto;
  }

  @media (max-width: 768px) {
    .demand-content-wrapper {
      width: 100%;
    }
  }

  .demand-filter-bar {
    /* 与设计稿一致：浅蓝系半透明导航栏底色 */
    background: linear-gradient(
      180deg,
      rgba(246, 250, 255, 0.96) 0%,
      rgba(232, 243, 255, 0.94) 100%
    );
    padding: 20px 24px;
    width: 100%;
    max-width: none;
    margin: -24px 0 16px;
    border-radius: 8px;
    box-sizing: border-box;
    border: 1px solid rgba(180, 216, 255, 0.55);
    box-shadow: 0 6px 18px rgba(17, 55, 111, 0.10);
  }

  .demand-filter-group {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
  }

  .demand-filter-label {
    font-size: 14px;
    color: #4e5969;
    white-space: nowrap;
  }

  .demand-filter-buttons {
    display: flex;
    gap: 15px;
    flex-wrap: wrap;
  }

  .demand-filter-actions {
    text-align: right;
    margin-top: 8px;
  }

  /* 应用场景标签 */
  .scenario-tag {
    border: 1px solid transparent;
    border-radius: 4px;
    margin-left: 6px;
  }

  .scenario-tag.share-service {
    background-color: #e8f3ff;
    border-color: #b4d8ff;
    color: #165dff;
  }

  .scenario-tag.research-production {
    background-color: #f5e8ff;
    border-color: #d3adff;
    color: #722ed1;
  }

  .scenario-tag.management {
    background-color: #fff5e6;
    border-color: #ffd591;
    color: #fa8c16;
  }

  .scenario-tag.test-production {
    background-color: #f6ffed;
    border-color: #c9f7a5;
    color: #52c41a;
  }

  .scenario-tag.operation-support {
    background-color: #e6fffb;
    border-color: #9be7d8;
    color: #13c2c2;
  }
</style>

