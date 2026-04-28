<template>
  <a-row :gutter="12" class="space-data-layout">
    <!-- 左侧：数据类型侧边导航栏 -->
    <a-col :span="4" class="left-sidebar">
      <!-- 返回按钮 -->
      <div class="back-link">
        <a @click="handleBack"><a-icon type="left" /> 返回数据空间管理</a>
      </div>

      <!-- 空间信息卡片 -->
      <a-card :bordered="false" class="sidebar-card sidebar-card-40">
        <div class="space-info-section">
          <div class="section-label">空间信息</div>
          <div class="space-info-title-row">
            <span class="space-info-title">{{ spaceInfo.spaceName }}</span>
            <a-tag :color="getSpaceTypeColor(spaceInfo.spaceType)">{{ spaceInfo.spaceType }}</a-tag>
          </div>
          <div class="space-info-meta">
            <span><a-icon type="user" /> 创建用户：{{ spaceInfo.createBy }}</span>
            <span><a-icon type="apartment" /> 所属部门：{{ spaceInfo.departName || '暂无部门' }}</span>
            <span class="user-count-link" @click="handleGoToSpaceUser">
              <a-icon type="team" /> {{ spaceUserCount || 0 }} 成员
            </span>
          </div>
        </div>
      </a-card>

      <!-- 间隔 -->
      <div class="sidebar-gap"></div>

      <!-- 导航菜单卡片 -->
      <a-card :bordered="false" class="sidebar-card sidebar-card-60">
        <div class="nav-menu">
          <div class="section-label">导航菜单</div>
          <div
            class="nav-item"
            :class="{ active: activeNav === 'dataset' }"
            @click="handleNavClick('dataset')"
          >
            <span class="nav-icon">📊</span>
            <span class="nav-text">数据集</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: activeNav === 'api' }"
            @click="handleNavClick('api')"
          >
            <span class="nav-icon">🔗</span>
            <span class="nav-text">数据 API</span>
          </div>
          <div
            class="nav-item"
            :class="{ active: activeNav === 'training_set' }"
            @click="handleNavClick('training_set')"
          >
            <span class="nav-icon">⚡</span>
            <span class="nav-text">高质量训练集</span>
          </div>
        </div>
      </a-card>
    </a-col>

    <!-- 中间：筛选条件区域 -->
    <a-col :span="6" class="middle-filter">
      <a-card :bordered="false" class="filter-card">
        <div class="section-label">筛选条件</div>

        <!-- 数据集筛选 -->
        <div v-if="activeNav === 'dataset'" class="filter-content">
          <div class="filter-group">
            <span class="filter-label">数据密级:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictDataLevel"
                :key="item.value"
                :type="selectedFilters.dataLevel === item.value ? 'primary' : 'default'"
                @click="handleTagClick('dataLevel', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>

          <div class="filter-group">
            <div class="filter-label">数据结构类型:</div>
            <div class="filter-buttons">
              <a-button
                :type="selectedFilters.dataStructure === 'structured' ? 'primary' : 'default'"
                @click="handleTagClick('dataStructure', 'structured')"
              >结构化数据</a-button>
              <a-button
                :type="selectedFilters.dataStructure === 'unstructured' ? 'primary' : 'default'"
                @click="handleTagClick('dataStructure', 'unstructured')"
              >非结构化数据</a-button>
            </div>
          </div>

          <div v-if="selectedFilters.dataStructure === 'structured'" class="filter-group">
            <div class="filter-label">应用场景:</div>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictStructuredScenario"
                :key="item"
                :type="selectedFilters.applicationScenario === item ? 'primary' : 'default'"
                @click="handleTagClick('applicationScenario', item)"
              >{{ item }}</a-button>
            </div>
          </div>

          <div v-if="selectedFilters.applicationScenario && selectedFilters.dataStructure === 'structured'" class="filter-group">
            <div class="filter-label">细分场景:</div>
            <div class="filter-buttons">
              <a-button
                v-for="item in getSubScenarios(selectedFilters.applicationScenario)"
                :key="item"
                :type="selectedFilters.subScenario === item ? 'primary' : 'default'"
                @click="handleTagClick('subScenario', item)"
              >{{ item }}</a-button>
            </div>
          </div>

          <div v-if="selectedFilters.dataStructure === 'unstructured'" class="filter-group">
            <span class="filter-label">应用场景:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictUnstructuredScenario"
                :key="item"
                :type="selectedFilters.applicationScenario === item ? 'primary' : 'default'"
                @click="handleTagClick('applicationScenario', item)"
              >{{ item }}</a-button>
            </div>
          </div>
        </div>

        <!-- 数据API筛选 -->
        <div v-if="activeNav === 'api'" class="filter-content">
          <div class="filter-group">
            <span class="filter-label">开放程度:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictOpenDegree"
                :key="item.value"
                :type="selectedFilters.openDegree === item.value ? 'primary' : 'default'"
                @click="handleTagClick('openDegree', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">操作类型:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictOperationType"
                :key="item.value"
                :type="selectedFilters.operationType === item.value ? 'primary' : 'default'"
                @click="handleTagClick('operationType', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">数据粒度:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictDataGranularity"
                :key="item.value"
                :type="selectedFilters.dataGranularity === item.value ? 'primary' : 'default'"
                @click="handleTagClick('dataGranularity', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>
        </div>

        <!-- 高质量训练集筛选 -->
        <div v-if="activeNav === 'training_set'" class="filter-content">
          <div class="filter-group">
            <span class="filter-label">数据格式:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictDataFormat"
                :key="item.value"
                :type="selectedFilters.dataFormat === item.value ? 'primary' : 'default'"
                @click="handleTagClick('dataFormat', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">数据模态:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictDataModality"
                :key="item.value"
                :type="selectedFilters.dataModality === item.value ? 'primary' : 'default'"
                @click="handleTagClick('dataModality', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">标注方式:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictAnnotationType"
                :key="item.value"
                :type="selectedFilters.annotationType === item.value ? 'primary' : 'default'"
                @click="handleTagClick('annotationType', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">训练阶段:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictTrainingStage"
                :key="item.value"
                :type="selectedFilters.trainingStage === item.value ? 'primary' : 'default'"
                @click="handleTagClick('trainingStage', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">数据来源:</span>
            <div class="filter-buttons">
              <a-button
                v-for="item in dictDataSource"
                :key="item.value"
                :type="selectedFilters.dataSource === item.value ? 'primary' : 'default'"
                @click="handleTagClick('dataSource', item.value)"
              >{{ item.label }}</a-button>
            </div>
          </div>
          <div class="filter-group">
            <span class="filter-label">数据量筛选:</span>
            <a-slider
              v-model="dataVolumeRange"
              range
              :marks="dataVolumeMarks"
              :min="0"
              :max="1000"
              :style="{ width: '85%', margin: '0 auto' }"
              @change="handleDataVolumeChange"
            />
            <div class="data-volume-display">{{ dataVolumeRange[0] }}K - {{ dataVolumeRange[1] }}T</div>
          </div>
        </div>
      </a-card>
    </a-col>

    <!-- 右侧：数据资产展示区 -->
    <a-col :span="12" class="right-content">
      <a-card :bordered="false" class="content-card">
        <!-- 顶部操作栏 -->
        <div class="content-header">
          <div class="header-top">
            <a-input-search
              v-model="searchKeyword"
              placeholder="搜索资产名称或提供者"
              style="width: 300px"
              @search="handleSearch"
            />
            <a-select v-model="sortType" style="width: 150px; margin-left: 12px" @change="handleSortChange">
              <a-select-option value="createTime_desc">创建时间降序</a-select-option>
              <a-select-option value="createTime_asc">创建时间升序</a-select-option>
              <a-select-option value="dataVolume_desc">数据量降序</a-select-option>
              <a-select-option value="dataVolume_asc">数据量升序</a-select-option>
            </a-select>
            <div class="view-toggle" style="margin-left: auto">
              <a-radio-group v-model="viewMode" button-style="solid">
                <a-radio-button value="card"><a-icon type="appstore" /></a-radio-button>
                <a-radio-button value="list"><a-icon type="unordered-list" /></a-radio-button>
              </a-radio-group>
            </div>
          </div>

          <div class="header-bottom">
            <div class="registration-buttons">
              <a-button type="primary" icon="plus" @click="handleOpenModal('initial')">首次登记</a-button>
              <a-button icon="swap" @click="handleOpenModal('transfer')">转让登记</a-button>
              <a-button icon="edit" @click="handleOpenModal('change')">变更登记</a-button>
              <a-button icon="delete" @click="handleOpenModal('cancel')">注销登记</a-button>
            </div>
          </div>
        </div>

        <!-- 卡片视图 -->
        <div v-if="viewMode === 'card'" class="asset-grid">
          <a-row :gutter="[16, 16]">
            <a-col
              v-for="item in filteredAssetList"
              :key="item.id"
              :xs="24"
              :sm="12"
              :md="12"
              :lg="12"
            >
              <a-card
                class="asset-card"
                :class="{ selected: selectedAssetId === item.id }"
                hoverable
                @click="handleCardClick(item)"
              >
                <div class="card-checkbox" @click.stop>
                  <a-checkbox
                    :checked="selectedAssetId === item.id"
                    @change="handleSelectAsset(item)"
                  />
                </div>
                <div class="card-content">
                  <div class="card-title-row">
                    <div class="card-title" :title="item.assetName" @click="handleGoToDetail(item)">
                      {{ item.assetName }}
                    </div>
                    <a-tag :color="getLevelColor(item.dataLevel)">{{ getLevelText(item.dataLevel) }}</a-tag>
                  </div>
                  <div class="card-desc">{{ item.assetDesc || '暂无描述' }}</div>
                  <div class="card-meta">
                    <span><a-icon type="user" /> {{ item.ownerName || '-' }}</span>
                    <span><a-icon type="database" /> {{ item.dataVolume || '-' }}</span>
                    <span><a-icon type="sync" /> {{ item.updateFrequency || '-' }}</span>
                  </div>
                  <div class="card-tags">
                    <a-tag color="blue" v-if="item.dataStructure === 'structured'">结构化数据</a-tag>
                    <a-tag color="blue" v-if="item.dataStructure === 'unstructured'">非结构化数据</a-tag>
                    <a-tag color="blue" v-if="item.applicationScenario">{{ item.applicationScenario }}</a-tag>
                  </div>
                  <div class="card-footer">
                    <span class="card-update-time">{{ formatDate(item.createTime) }}</span>
                  </div>
                </div>
                <div v-if="item.certPath" class="card-cert">
                  <img :src="item.certPath" alt="证书" />
                </div>
              </a-card>
            </a-col>
          </a-row>

          <div v-if="filteredAssetList.length === 0" class="empty-state">
            <a-empty description="暂无数据资产">
              <a-button type="primary" @click="handleOpenModal('initial')">去登记</a-button>
            </a-empty>
          </div>
        </div>

        <!-- 列表视图 -->
        <a-table
          v-else
          :columns="listColumns"
          :dataSource="filteredAssetList"
          :pagination="pagination"
          rowKey="id"
          :row-selection="{ selectedRowKeys: [selectedAssetId], onChange: handleRowSelectChange }"
          @change="handleTableChange"
        >
          <span slot="action" slot-scope="text, record">
            <a @click="handleGoToDetail(record)">查看详情</a>
          </span>
        </a-table>
      </a-card>
    </a-col>

    <!-- 首次登记弹窗 -->
    <initial-registration-modal
      :visible="showInitialModal"
      :space-info="spaceInfo"
      :active-nav="activeNav"
      @success="handleModalSuccess"
      @close="showInitialModal = false"
    />

    <!-- 转让登记弹窗 -->
    <transfer-registration-modal
      :visible="showTransferModal"
      :space-info="spaceInfo"
      :selected-asset="selectedAsset"
      @success="handleModalSuccess"
      @close="showTransferModal = false"
    />

    <!-- 变更登记弹窗 -->
    <change-registration-modal
      :visible="showChangeModal"
      :space-info="spaceInfo"
      :active-nav="activeNav"
      :selected-asset="selectedAsset"
      @success="handleModalSuccess"
      @close="showChangeModal = false"
    />

    <!-- 注销登记弹窗 -->
    <cancel-registration-modal
      :visible="showCancelModal"
      :space-info="spaceInfo"
      :selected-asset="selectedAsset"
      @success="handleModalSuccess"
      @close="showCancelModal = false"
    />
  </a-row>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { getSpaceAssetList } from '@/api/spaceAsset'
