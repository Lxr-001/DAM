<template>
  <div class="data-demand-page">
    <!-- 顶部英雄区 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">连接供需，数创未来</h1>
        <p class="hero-subtitle">供需有效撮合，实现全面定制</p>
        <a-button type="primary" size="large" class="publish-btn">
          发布需求
          <i-icon type="right" slot="icon"></i-icon>
        </a-button>
      </div>
      <div class="hero-visual">
        <div class="visual-shapes">
          <div class="shape shape-1"></div>
          <div class="shape shape-2"></div>
          <div class="shape shape-3"></div>
          <div class="shape shape-4"></div>
          <div class="shape shape-5"></div>
          <div class="shape shape-6"></div>
        </div>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <span class="filter-label">需求类型:</span>
        <div class="filter-buttons">
          <a-button
            :type="filterType === 'all' ? 'primary' : 'default'"
            @click="filterType = 'all'"
          >
            全部
          </a-button>
          <a-button
            :type="filterType === 'resource' ? 'primary' : 'default'"
            @click="filterType = 'resource'"
          >
            数据资源
          </a-button>
          <a-button
            :type="filterType === 'product' ? 'primary' : 'default'"
            @click="filterType = 'product'"
          >
            数据产品
          </a-button>
          <a-button
            :type="filterType === 'service' ? 'primary' : 'default'"
            @click="filterType = 'service'"
          >
            数据服务
          </a-button>
        </div>
      </div>

      <div class="filter-group">
        <span class="filter-label">需求状态:</span>
        <div class="filter-buttons">
          <a-button
            :type="filterStatus === 'all' ? 'primary' : 'default'"
            @click="filterStatus = 'all'"
          >
            全部
          </a-button>
          <a-button
            :type="filterStatus === 'active' ? 'primary' : 'default'"
            @click="filterStatus = 'active'"
          >
            生效中
          </a-button>
          <a-button
            :type="filterStatus === 'finished' ? 'primary' : 'default'"
            @click="filterStatus = 'finished'"
          >
            已完成
          </a-button>
          <a-button
            :type="filterStatus === 'expired' ? 'primary' : 'default'"
            @click="filterStatus = 'expired'"
          >
            已过期
          </a-button>
        </div>
      </div>

      <div class="filter-group">
        <span class="filter-label">应用场景:</span>
        <div class="filter-buttons">
          <a-button
            :type="filterScenarios === 'shareService' ? 'primary' : 'default'"
            @click="handleScenarioChange('shareService')"
          >
            共享服务
          </a-button>
          <a-button
            :type="filterScenarios === 'research' ? 'primary' : 'default'"
            @click="handleScenarioChange('research')"
          >
            科研生产
          </a-button>
          <a-button
            :type="filterScenarios === 'management' ? 'primary' : 'default'"
            @click="handleScenarioChange('management')"
          >
            经营管理
          </a-button>
          <a-button
            :type="filterScenarios === 'testProduction' ? 'primary' : 'default'"
            @click="handleScenarioChange('testProduction')"
          >
            试验生产
          </a-button>
          <a-button
            :type="filterScenarios === 'operationSupport' ? 'primary' : 'default'"
            @click="handleScenarioChange('operationSupport')"
          >
            运行支撑
          </a-button>
        </div>
      </div>

      <!-- 二级筛选条件 - 按钮样式 -->
      <div class="filter-group" v-if="filterScenarios && secondLevelOptions.length">
        <span class="filter-label">细分场景:</span>
        <div class="second-level-buttons">
          <a-button
            v-for="option in secondLevelOptions"
            :key="option.value"
            :type="selectedSecondLevel.includes(option.value) ? 'primary' : 'default'"
            size="middle"
            @click="toggleSecondLevel(option.value)"
            class="second-level-btn"
          >
            {{ option.label }}
          </a-button>
        </div>
      </div>

      <div class="filter-actions">
        <a-button type="link" @click="resetFilters">全部重置</a-button>
      </div>
    </div>

    <!-- 需求列表 -->
    <div class="demand-list">
      <div
        v-for="item in demandList"
        :key="item.id"
        class="demand-item"
      >
        <div class="item-type-tag" :class="item.type">{{ item.typeLabel }}</div>
        <div class="item-header">
          <!-- 修改后的跳转代码 -->
          <h3 class="item-title">
            <router-link
              :to="{ name: 'DemandDetail', params: { id: item.id } }"
              class="title-link"
              v-if="item.id === 1"
            >
            {{ item.title }}
            </router-link>
            <span v-else>{{ item.title }}</span>
          </h3>
          <span class="item-status" :class="item.status">{{ item.statusLabel }}</span>
        </div>
        <p class="item-desc">{{ item.description }}</p>
        <div class="item-meta">
          <span class="meta-item">有效时间截至: {{ item.validUntil }}</span>
          <span class="meta-item">数据覆盖范围: {{ item.coverage }}</span>
          <span class="meta-item">需求预算 (元): {{ item.budget }}</span>
          <span class="meta-item">需求方名称: {{ item.requester }}</span>
          <span class="meta-item">应用场景:</span>
          <a-tag
            v-for="tag in item.scenarios"
            :key="tag"
            size="small"
            class="scene-tag"
          >{{ tag }}</a-tag>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <span class="total-count">共 {{ totalCount }} 条</span>
      <a-select v-model="pageSize" class="page-size-select" @change="handlePageSizeChange">
        <a-select-option value="10">10条/页</a-select-option>
        <a-select-option value="20">20条/页</a-select-option>
        <a-select-option value="50">50条/页</a-select-option>
      </a-select>
      <a-pagination
        :current="currentPage"
        :page-size="pageSize"
        :total="totalCount"
        :showSizeChanger="false"
        showQuickJumper
        @change="handlePageChange"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'DemandHall',
  data() {
    return {
      // 筛选状态
      filterType: 'all',
      filterStatus: 'all',
      filterScenarios: '',
      selectedSecondLevel: [],
      // 分页状态
      currentPage: 1,
      pageSize: 10,
      totalCount: 7,
      // 二级筛选选项配置
      secondLevelConfig: {
        shareService: [
          { value: 'standardShare', label: '标准共享服务' },
          { value: 'comprehensiveSupport', label: '综合保障服务' },
          { value: 'qualityAnalysis', label: '质量分析服务' }
        ],
        research: [
          { value: 'researchCollaboration', label: '科研协同' },
          { value: 'softwareDevelopment', label: '软件开发' }
        ],
        management: [
          { value: 'hrManagement', label: '人力管理' },
          { value: 'contractManagement', label: '合同管理' },
          { value: 'marketManagement', label: '市场管理' },
          { value: 'comprehensivePlan', label: '综合计划' },
          { value: 'financeManagement', label: '财务管理' },
          { value: 'qualityManagement', label: '质量管理' },
          { value: 'purchaseManagement', label: '采购管理' },
          { value: 'projectManagement', label: '项目管理' }
        ],
        testProduction: [
          { value: 'componentTest', label: '元器件试验检测' },
          { value: 'testabilityTest', label: '测试性试验检测' },
          { value: 'environmentReliabilityTest', label: '环境与可靠性试验' },
          { value: 'emcTest', label: '电磁兼容性试验检测' },
          { value: 'softwareTest', label: '软件测试评价' }
        ],
        operationSupport: [
          { value: 'cbbManagement', label: 'CBB管理' },
          { value: 'fixedAssetManagement', label: '固定资产管理' },
          { value: 'archiveManagement', label: '档案管理' },
          { value: 'ipManagement', label: '知识产权与成果管理' },
          { value: 'knowledgeManagement', label: '知识管理' },
          { value: 'researchInfoManagement', label: '科研情报管理' },
          { value: 'regulationManagement', label: '规章制度管理' },
          { value: 'equipmentManagement', label: '设备管理' }
        ]
      },
      // 需求列表
      demandList: [
        {
          id: 1,
          type: 'product',
          typeLabel: '数据产品',
          title: '地铁客流数据使用场景和需求征集',
          description: '为积极响应数据要素相关政策、促进交通行业数据要素市场发展，郑州地铁集团有限公司拟在郑州数据交易中心上架地铁…',
          status: 'expired',
          statusLabel: '已过期',
          validUntil: '2025-12-31',
          coverage: '全国',
          budget: '1-100000',
          requester: '郑州数据交易中心有限公司',
          scenarios: ['交通']
        },
        {
          id: 2,
          type: 'product',
          typeLabel: '数据产品',
          title: '面向全国征集 “三农数据应用场景及数据需求”',
          description: '河南作为农业大省，具有丰富的“三农”数据资源，为推动“三农”数据资源的高效利用和创新发展，释放数据要素价值…',
          status: 'expired',
          statusLabel: '已过期',
          validUntil: '2025-06-30',
          coverage: '全国',
          budget: '1-100000',
          requester: '郑州数据交易中心有限公司',
          scenarios: ['三农']
        },
        {
          id: 3,
          type: 'resource',
          typeLabel: '数据资源',
          title: '方城县文旅数据',
          description: '方城县文旅数据，统计全域游客数据，涵盖全域实时客流、总客流数、客流趋势变化、游客热力图分析、客流热门来源地…',
          status: 'expired',
          statusLabel: '已过期',
          validUntil: '2024-12-21',
          coverage: '河南/南阳',
          budget: '1000-40000',
          requester: '方城县旅游服务中心',
          scenarios: ['文旅']
        },
        {
          id: 4,
          type: 'product',
          typeLabel: '数据产品',
          title: '政策数据',
          description: '全国各省市发布的政策基本信息，政策解读等。',
          status: 'expired',
          statusLabel: '已过期',
          validUntil: '2024-10-20',
          coverage: '全国',
          budget: '100000-200000',
          requester: '郑州数据交易中心有限公司',
          scenarios: ['企服', '政务', '综合']
        },
        {
          id: 5,
          type: 'product',
          typeLabel: '数据产品',
          title: '医疗数据',
          description: '医保药品目录，药品、诊断、治疗等结构化字段。',
          status: 'expired',
          statusLabel: '已过期',
          validUntil: '2024-10-20',
          coverage: '全国',
          budget: '10000-100000',
          requester: '郑州数据交易中心有限公司',
          scenarios: ['医疗']
        },
        {
          id: 6,
          type: 'product',
          typeLabel: '数据产品',
          title: '绿色金融数据',
          description: '绿色建筑信息数据（星级、位置、碳排放量、碳配额、综合能耗等数据）用于绿色项目识别、绿色金融指标分析、绿色金…',
          status: 'expired',
          statusLabel: '已过期',
          validUntil: '2023-07-31',
          coverage: '全国',
          budget: '0-1000000',
          requester: '郑州数据交易中心有限公司',
          scenarios: ['金融']
        }
      ]
    }
  },
  computed: {
    secondLevelOptions() {
      return this.secondLevelConfig[this.filterScenarios] || [];
    }
  },
  methods: {
    handlePageChange(page, pageSize) {
      this.currentPage = page;
    },
    handlePageSizeChange(value) {
      this.pageSize = Number(value);
      this.currentPage = 1;
    },
    handleScenarioChange(value) {
      this.filterScenarios = value;
      this.selectedSecondLevel = [];
    },
    toggleSecondLevel(value) {
      if (this.selectedSecondLevel.includes(value)) {
        this.selectedSecondLevel = this.selectedSecondLevel.filter(item => item !== value);
      } else {
        this.selectedSecondLevel.push(value);
      }
    },
    resetFilters() {
      this.filterType = 'all';
      this.filterStatus = 'all';
      this.filterScenarios = '';
      this.selectedSecondLevel = [];
    }
  }
};
</script>

