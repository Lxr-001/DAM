<template>
  <div class="space-data-detail">
    <div class="detail-header-bar">
      <div class="header-left">
        <a-button icon="arrow-left" @click="handleBack">返回</a-button>
        <span class="header-title">{{ assetDetail.assetName }}</span>
      </div>
      <div class="header-actions">
        <a-button icon="edit" @click="handleEdit">编辑</a-button>
      </div>
    </div>

    <a-card :bordered="false" class="detail-card">
      <!-- 基本信息 -->
      <div class="info-section">
        <div class="section-title">基本信息</div>
        <div class="info-grid">
          <div class="info-row">
            <div class="info-col">
              <span class="label">资产名称：</span>
              <span class="value">{{ assetDetail.assetName }}</span>
            </div>
            <div class="info-col">
              <span class="label">资产类型：</span>
              <a-tag :color="getAssetTypeColor(assetDetail.assetType)">{{ getAssetTypeText(assetDetail.assetType) }}</a-tag>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="label">数据密级：</span>
              <a-tag :color="getLevelColor(assetDetail.dataLevel)">{{ assetDetail.dataLevel }}</a-tag>
            </div>
            <div class="info-col">
              <span class="label">提供者：</span>
              <span class="value">{{ assetDetail.provider }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="label">数据Owner：</span>
              <span class="value">{{ assetDetail.ownerName }}</span>
            </div>
            <div class="info-col">
              <span class="label">所属部门：</span>
              <span class="value">{{ assetDetail.belongDept }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="label">更新时间：</span>
              <span class="value">{{ formatDate(assetDetail.updateTime) }}</span>
            </div>
            <div class="info-col">
              <span class="label">登记状态：</span>
              <a-tag :color="getStatusColor(assetDetail.status)">{{ getStatusText(assetDetail.status) }}</a-tag>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col full">
              <span class="label">资产描述：</span>
              <span class="value">{{ assetDetail.assetDesc || '暂无描述' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据集特有信息 -->
      <div v-if="assetDetail.assetType === 'dataset'" class="info-section">
        <div class="section-title">数据集信息</div>
        <div class="info-grid">
          <div class="info-row">
            <div class="info-col">
              <span class="label">数据结构类型：</span>
              <span class="value">{{ assetDetail.dataStructure === 'structured' ? '结构化数据' : '非结构化数据' }}</span>
            </div>
            <div class="info-col">
              <span class="label">更新频率：</span>
              <span class="value">{{ getUpdateFrequencyText(assetDetail.updateFrequency) }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="label">数据规模：</span>
              <span class="value">{{ assetDetail.dataScale || '-' }} 条</span>
            </div>
            <div class="info-col">
              <span class="label">数据体量：</span>
              <span class="value">{{ assetDetail.dataVolume }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="label">应用场景：</span>
              <span class="value">{{ assetDetail.applicationScenario }}</span>
            </div>
            <div class="info-col">
              <span class="label">细分场景：</span>
              <span class="value">{{ assetDetail.subScenario || '-' }}</span>
            </div>
          </div>
          <div v-if="assetDetail.techSystem" class="info-row">
            <div class="info-col full">
              <span class="label">技术体系：</span>
              <span class="value">{{ assetDetail.techSystem }}</span>
            </div>
          </div>
          <div v-if="assetDetail.timeRangeStart" class="info-row">
            <div class="info-col full">
              <span class="label">时间范围：</span>
              <span class="value">{{ assetDetail.timeRangeStart }} 至 {{ assetDetail.timeRangeEnd }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- API特有信息 -->
      <div v-if="assetDetail.assetType === 'api'" class="info-section">
        <div class="section-title">API信息</div>
        <div class="info-grid">
          <div class="info-row">
            <div class="info-col">
              <span class="label">开放程度：</span>
              <span class="value">{{ getOpenDegreeText(assetDetail.openDegree) }}</span>
            </div>
            <div class="info-col">
              <span class="label">操作类型：</span>
              <span class="value">{{ getOperationTypeText(assetDetail.operationType) }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="label">数据粒度：</span>
              <span class="value">{{ getDataGranularityText(assetDetail.dataGranularity) }}</span>
            </div>
            <div class="info-col">
              <span class="label">请求方式：</span>
              <a-tag>{{ assetDetail.apiMethod }}</a-tag>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col full">
              <span class="label">调用地址：</span>
              <span class="value">{{ assetDetail.apiAddress }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col full">
              <span class="label">返回格式：</span>
              <span class="value">{{ assetDetail.apiReturnType }}</span>
            </div>
          </div>
          <div v-if="assetDetail.apiRequestExample" class="info-row">
            <div class="info-col full">
              <span class="label">请求示例：</span>
              <pre class="code-block">{{ assetDetail.apiRequestExample }}</pre>
            </div>
          </div>
          <div v-if="assetDetail.apiSuccessExample" class="info-row">
            <div class="info-col full">
              <span class="label">成功返回示例：</span>
              <pre class="code-block">{{ assetDetail.apiSuccessExample }}</pre>
            </div>
          </div>
        </div>
      </div>

      <!-- 训练集特有信息 -->
      <div v-if="assetDetail.assetType === 'training_set'" class="info-section">
        <div class="section-title">训练集信息</div>
        <div class="info-grid">
          <div class="info-row">
            <div class="info-col">
              <span class="label">数据格式：</span>
              <span class="value">{{ assetDetail.dataFormat }}</span>
            </div>
            <div class="info-col">
              <span class="label">数据模态：</span>
              <span class="value">{{ getDataModalityText(assetDetail.dataModality) }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="label">标注方式：</span>
              <span class="value">{{ getAnnotationTypeText(assetDetail.annotationType) }}</span>
            </div>
            <div class="info-col">
              <span class="label">训练阶段：</span>
              <span class="value">{{ getTrainingStageText(assetDetail.trainingStage) }}</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="label">数据来源：</span>
              <span class="value">{{ getDataSourceText(assetDetail.dataSource) }}</span>
            </div>
            <div class="info-col">
              <span class="label">数据体量：</span>
              <span class="value">{{ assetDetail.dataVolume }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 证书信息 -->
      <div v-if="assetDetail.certPath" class="info-section">
        <div class="section-title">数据资产证书</div>
        <div class="cert-display">
          <img :src="assetDetail.certPath" alt="数据资产证书" />
          <div class="cert-info">
            <p>证书编号：{{ assetDetail.certNo }}</p>
            <p>登记日期：{{ formatDate(assetDetail.createTime) }}</p>
          </div>
        </div>
      </div>
    </a-card>

    <a-back-top />
  </div>
</template>

<script>
import { getSpaceAssetById } from '@/api/spaceAsset'

export default {
  name: 'SpaceDataDetail',
  data() {
    return {
      assetDetail: {}
    }
  },
  created() {
    this.loadAssetDetail()
  },
  methods: {
    loadAssetDetail() {
      const assetId = this.$route.query.assetId
      if (!assetId) return
      getSpaceAssetById(assetId).then(res => {
        if (res.success) {
          this.assetDetail = res.result
        } else {
          this.$message.warning(res.message || '加载失败')
        }
      })
    },
    handleBack() {
      // 返回数据空间管理页面时，记忆当前部门选择
      const query = this.$route.query
      if (query.departId) {
        sessionStorage.setItem('lastSelectedDepartId', query.departId)
        sessionStorage.setItem('lastSelectedDepartName', query.departName || '')
      }
      this.$router.push({ path: '/space/spaceList' })
    },
    handleEdit() {
      this.$message.info('编辑功能开发中')
    },
    formatDate(dateValue) {
      if (!dateValue) return '-'
      const date = new Date(dateValue)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },
    getAssetTypeText(type) {
      const map = { dataset: '数据集', api: '数据API', training_set: '高质量训练集' }
      return map[type] || type
    },
    getAssetTypeColor(type) {
      const map = { dataset: 'blue', api: 'purple', training_set: 'orange' }
      return map[type] || 'default'
    },
    getLevelColor(level) {
      const map = { '公开': 'green', '内部': 'blue', '秘密': 'orange', '机密': 'red', '绝密': 'purple' }
      return map[level] || 'default'
    },
    getStatusColor(status) {
      const map = { draft: 'default', submitted: 'blue', approved: 'green', rejected: 'red', cancelled: 'gray' }
      return map[status] || 'default'
    },
    getStatusText(status) {
      const map = { draft: '草稿', submitted: '待审批', approved: '已审批', rejected: '已驳回', cancelled: '已注销' }
      return map[status] || status
    },
    getUpdateFrequencyText(freq) {
      const map = { daily: '按天更新', weekly: '按周更新', monthly: '按月更新', yearly: '按年更新', ondemand: '按需更新', never: '不更新' }
      return map[freq] || freq
    },
    getOpenDegreeText(degree) {
      const map = { open: '开放API', internal: '内部API', private: '私有API' }
      return map[degree] || degree
    },
    getOperationTypeText(type) {
      const map = { read_only: '只读API', write_only: '只写API', read_write: '读写API' }
      return map[type] || type
    },
    getDataGranularityText(granularity) {
      const map = { detail: '明细API', summary: '汇总API', wide_table: '宽表API', theme_domain: '主题域API' }
      return map[granularity] || granularity
    },
    getDataModalityText(modality) {
      const map = { text: '文本数据', image: '图像数据', audio: '音频数据', video: '视频数据', multimodal: '多模态数据' }
      return map[modality] || modality
    },
    getAnnotationTypeText(type) {
      const map = { supervised: '有监督标注数据', unsupervised: '无监督原始数据', weakly: '弱监督数据', semi: '半监督数据', self: '自监督数据' }
      return map[type] || type
    },
    getTrainingStageText(stage) {
      const map = { pretrain: '预训练数据集', finetune: '微调数据集', validation: '验证集', test: '测试集' }
      return map[stage] || stage
    },
    getDataSourceText(source) {
      const map = { business: '业务部门', opensource: '公开开源', crawler: '爬虫互联网数据', authorized: '合作方授权数据', synthetic: '人工合成数据' }
      return map[source] || source
    }
  }
}
</script>

<style scoped>
.space-data-detail {
  padding: 16px;
  background: #f5f7fa;
  min-height: 100vh;
}

.detail-header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
}

.detail-card {
  border-radius: 8px;
}

.info-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  gap: 16px;
}

.info-col {
  flex: 1;
  display: flex;
  align-items: flex-start;
}

.info-col.full {
  flex: 1;
}

.label {
  font-weight: 500;
  color: #595959;
  min-width: 100px;
}

.value {
  color: #262626;
}

.code-block {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  width: 100%;
  overflow-x: auto;
  white-space: pre-wrap;
}

.cert-display {
  text-align: center;
}

.cert-display img {
  max-width: 100%;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.cert-info {
  margin-top: 12px;
  color: #595959;
}
</style>
