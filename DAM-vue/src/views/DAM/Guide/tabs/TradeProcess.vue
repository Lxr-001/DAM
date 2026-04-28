<template>
  <div class="trade-page">
    <!-- 交易四步走 标题 -->
    <h2 class="main-title">交易四步走</h2>

    <!-- 顶部步骤导航 -->
    <!-- 横向步骤导航（带连接线） -->
    <div class="horizontal-steps">
      <div class="step-item" v-for="(item, index) in stepList" :key="index">
        <div class="step-dot">
          <span>{{ index + 1 }}</span>
        </div>
        <span class="step-label">{{ item.title }}</span>
        <!-- 横向连接线（除最后一个外都显示） -->
        <div class="step-line" v-if="index < stepList.length - 1"></div>
      </div>
    </div>

    <!-- 四步走内容区 -->
    <div class="process-container">
      <div class="process-grid">
        <!-- 左侧步骤卡片列 -->
        <div class="left-cards">
          <div class="step-card" v-for="(item, index) in processList" :key="index">
            <span class="card-num">{{ '0' + (index + 1) }}</span>
            <span class="card-title">{{ item.title }}</span>
            <!-- 向下箭头 (除了最后一个) -->
            <div class="arrow-down" v-if="index < processList.length - 1"></div>
            <!-- 向下连接线 (除了最后一个) -->
            <div class="connect-line" v-if="index < processList.length - 1"></div>
          </div>
        </div>

        <!-- 右侧流程内容列 -->
        <div class="right-content">
          <div class="flow-card" v-for="(item, index) in processList" :key="index">
            <div class="flow-row">
              <!-- 步骤1 -->
              <div class="flow-item">
                <a-icon :type="item.icons[0]" class="flow-icon" />
                <span class="flow-text">{{ item.steps[0] }}</span>
              </div>
              <span class="flow-connector">+</span>
              <!-- 步骤2 -->
              <div class="flow-item">
                <a-icon :type="item.icons[1]" class="flow-icon" />
                <span class="flow-text">{{ item.steps[1] }}</span>
              </div>
              <span class="flow-connector arrow">></span>
              <!-- 步骤3 -->
              <div class="flow-item">
                <a-icon :type="item.icons[2]" class="flow-icon" />
                <span class="flow-text">{{ item.steps[2] }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 1+6规则体系 标题 -->
    <h2 class="main-title" style="margin-top: 64px;">“1+6”规则体系</h2>

    <!-- 规则体系内容区 -->
    <div class="rules-container">
      <!-- 左侧主规则卡片 -->
      <div class="main-rule-card">
        <p class="main-rule-text">《西部数据交易中心<br>交易规则》</p>
      </div>

      <span class="plus-sign">+</span>

      <!-- 右侧6个子规则 -->
      <div class="sub-rules-grid">
        <div class="sub-rule-item" v-for="(rule, index) in ruleList" :key="index">
          <a-icon type="file-text" class="sub-rule-icon" />
          <span class="sub-rule-text">{{ rule }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DataTradeProcessFinal',
  data() {
    return {
      // 顶部导航步骤
      stepList: [
        { title: '主体入驻' },
        { title: '产品上架' },
        { title: '交易撮合' },
        { title: '交付结算' }
      ],
      // 流程详情数据 (严格对应设计图)
      processList: [
        {
          title: '主体入驻',
          icons: ['file-text', 'check-circle', 'file-text'],
          steps: ['注册账号', '实名认证', '获得交易资格']
        },
        {
          title: '产品上架',
          icons: ['file-text', 'search', 'file-text'],
          steps: ['产品信息录入', '平台审核', '产品上架交易']
        },
        {
          title: '交易撮合',
          icons: ['experiment', 'search', 'check-circle'],
          steps: ['试用、撮合、购买', '合规审核', '达成数据交易']
        },
        {
          title: '交付结算',
          icons: ['file-text', 'credit-card', 'file-text'],
          steps: ['产品交付', '结算支付', '领取交易凭证']
        }
      ],
      // 1+6规则列表
      ruleList: [
        '《西部数据交易中心合规审核指南》(试行)',
        '《西部数据交易中心数据评估指南》(试行)',
        '《西部数据交易中心数据训练场使用指南》(试行)',
        '《西部数据交易中心交易实施技术指南》(试行)',
        '《西部数据交易中心安全保障规范》(试行)',
        '《西部数据交易中心交易主体管理指南》(试行)'
      ]
    }
  }
}
</script>

<style scoped>
/* 全局重置与基础样式 */
.trade-page {
  width: 100%;
  max-width: 1100px;
  margin: 0 auto;
  padding: 40px 20px;
  background: #ffffff;
  font-family: "Microsoft Yahei", sans-serif;
  box-sizing: border-box;
}

.main-title {
  font-size: 24px;
  font-weight: 600;
  color: #333333;
  text-align: center;
  margin: 0 0 32px 0;
}

/* 顶部步骤导航 */
.top-step-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 60px;
  position: relative;
}

.top-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  width: 120px;
}

