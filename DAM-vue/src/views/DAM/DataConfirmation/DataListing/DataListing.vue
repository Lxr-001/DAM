<template>
  <div class="data-listing-page">
    <!-- 顶部标题栏 -->
    <div class="header-banner">
      <h1>数据确权服务中心</h1>
      <div class="search-bar">
        <a-input-search
          placeholder="请输入数据名称/登记编号/主体名称查询"
          style="width: 500px"
          @search="onSearch"
        />
        <a-button type="primary" class="register-btn">立即登记</a-button>
      </div>
      <div class="service-tags">
        <a-tag color="blue">权威公正</a-tag>
        <a-tag color="blue">创新驱动</a-tag>
        <a-tag color="blue">服务导向</a-tag>
        <a-tag color="blue">便捷高效</a-tag>
      </div>
    </div>

    <!-- 登记流程（已细化为卡片式） -->
    <div class="section process-section">
      <h2 class="section-title">登记流程</h2>
      <div class="process-buttons">
        <a-button type="primary" class="process-btn" @click="showOperate = true">首次登记</a-button>
        <a-button type="primary" class="process-btn">许可登记</a-button>
        <a-button type="primary" class="process-btn">转让登记</a-button>
        <a-button type="primary" class="process-btn">变更登记</a-button>
        <a-button type="primary" class="process-btn">注销登记</a-button>
      </div>
      <div class="process-cards">
        <div class="process-card">
          <div class="card-icon">
            <span class="icon-document">📄</span>
            <span class="icon-badge check">✓</span>
          </div>
          <div class="card-title">登记信息填写</div>
        </div>
        <div class="process-arrow">➤</div>
        <div class="process-card">
          <div class="card-icon">
            <span class="icon-document">📄</span>
            <span class="icon-badge user">👤</span>
          </div>
          <div class="card-title">平台审核</div>
        </div>
        <div class="process-arrow">➤</div>
        <div class="process-card">
          <div class="card-icon">
            <span class="icon-folder">📁</span>
            <span class="icon-badge up">↑</span>
          </div>
          <div class="card-title">信息公示</div>
        </div>
        <div class="process-arrow">➤</div>
        <div class="process-card">
          <div class="card-icon">
            <span class="icon-document">📄</span>
            <span class="icon-badge link">🔄</span>
          </div>
          <div class="card-title">信息上链</div>
        </div>
        <div class="process-arrow">➤</div>
        <div class="process-card">
          <div class="card-icon">
            <span class="icon-document">📄</span>
            <span class="icon-badge down">↓</span>
          </div>
          <div class="card-title">证书发放</div>
        </div>
      </div>
    </div>

    <!-- 登记列表 -->
    <div class="section list-section">
      <div class="section-header">
        <h2 class="section-title">登记列表</h2>
        <a href="javascript:;" @click="$router.push('/DataListingCategory')">查看更多 ></a>
      </div>
      <div class="card-list">
        <a-row :gutter="20">
          <a-col :span="12">
            <a-card class="data-card" hoverable>
              <div class="card-content">
                <div class="card-info">
                  <h3 class="card-title" @click="$router.push('/DataListingDetail')">汉寿甲鱼养殖加工数据产品</h3>
                  <p class="card-subtitle">汉寿县现代农业投资开发有限公司</p>
                  <p class="card-item">登记编号：SZDEX-RE021769097600000001</p>
                  <p class="card-item">登记对象：数据产品</p>
                  <p class="card-item">产权类型：数据持有权、数据使用权、数据经营权</p>
                  <p class="card-item">首次登记：2026-02-07</p>
                </div>
                <div class="card-cert">
                  <img src="@/views/DAM/DataConfirmation/DataListing/img/p1.png" alt="数据产权登记证书" class="cert-img" />
                </div>
              </div>
            </a-card>
          </a-col>
          <a-col :span="12">
            <a-card class="data-card" hoverable>
              <div class="card-content">
                <div class="card-info">
                  <h3 class="card-title">商业地产物理模型标准库</h3>
                  <p class="card-subtitle">深圳泛和科技有限公司</p>
                  <p class="card-item">登记编号：SZDEX-RE021768924800000001</p>
                  <p class="card-item">登记对象：数据产品</p>
                  <p class="card-item">产权类型：数据持有权、数据使用权、数据经营权</p>
                  <p class="card-item">首次登记：2026-02-04</p>
                </div>
                <div class="card-cert">
                  <div class="cert-placeholder">证书预览</div>
                </div>
              </div>
            </a-card>
          </a-col>
          <a-col :span="12">
            <a-card class="data-card" hoverable>
              <div class="card-content">
                <div class="card-info">
                  <h3 class="card-title">新会陈皮全链路溯源数据产品</h3>
                  <p class="card-subtitle">深圳前海链藏数据科技有限公司</p>
                  <p class="card-item">登记编号：SZDEX-RE021768492800000001</p>
                  <p class="card-item">登记对象：数据产品</p>
                  <p class="card-item">产权类型：数据持有权、数据使用权、数据经营权</p>
                  <p class="card-item">首次登记：2026-01-31</p>
                </div>
                <div class="card-cert">
                  <div class="cert-placeholder">证书预览</div>
                </div>
              </div>
            </a-card>
          </a-col>
          <a-col :span="12">
            <a-card class="data-card" hoverable>
              <div class="card-content">
                <div class="card-info">
                  <h3 class="card-title">安义县铝型材产业链精准营销数据产品</h3>
                  <p class="card-subtitle">南昌职城教育投资发展有限公司</p>
                  <p class="card-item">登记编号：SZDEX-RE021768320000000001</p>
                  <p class="card-item">登记对象：数据产品</p>
                  <p class="card-item">产权类型：数据使用权、数据经营权</p>
                  <p class="card-item">首次登记：2026-01-29</p>
                </div>
                <div class="card-cert">
                  <div class="cert-placeholder">证书预览</div>
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </div>
    </div>

    <!-- 弹窗组件 -->
    <DataListingOperate :visible.sync="showOperate" />
  </div>