<style scoped>
/* 全局页面容器 */
.data-demand-page {
  min-height: 100vh;
  background-color: #f5f7fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  margin: 0;
  padding: 0;
}

.title-link {
  color: #165dff;
  text-decoration: none;
}
.title-link:hover {
  color: #0e48d9;
  text-decoration: underline;
}

/* 顶部英雄区 */
.hero-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40px 60px;
  background: linear-gradient(135deg, #ffffff 0%, #f0f5ff 100%);
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.hero-content {
  max-width: 500px;
  z-index: 2;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  color: #1d2129;
  margin: 0 0 8px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 16px;
  color: #4e5969;
  margin: 0 0 24px;
}

.publish-btn {
  height: 44px;
  padding: 0 24px;
  font-size: 16px;
  border-radius: 4px;
}

/* 3D 视觉元素占位 */
.hero-visual {
  position: absolute;
  right: 60px;
  top: 0;
  bottom: 0;
  width: 50%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.visual-shapes {
  position: relative;
  width: 400px;
  height: 300px;
}

.shape {
  position: absolute;
  border-radius: 4px;
  background: linear-gradient(135deg, #69b1ff 0%, #4080ff 100%);
  opacity: 0.8;
}

.shape-1 { width: 60px; height: 180px; top: 60px; right: 220px; }
.shape-2 { width: 80px; height: 120px; top: 120px; right: 140px; background: linear-gradient(135deg, #ffd666 0%, #ffa940 100%); }
.shape-3 { width: 120px; height: 160px; top: 80px; right: 40px; }
.shape-4 { width: 60px; height: 80px; top: 160px; right: 10px; background: linear-gradient(135deg, #ffd666 0%, #ffa940 100%); }
.shape-5 { width: 160px; height: 120px; top: 20px; right: 180px; }
.shape-6 {
  width: 120px;
  height: 120px;
  top: 0;
  right: 280px;
  clip-path: polygon(50% 0%, 100% 38%, 82% 100%, 18% 100%, 0% 38%);
}

/* 筛选栏 */
.filter-bar {
  background-color: #e8f3ff;
  padding: 24px 60px;
  margin: 0 60px 20px;
  border-radius: 8px;
  box-sizing: border-box;
}

.filter-group {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  color: #4e5969;
  white-space: nowrap;
}

/* 按钮组：每个按钮独立，间距 15px */
.filter-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.filter-actions {
  text-align: right;
  margin-top: 8px;
}

/* 二级筛选按钮间距也 15px */
.second-level-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.second-level-btn {
  border-radius: 4px;
}

/* 主题色 */
:deep(.ant-btn-primary) {
  background-color: #165dff;
  border-color: #165dff;
}

/* 需求列表 */
.demand-list {
  margin: 0 60px;
}

.demand-item {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
}

.item-type-tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px 4px 0 0;
  font-size: 12px;
  font-weight: 500;
  color: #ffffff;
  margin-bottom: 12px;
}

.item-type-tag.product { background-color: #ff7d00; }
.item-type-tag.resource { background-color: #ff7d00; }
.item-type-tag.service { background-color: #13c2c2; }

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d2129;
  margin: 0;
}

.item-status {
  font-size: 14px;
  color: #86909c;
  display: flex;
  align-items: center;
  gap: 4px;
}

.item-status.expired { color: #86909c; }
.item-status.active { color: #00b42a; }
.item-status.finished { color: #165dff; }

.item-desc {
  font-size: 14px;
  color: #4e5969;
  margin: 0 0 16px;
  line-height: 1.5;
}

.item-meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 24px;
  font-size: 13px;
  color: #4e5969;
}

.meta-item {
  white-space: nowrap;
}

.scene-tag {
  margin-left: 4px;
  background-color: #e8f3ff;
  color: #165dff;
  border: 1px solid #b4d8ff;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 24px 60px;
  margin-top: 12px;
  box-sizing: border-box;
}

.total-count {
  font-size: 14px;
  color: #4e5969;
}

.page-size-select {
  width: 120px;
}
</style>