import { getAction } from '@/api/manage'
import InitialRegistrationModal from './modules/InitialRegistrationModal'
import TransferRegistrationModal from './modules/TransferRegistrationModal'
import ChangeRegistrationModal from './modules/ChangeRegistrationModal'
import CancelRegistrationModal from './modules/CancelRegistrationModal'

export default {
  name: 'SpaceDataList',
  components: {
    InitialRegistrationModal,
    TransferRegistrationModal,
    ChangeRegistrationModal,
    CancelRegistrationModal
  },
  mixins: [JeecgListMixin],
  data() {
    return {
      spaceInfo: {},
      spaceUserCount: 0,
      activeNav: 'dataset',
      searchKeyword: '',
      sortType: 'createTime_desc',
      viewMode: 'card',
      selectedAssetId: '',
      selectedAsset: null,
      showInitialModal: false,
      showTransferModal: false,
      showChangeModal: false,
      showCancelModal: false,
      selectedFilters: {
        dataLevel: '',
        dataStructure: '',
        applicationScenario: '',
        subScenario: '',
        techSystem: '',
        openDegree: '',
        operationType: '',
        dataGranularity: '',
        dataFormat: '',
        dataModality: '',
        annotationType: '',
        trainingStage: '',
        dataSource: ''
      },
      dataVolumeRange: [0, 1000],
      dataVolumeMarks: {
        0: '0K',
        500: '500G',
        1000: '1T'
      },
      assetList: [],
      loading: false,
      dictDataLevel: [
        { label: '公开', value: 'public' },
        { label: '内部', value: 'internal' },
        { label: '秘密', value: 'secret' },
        { label: '机密', value: 'confidential' },
        { label: '绝密', value: 'topsecret' }
      ],
      dictOpenDegree: [
        { label: '开放API', value: 'open' },
        { label: '内部API', value: 'internal' },
        { label: '私有API', value: 'private' }
      ],
      dictOperationType: [
        { label: '只读API', value: 'read_only' },
        { label: '只写API', value: 'write_only' },
        { label: '读写API', value: 'read_write' }
      ],
      dictDataGranularity: [
        { label: '明细API', value: 'detail' },
        { label: '汇总API', value: 'summary' },
        { label: '宽表API', value: 'wide_table' },
        { label: '主题域API', value: 'theme_domain' }
      ],
      dictDataFormat: [
        { label: 'json', value: 'json' },
        { label: 'csv', value: 'csv' },
        { label: 'text', value: 'text' },
        { label: 'imagefolder', value: 'imagefolder' },
        { label: 'soundfolder', value: 'soundfolder' }
      ],
      dictDataModality: [
        { label: '文本数据', value: 'text' },
        { label: '图像数据', value: 'image' },
        { label: '音频数据', value: 'audio' },
        { label: '视频数据', value: 'video' },
        { label: '多模态数据', value: 'multimodal' }
      ],
      dictAnnotationType: [
        { label: '有监督标注数据', value: 'supervised' },
        { label: '无监督原始数据', value: 'unsupervised' },
        { label: '弱监督数据', value: 'weakly' },
        { label: '半监督数据', value: 'semi' },
        { label: '自监督数据', value: 'self' }
      ],
      dictTrainingStage: [
        { label: '预训练数据集', value: 'pretrain' },
        { label: '微调数据集', value: 'finetune' },
        { label: '验证集', value: 'validation' },
        { label: '测试集', value: 'test' }
      ],
      dictDataSource: [
        { label: '业务部门', value: 'business' },
        { label: '公开开源', value: 'opensource' },
        { label: '爬虫互联网数据', value: 'crawler' },
        { label: '合作方授权数据', value: 'authorized' },
        { label: '人工合成数据', value: 'synthetic' }
      ],
      dictStructuredScenario: [
        '共享服务域', '科研生产域', '经营管理域', '试验生产域', '运行支撑域'
      ],
      dictUnstructuredScenario: [
        '规章制度', '标准规范', '科技报告', '行政公文', '新闻资讯'
      ],
      structuredSubScenarios: {
        '共享服务域': ['标准共享服务', '综合保障服务', '质量分析服务'],
        '科研生产域': ['科研协同', '软件开发'],
        '经营管理域': ['人才管理', '合同管理', '市场管理', '综合计划', '财务管理', '质量管理', '采购管理', '项目管理'],
        '试验生产域': ['元器件试验检测', '基础产品试验检测', '测试性试验检测', '环境与可靠性试验', '电磁兼容性试验检测', '软件测试评价'],
        '运行支撑域': ['CBB管理', '固定资产管理', '档案管理', '知识产权与成果管理', '知识管理', '科技情报管理', '规章制度管理', '设备管理']
      },
      techSystemTreeData: {
        '规章制度': [
          { title: '标准化', key: '标准化', children: [{ title: '标准化共性', key: '标准化-标准化共性' }, { title: '机载系统标准化', key: '标准化-机载系统标准化' }] },
          { title: '专业工程', key: '专业工程' },
          { title: '质量工程', key: '质量工程' },
          { title: '数据与软件工程', key: '数据与软件工程' },
          { title: '本质安全与适航', key: '本质安全与适航' },
          { title: '基础产品', key: '基础产品' }
        ]
      },
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0
      },
      listColumns: [
        { title: '资产名称', dataIndex: 'assetName' },
        { title: '数据类型', dataIndex: 'assetType' },
        { title: '数据密级', dataIndex: 'dataLevel' },
        { title: '提供者', dataIndex: 'provider' },
        { title: '数据体量', dataIndex: 'dataVolume' },
        { title: '状态', dataIndex: 'status' },
        { title: '操作', key: 'action', scopedSlots: { customRender: 'action' } }
      ]
    }
  },
  computed: {
    filteredAssetList() {
      return this.assetList
    }
  },
  created() {
    this.initSpaceInfo()
    this.loadAssetList()
    this.loadSpaceUserCount()
  },
  methods: {
    initSpaceInfo() {
      const query = this.$route.query
      const lastSelectedDepartName = sessionStorage.getItem('lastSelectedDepartName') || ''
      const lastSelectedDepartId = sessionStorage.getItem('lastSelectedDepartId') || ''
      this.spaceInfo = {
        id: query.spaceId || '',
        spaceName: query.spaceName || '数据空间',
        spaceType: query.spaceType || '公共',
        createBy: query.createBy || '',
        departName: query.departName || lastSelectedDepartName || '',
        departId: query.departId || lastSelectedDepartId || ''
      }
    },
    loadSpaceUserCount() {
      getAction('/spaceUser/list', { spaceId: this.spaceInfo.id }).then(res => {
        if (res.success) {
          const list = res.result.records || res.result || []
          this.spaceUserCount = list.length
        }
      })
    },
    handleGoToSpaceUser() {
      this.$router.push({
        path: '/space/spaceUser',
        query: {
          spaceId: this.spaceInfo.id,
          spaceName: this.spaceInfo.spaceName,
          spaceType: this.spaceInfo.spaceType,
          departId: this.spaceInfo.departId,
          departName: this.spaceInfo.departName
        }
      })
    },
    loadAssetList() {
      this.loading = true
      const params = {
        spaceId: this.spaceInfo.id,
        assetType: this.activeNav,
        ...this.selectedFilters
      }
      Object.keys(params).forEach(key => {
        if (!params[key]) delete params[key]
      })
      if (this.searchKeyword) {
        params.assetName = this.searchKeyword
      }
      getSpaceAssetList(params)
        .then(res => {
          if (res.success) {
            this.assetList = res.result.records || res.result || []
            this.pagination.total = res.result.total || this.assetList.length
          } else {
            this.$message.warning(res.message)
          }
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleNavClick(type) {
      this.activeNav = type
      this.resetFilters()
      this.loadAssetList()
    },
    handleTagClick(filterKey, value) {
      if (this.selectedFilters[filterKey] === value) {
        this.selectedFilters[filterKey] = ''
      } else {
        this.selectedFilters[filterKey] = value
        if (filterKey === 'dataStructure') {
          this.selectedFilters.applicationScenario = ''
          this.selectedFilters.subScenario = ''
          this.selectedFilters.techSystem = ''
        }
        if (filterKey === 'applicationScenario') {
          this.selectedFilters.subScenario = ''
          this.selectedFilters.techSystem = ''
        }
      }
    },
    handleTreeSelect(filterKey, keys) {
      this.selectedFilters[filterKey] = keys[0] || ''
    },
    handleDataVolumeChange(value) {
      this.dataVolumeRange = value
    },
    handleResetFilter() {
      this.resetFilters()
      this.loadAssetList()
    },
    resetFilters() {
      this.selectedFilters = {
        dataLevel: '',
        dataStructure: '',
        applicationScenario: '',
        subScenario: '',
        techSystem: '',
        openDegree: '',
        operationType: '',
        dataGranularity: '',
        dataFormat: '',
        dataModality: '',
        annotationType: '',
        trainingStage: '',
        dataSource: ''
      }
      this.dataVolumeRange = [0, 1000]
    },
    handleConfirmFilter() {
      this.pagination.current = 1
      this.loadAssetList()
    },
    handleSearch(value) {
      this.pagination.current = 1
      this.loadAssetList()
    },
    handleSortChange() {
      this.loadAssetList()
    },
    handleCardClick(item) {
      this.selectedAssetId = item.id
      this.selectedAsset = item
    },
    handleSelectAsset(item) {
      this.selectedAssetId = item.id
      this.selectedAsset = item
    },
    handleRowSelectChange(selectedRowKeys) {
      this.selectedAssetId = selectedRowKeys[0] || ''
      this.selectedAsset = this.assetList.find(item => item.id === this.selectedAssetId) || null
    },
    handleTableChange(pagination) {
      this.pagination.current = pagination.current
      this.loadAssetList()
    },
    handleBack() {
      // 返回时记忆当前部门选择
      if (this.spaceInfo.departId) {
        sessionStorage.setItem('lastSelectedDepartId', this.spaceInfo.departId)
        sessionStorage.setItem('lastSelectedDepartName', this.spaceInfo.departName || '')
      }
      this.$router.push({ path: '/space/spaceList' })
    },
    handleGoToDetail(item) {
      this.$router.push({
        path: '/SpaceDataDetail',
        query: {
          assetId: item.id,
          spaceId: this.spaceInfo.id,
          spaceName: this.spaceInfo.spaceName,
          departId: this.spaceInfo.departId,
          departName: this.spaceInfo.departName
        }
      })
    },
    handleOpenModal(type) {
      if (type !== 'initial' && !this.selectedAssetId) {
        this.$message.warning('请先选择一个数据资产')
        return
      }
      if (type !== 'initial' && this.selectedAssetId && this.assetList.filter(a => a.id === this.selectedAssetId).length > 1) {
        this.$message.warning('只能选择一个数据资产卡片')
        return
      }
      switch (type) {
        case 'initial':
          this.showInitialModal = true
          break
        case 'transfer':
          if (!this.selectedAsset) {
            this.$message.warning('请先选择一个数据资产')
            return
          }
          this.showTransferModal = true
          break
        case 'change':
          if (!this.selectedAsset) {
            this.$message.warning('请先选择一个数据资产')
            return
          }
          this.showChangeModal = true
          break
        case 'cancel':
          if (!this.selectedAsset) {
            this.$message.warning('请先选择一个数据资产')
            return
          }
          this.showCancelModal = true
          break
      }
    },
    handleModalSuccess() {
      this.selectedAssetId = ''
      this.selectedAsset = null
      this.showInitialModal = false
      this.showTransferModal = false
      this.showChangeModal = false
      this.showCancelModal = false
      this.loadAssetList()
    },
    getSubScenarios(scenario) {
      return this.structuredSubScenarios[scenario] || []
    },
    getTechSystemTree(scenario) {
      return this.techSystemTreeData[scenario] || []
    },
    getSpaceTypeColor(type) {
      const map = { '公共': 'blue', '私有': 'purple', '项目': 'green', '部门': 'orange' }
      return map[type] || 'default'
    },
    getLevelColor(level) {
      const map = { '公开': 'green', '内部': 'blue', '秘密': 'orange', '机密': 'red', '绝密': 'purple', 'Public': 'green', 'Internal': 'blue', 'Secret': 'orange', 'Classified': 'red', 'TopSecret': 'purple' }
      return map[level] || 'default'
    },
    getLevelText(level) {
      const map = { 'Public': '公开', 'Internal': '内部', 'Secret': '秘密', 'Classified': '机密', 'TopSecret': '绝密' }
      return map[level] || level || '-'
    },
    formatDate(dateValue) {
      if (!dateValue) return '-'
      const date = new Date(dateValue)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.space-data-layout {
  display: flex;
  min-height: calc(100vh - 148px);
  background: #f0f2f5;
  padding: 12px;
  gap: 12px;
}

.left-sidebar {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 172px);
}

.sidebar-card {
  border-radius: 8px;
  background: #fff;
  padding: 16px;
}

.sidebar-card-40 {
  height: 40%;
}

.sidebar-gap {
  height: 12px;
  flex-shrink: 0;
}

.sidebar-card-60 {
  height: 60%;
}

.back-link {
  margin-bottom: 12px;
  flex-shrink: 0;
}

.back-link a {
  color: #1890ff;
  font-size: 13px;
  cursor: pointer;
}

.back-link a:hover {
  color: #40a9ff;
}

.space-info-section {
  padding-bottom: 0;
}

.section-label {
  font-weight: 600;
  font-size: 15px;
  color: #262626;
  margin-bottom: 12px;
}

.space-info-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.space-info-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.space-info-meta {
  margin-top: 8px;
  font-size: 12px;
  color: #8c8c8c;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-count-link {
  cursor: pointer;
  color: #1890ff;
}

.user-count-link:hover {
  color: #40a9ff;
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: #262626;
  font-size: 14px;
}

.nav-item:hover {
  background: #f5f7fa;
}

.nav-item.active {
  background: #e6f7ff;
  color: #1890ff;
  font-weight: 500;
}

.nav-icon {
  font-size: 18px;
}

.nav-text {
  font-size: 14px;
}

.middle-filter {
  flex-shrink: 0;
  height: calc(100vh - 172px);
  border-left: 1px solid #f0f0f0;
  padding-left: 12px;
}

.filter-card {
  height: 100%;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #f0f0f0;
  padding: 16px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
  font-size: 15px;
  color: #262626;
}

.filter-content {
  overflow-y: auto;
  max-height: calc(100vh - 260px);
}

.filter-group {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: #4e5969;
  white-space: nowrap;
}

.filter-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-card {
  height: 100%;
  border-radius: 8px;
  background: #fff;
  flex: 1;
}

.content-header {
  margin-bottom: 16px;
}

.header-top {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.header-bottom {
  display: flex;
  align-items: center;
  margin-top: 12px;
}

.registration-buttons {
  display: flex;
  gap: 8px;
}

.asset-grid {
  margin-top: 16px;
}

.asset-card {
  position: relative;
  border-radius: 14px;
  transition: all 0.3s;
  min-height: 220px;
}

.asset-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.asset-card.selected {
  border: 2px solid #1890ff;
}

.card-checkbox {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 1;
}

.card-content {
  padding: 8px 0;
}

.card-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.card-title {
  font-weight: 600;
  font-size: 20px;
  cursor: pointer;
  color: #262626;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.card-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #595959;
  margin-bottom: 12px;
}

.card-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-tags {
  margin-bottom: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.card-update-time {
  font-size: 14px;
  color: #8c8c8c;
}

.card-cert {
  position: absolute;
  top: 8px;
  left: 8px;
  width: 40px;
  height: 40px;
  border-radius: 4px;
  overflow: hidden;
}

.card-cert img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.empty-state {
  padding: 60px 0;
  text-align: center;
}

.data-volume-display {
  text-align: center;
  font-size: 12px;
  color: #595959;
  margin-top: 16px;
}
</style>