</template>

<script>
import DataListingOperate from '@views/DAM/DataConfirmation/DataListing/DataListingOperate.vue'

export default {
  name: 'DataListing',
  components: {
    DataListingOperate
  },
  data() {
    return {
      showOperate: false
    }
  },
  methods: {
    onSearch(value) {
      console.log('搜索:', value)
    }
  }
}
</script>

<style scoped>
.data-listing-page {
  background: #f5f7fa;
  min-height: 100vh;
}
.header-banner {
  background: linear-gradient(90deg, #e6f2ff 0%, #f0f8ff 100%);
  padding: 30px 20px;
  text-align: center;
}
.header-banner h1 {
  font-size: 36px;
  color: #1f2937;
  margin-bottom: 20px;
}
.search-bar {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
}
.register-btn {
  height: 40px;
}
.service-tags {
  display: flex;
  justify-content: center;
  gap: 15px;
}
.section {
  margin: 40px 20px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
.section-title {
  font-size: 20px;
  color: #1f2937;
  border-left: 4px solid #1890ff;
  padding-left: 10px;
  margin-bottom: 20px;
}
.process-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  justify-content: center;
}
.process-btn {
  background: #1890ff;
  border-color: #1890ff;
}

/* 新增：卡片式登记流程样式 */
.process-cards {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  padding: 20px 0;
}
.process-card {
  width: 220px;
  height: 220px;
  background: linear-gradient(180deg, #e6f2ff 0%, #ffffff 100%);
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.process-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 10%;
  right: 10%;
  height: 12px;
  background: #1890ff;
  border-radius: 0 0 8px 8px;
}
.card-icon {
  position: relative;
  margin-bottom: 15px;
  font-size: 48px;
}
.icon-badge {
  position: absolute;
  bottom: -5px;
  right: -5px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #52c41a;
  color: #fff;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon-badge.check { background: #52c41a; }
.icon-badge.user { background: #1890ff; }
.icon-badge.up { background: #52c41a; }
.icon-badge.link { background: #1890ff; }
.icon-badge.down { background: #52c41a; }
.process-card .card-title {
  font-size: 20px;
  color: #1f2937;
  font-weight: 500;
}
.process-arrow {
  font-size: 24px;
  color: #1890ff;
  font-weight: bold;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.card-content {
  display: flex;
  justify-content: space-between;
}
.card-info {
  flex: 1;
}
.card-title {
  font-size: 16px;
  color: #1f2937;
  margin-bottom: 10px;
  cursor: pointer;
}
.card-title:hover {
  color: #1890ff;
}
.card-subtitle {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}
.card-item {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}
.card-cert {
  width: 120px;
}
.cert-placeholder {
  width: 100%;
  height: 150px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  background: #fafafa;
}
.cert-img {
  width: 100%;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}
</style>