/* 步骤圆点 - 蓝绿渐变 */
.step-dot {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff 0%, #52c41a 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-bottom: 8px;
  z-index: 2;
}

.step-label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

/* 步骤连接线 - 与设计图一致的淡蓝线 */
.step-line {
  position: absolute;
  top: 12px;
  left: 120px;
  width: calc(100% - 120px);
  height: 2px;
  background-color: #e6f7ff;
  z-index: 1;
}

/* 流程主体容器 */
.process-container {
  padding: 0 20px;
}

.process-grid {
  display: grid;
  grid-template-columns: 140px 1fr;
  gap: 24px;
}

/* 左侧步骤卡片 */
.left-cards {
  display: flex;
  flex-direction: column;
  gap: 0; /* 移除间距，让连线自然衔接 */
}

.step-card {
  height: 90px;
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  padding: 0 10px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(24, 144, 255, 0.1);
  margin-bottom: 20px; /* 给连接线留出高度 */
}

.step-card:last-child {
  margin-bottom: 0; /* 最后一个卡片不留底部间距 */
}


/* 横向步骤导航容器 */
.horizontal-steps {
  display: flex;
  justify-content: space-around;
  align-items: center;
  position: relative;
  width: 100%;
}

/* 单个步骤项 */
.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
}

/* 步骤圆点 - 蓝绿渐变 */
.step-dot {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff 0%, #52c41a 100%);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 16px;
  z-index: 2;
}

/* 步骤标签 */
.step-label {
  font-size: 16px;
  color: #666666;
  white-space: nowrap;
}

/* 横向连接线 - 核心实现 */
.step-line {
  position: absolute;
  top: 24px; /* 与圆点垂直居中 */
  left: calc(50% + 40px); /* 从当前步骤圆点右侧开始 */
  width: calc(100% - 80px); /* 覆盖到下一个步骤圆点左侧 */
  height: 2px;
  background-color: #e6f7ff; /* 淡蓝色，与设计图一致 */
  z-index: 1;
}

.card-num {
  font-size: 32px;
  font-weight: 300;
  color: #ffffff;
  line-height: 1;
  margin-bottom: 4px;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
  color: #1890ff;
}

/* 向下箭头 */
.arrow-down {
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 12px solid #bae7ff;
  z-index: 2;
}

/* 向下连接线 - 新增 */
.connect-line {
  position: absolute;
  bottom: -20px;
  left: 50%;
  transform: translateX(-50%);
  width: 2px;
  height: 20px;
  background-color: #bae7ff;
  z-index: 1;
}

/* 右侧流程内容 */
.right-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 流程白色卡片 */
.flow-card {
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  height: 90px;
  box-sizing: border-box;
}

.flow-row {
  display: flex;
  align-items: center;
  width: 100%;
}

.flow-item {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 4px;
  padding: 8px 16px;
  min-width: 180px;
  justify-content: flex-start;
}

.flow-icon {
  color: #1890ff;
  font-size: 16px;
  margin-right: 8px;
}

.flow-text {
  font-size: 14px;
  color: #333;
}

/* 连接符 */
.flow-connector {
  font-size: 14px;
  color: #1890ff;
  margin: 0 16px;
  font-weight: 500;
}

.flow-connector.arrow {
  font-size: 16px;
}

/* 规则体系 */
.rules-container {
  display: flex;
  align-items: center;
  margin-top: 16px;
  padding: 0 20px;
  gap: 16px;
}

/* 主规则卡片 - 带立体感 */
.main-rule-card {
  width: 200px;
  height: 160px;
  background: linear-gradient(135deg, #f0f5ff 0%, #e6f7ff 100%);
  border: 1px solid #91d5ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
  box-sizing: border-box;
  box-shadow: 0 4px 8px rgba(145, 213, 255, 0.3);
  position: relative;
  overflow: hidden;
}

/* 模拟设计图中的白色几何块 */
.main-rule-card::before {
  content: '';
  position: absolute;
  right: -20px;
  bottom: -20px;
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.4);
  transform: rotate(45deg);
}

.main-rule-text {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  line-height: 1.6;
  z-index: 1;
  margin: 0;
}

.plus-sign {
  font-size: 20px;
  color: #1890ff;
  font-weight: bold;
  margin: 0 8px;
}

/* 子规则网格 */
.sub-rules-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  flex: 1;
}

.sub-rule-item {
  background: #ffffff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 12px 16px;
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
}

.sub-rule-icon {
  color: #1890ff;
  font-size: 14px;
  margin-right: 8px;
  margin-top: 2px;
  flex-shrink: 0;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .process-grid {
    grid-template-columns: 1fr;
  }
  .left-cards {
    flex-direction: row;
    overflow-x: auto;
    padding-bottom: 10px;
  }
  .step-card {
    margin-bottom: 0;
    margin-right: 20px;
  }
  .arrow-down, .connect-line {
    display: none;
  }
  .rules-container {
    flex-direction: column;
    align-items: flex-start;
  }
  .main-rule-card {
    width: 100%;
  }
  .sub-rules-grid {
    grid-template-columns: 1fr;
  }
}
</style>