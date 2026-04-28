<template>
  <div class="data-listing-detail-page">
    <!-- 顶部标题栏 -->
    <div class="detail-header-bar">
      <h1>汉寿甲鱼养殖加工数据产品</h1>
    </div>

    <div class="detail-container">
      <!-- 产品描述 -->
      <div class="product-desc">
        汉寿甲鱼数据产品深度融合养殖生产、加工流通全链条数据，运用AI算法构建智能分析模型，为合作社、加工企业、外部销售平台等产业链各环节提供数据支持。通过动态监测养殖生产指标、精准分析加工转化效率、实时追踪市场销售动态，实现从塘口到餐桌的全域数据可视化与价值挖掘。
      </div>

      <!-- 核心信息栏 -->
      <div class="core-info-bar">
        <div class="core-info-item">
          <a-icon type="bank" class="info-icon" />
          <div class="info-content">
            <div class="info-label">登记主体</div>
            <div class="info-value">汉寿县现代农业投资开发有限公司</div>
          </div>
        </div>
        <div class="core-info-item">
          <a-icon type="appstore" class="info-icon" />
          <div class="info-content">
            <div class="info-label">登记对象</div>
            <div class="info-value">数据产品</div>
          </div>
        </div>
        <div class="core-info-item">
          <a-icon type="tags" class="info-icon" />
          <div class="info-content">
            <div class="info-label">产权类型</div>
            <div class="info-value">数据持有权、数据使用权、数据经营权</div>
          </div>
        </div>
        <div class="core-info-item">
          <a-icon type="clock-circle" class="info-icon" />
          <div class="info-content">
            <div class="info-label">首次登记</div>
            <div class="info-value">2026-02-07</div>
          </div>
        </div>
      </div>

      <!-- 登记信息 -->
      <div class="info-section">
        <h3 class="section-title">登记信息</h3>
        <div class="info-grid">
          <div class="info-row">
            <div class="info-col">
              <span class="info-key">登记编号：</span>
              <span class="info-value">SZDEX-RE021769097600000001</span>
            </div>
            <div class="info-col">
              <span class="info-key">登记对象类型：</span>
              <span class="info-value">数据集</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="info-key">数据结构类型：</span>
              <span class="info-value">结构化</span>
            </div>
            <div class="info-col">
              <span class="info-key">数据来源类型：</span>
              <span class="info-value">协议取得</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="info-key">数据形态：</span>
              <span class="info-value">表格</span>
            </div>
            <div class="info-col">
              <span class="info-key">数据规模：</span>
              <span class="info-value">400条</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="info-key">数据量级：</span>
              <span class="info-value">0.0004TB</span>
            </div>
            <div class="info-col">
              <span class="info-key">更新频率：</span>
              <span class="info-value">年</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="info-key">时域范围：</span>
              <span class="info-value">2024-01-01 ~ 2025-08-20</span>
            </div>
            <div class="info-col">
              <span class="info-key">地域范围：</span>
              <span class="info-value">区县级</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="info-key">数据类型：</span>
              <span class="info-value">企业数据</span>
            </div>
            <div class="info-col"></div>
          </div>
          <div class="info-row">
            <div class="info-col-full">
              <span class="info-key">数字经济及其核心产业统计分类：</span>
              <span class="info-value">数字要素驱动业/数据资源与产权交易/数据资源与产权交易</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 主体信息 -->
      <div class="info-section">
        <h3 class="section-title">主体信息</h3>
        <div class="info-grid">
          <div class="info-row">
            <div class="info-col">
              <span class="info-key">登记主体：</span>
              <span class="info-value">汉寿县现代农业投资开发有限公司</span>
            </div>
            <div class="info-col">
              <span class="info-key">统一社会信用代码：</span>
              <span class="info-value">9143072207262666X2</span>
            </div>
          </div>
          <div class="info-row">
            <div class="info-col">
              <span class="info-key">单位规模：</span>
              <span class="info-value">--</span>
            </div>
            <div class="info-col">
              <span class="info-key">所属行业：</span>
              <span class="info-value">金融业</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 证书信息 -->
      <div class="info-section">
        <h3 class="section-title">证书信息</h3>
        <div class="cert-wrapper">
          <img src="@/views/DAM/DataConfirmation/DataListing/img/p2.png" alt="数据产权登记证书" class="cert-img" />
        </div>
        <div class="feedback-btn">
          <a-button type="primary">异议反馈</a-button>
        </div>
      </div>
    </div>

    <!-- 回顶部按钮 -->
    <div class="back-to-top" @click="scrollToTop">
      <a-icon type="up" />
      <span>回顶部</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DataListingDetail',
  methods: {
    scrollToTop() {
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
  }
}
</script>

<style scoped>
.data-listing-detail-page {
  background: #f5f7fa;
  min-height: 100vh;
}
.detail-header-bar {
  background: #fff;
  padding: 15px 20px;
  border-bottom: 1px solid #e8e8e8;
}
.detail-header-bar h1 {
  font-size: 24px;
  color: #1f2937;
  margin: 0;
}
.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.product-desc {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}
.core-info-bar {
  background: #fff;
  padding: 15px 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
}
.core-info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.info-icon {
  font-size: 18px;
  color: #1890ff;
}
.info-content {
  display: flex;
  flex-direction: column;
}
.info-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}
.info-value {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
}
.info-section {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}
.section-title {
  font-size: 16px;
  color: #1f2937;
  margin-bottom: 15px;
  font-weight: 600;
}
.info-grid {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}
.info-row {
  display: flex;
  margin-bottom: 10px;
}
.info-col {
  flex: 1;
  display: flex;
}
.info-col-full {
  flex: 1;
  display: flex;
}
.info-key {
  font-size: 14px;
  color: #666;
  min-width: 160px;
}
.info-value {
  font-size: 14px;
  color: #1f2937;
}
.cert-wrapper {
  text-align: center;
  margin: 20px 0;
}
.cert-full-img {
  max-width: 600px;
  height: auto;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}
.feedback-btn {
  text-align: right;
}
.back-to-top {
  position: fixed;
  right: 20px;
  bottom: 80px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  font-size: 12px;
  color: #666;
}
.back-to-top:hover {
  color: #1890ff;
}
</style>