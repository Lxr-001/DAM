<template>
  <div class="trade-hall-page">
    <!-- 顶部搜索区 -->
    <div class="header-section">
      <h1 class="page-title">交易市场</h1>
      <p class="subtitle">安全合规交易，价值高效释放</p>
      <div class="search-tabs">
        <a-tabs default-active-key="1" @change="handleTabChange">
          <a-tab-pane key="1" tab="数据产品搜索">
            <div class="search-box">
              <a-input-search
                placeholder="请输入您想搜索的数据产品"
                enter-button="搜索产品"
                size="large"
                @search="onSearch"
              />
            </div>
          </a-tab-pane>
          <a-tab-pane key="2" tab="数据服务搜索">
            <div class="search-box">
              <a-input-search
                placeholder="请输入您想搜索的数据服务"
                enter-button="搜索服务"
                size="large"
                @search="onSearch"
              />
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>

    <!-- 分类导航区 -->
    <div class="category-nav">
      <div class="category-left">
        <div class="category-title">
          <a-icon type="appstore" />
          <span>数据产品</span>
          <a href="#" class="view-all">查看全部 →</a>
        </div>
        <a-tabs default-active-key="api" class="category-tabs">
          <a-tab-pane key="api" tab="数据API" />
          <a-tab-pane key="dataset" tab="数据集" />
          <a-tab-pane key="app" tab="数据应用" />
        </a-tabs>
      </div>
      <div class="category-right">
        <div class="category-title">
          <a-icon type="cloud-server" />
          <span>数据服务</span>
          <a href="#" class="view-all">查看全部 →</a>
        </div>
        <a-tabs default-active-key="solution" class="category-tabs">
          <a-tab-pane key="solution" tab="解决方案" />
          <a-tab-pane key="model" tab="算法模型" />
          <a-tab-pane key="tool" tab="应用工具" />
        </a-tabs>
      </div>
    </div>

    <!-- 全部产品标题 -->
    <div class="all-products-title">
      <p>全部产品</p>
      <h2>了解郑州数据交易中心全部数据产品和服务</h2>
    </div>

    <!-- 产品展示区 -->
    <div class="products-container">
      <!-- 数据API模块 -->
      <div class="product-section">
        <div class="section-sidebar">
          <h3>数据API</h3>
          <p>http(s)标准协议，支持多种订购规格，灵活的计量方式，实时准确的调用计量能力</p>
          <a href="#" class="section-more">→</a>
          <div class="sidebar-dots">
            <span class="dot active"></span>
          </div>
        </div>
        <div class="product-grid">
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>郑州市企业法人国籍核验…</h4>
                <a-tag color="blue">企业信息</a-tag>
              </div>
            </div>
            <p class="card-desc">该产品基于郑州市企业数据开发形成，金融机构得到用户授权后可以通过统一社会信用代码查询企业法人国…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>行业风险评估报告</h4>
                <a-tag color="blue">企业信息</a-tag>
              </div>
            </div>
            <p class="card-desc">适用于法人企业及个体工商户，基于用户身份信息识别其行业风险特征，通过结构化策略输出行业风险码，…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>煤炭行业综合数据智库</h4>
                <a-tag color="orange">AI智能</a-tag>
              </div>
            </div>
            <p class="card-desc">该煤炭行业知识问答数据资产包含了国家在煤炭行业的相关法律法规及郑煤集团标准化管理规章制度，依…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>大学商圈餐饮交易分析</h4>
                <a-tag color="purple">客群洞察</a-tag>
              </div>
            </div>
            <p class="card-desc">本产品提供指定商圈餐饮商户的日均交易金额、交易订单量、客单价、注册人数、充值人数等数据，为分析…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
        </div>
        <div class="provider-sidebar">
          <h4>实力数商</h4>
          <div class="provider-item" v-for="item in providers1" :key="item.id">
            <img :src="item.logo" alt="provider" class="provider-logo" />
            <div class="provider-info">
              <p class="provider-name">{{ item.name }}</p>
              <p class="provider-date">{{ item.date }}</p>
            </div>
            <a-tag :color="item.tagColor">{{ item.tag }}</a-tag>
          </div>
        </div>
      </div>

      <!-- 数据集模块 -->
      <div class="product-section">
        <div class="section-sidebar">
          <h3>数据集</h3>
          <p>结构化或非结构化数据集，适用于程序开发、模型训练等多种场景。</p>
          <a href="#" class="section-more">→</a>
          <div class="sidebar-dots">
            <span class="dot active"></span>
          </div>
        </div>
        <div class="product-grid">
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>钢材历史价格分析数据集</h4>
                <a-tag color="red">数据挖掘</a-tag>
              </div>
            </div>
            <p class="card-desc">缔开公司通过产业链各个环节，将钢材历史价格进行分析，形成数据集。深度挖掘钢材价格波动背后的数据…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>医学学科类带图试题集</h4>
                <a-tag color="red">数据挖掘</a-tag>
              </div>
            </div>
            <p class="card-desc">(1) 原图题主要为工科和文科类：题干含图，答案不含图；(2) 覆盖基础医学、临床医学、预防医学等核…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>金域医学侵袭性真菌感染…</h4>
                <a-tag color="red">数据挖掘</a-tag>
              </div>
            </div>
            <p class="card-desc">基于患者授权医院委托第三方机构进行检测及可使用匿名化检测数据，金域实验室进行呼吸道病原体相关检…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>标准地址产品（12级）</h4>
                <a-tag color="cyan">地理空间</a-tag>
              </div>
            </div>
            <p class="card-desc">标准地址是把地址拆分为12个字段，分别是省、市、区、县、乡/镇/街道、社区/行政村、街/路/巷、门牌、小…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
        </div>
        <div class="provider-sidebar">
          <h4>实力数商</h4>
          <div class="provider-item" v-for="item in providers2" :key="item.id">
            <img :src="item.logo" alt="provider" class="provider-logo" />
            <div class="provider-info">
              <p class="provider-name">{{ item.name }}</p>
              <p class="provider-date">{{ item.date }}</p>
            </div>
            <a-tag :color="item.tagColor">{{ item.tag }}</a-tag>
          </div>
        </div>
      </div>

      <!-- 数据应用模块 -->
      <div class="product-section">
        <div class="section-sidebar">
          <h3>数据应用</h3>
          <p>包括SaaS服务应用和本地化部署应用，数据、程序融合开发，开箱即用。</p>
          <a href="#" class="section-more">→</a>
          <div class="sidebar-dots">
            <span class="dot active"></span>
          </div>
        </div>
        <div class="product-grid">
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>君友智达-自动化报告生成</h4>
                <a-tag color="orange">AI智能</a-tag>
              </div>
            </div>
            <p class="card-desc">君友智达-自动化报告生成是一种利用人工智能（AI）、自然语言处理（NLP）、数据可视化和自动化技术，…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>互联网内容规范审校平台</h4>
                <a-tag color="blue">企业信息</a-tag>
              </div>
            </div>
            <p class="card-desc">互联网内容规范审校平台(大河认真-1S)，由大河网数字科技有限公司和中国科学院软件研究所联合成立的新…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>财金先生</h4>
                <a-tag color="blue">企业信息</a-tag>
              </div>
            </div>
            <p class="card-desc"></p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>建筑云查询系统</h4>
                <a-tag color="blue">电子商务</a-tag>
              </div>
            </div>
            <p class="card-desc"></p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
        </div>
        <div class="provider-sidebar">
          <h4>实力数商</h4>
          <div class="provider-item" v-for="item in providers3" :key="item.id">
            <img :src="item.logo" alt="provider" class="provider-logo" />
            <div class="provider-info">
              <p class="provider-name">{{ item.name }}</p>
              <p class="provider-date">{{ item.date }}</p>
            </div>
            <a-tag :color="item.tagColor">{{ item.tag }}</a-tag>
          </div>
        </div>
      </div>

      <!-- 解决方案模块 -->
      <div class="product-section">
        <div class="section-sidebar">
          <h3>解决方案</h3>
          <p>根据数据基础和场景需求，可定制化解决方案，发挥数据价值。</p>
          <a href="#" class="section-more">→</a>
          <div class="sidebar-dots">
            <span class="dot active"></span>
          </div>
        </div>
        <div class="product-grid">
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>数据资产入表全流程服务</h4>
                <a-tag color="purple">数据治理</a-tag>
              </div>
            </div>
            <p class="card-desc">企业数据资产入表全流程服务说明 本服务为企业提供数据资产入表全环节专业化落地支持，覆盖从数据资源…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>企业ESG金融服务交易平台</h4>
                <a-tag color="gold">金融风控</a-tag>
              </div>
            </div>
            <p class="card-desc">依托大数据信用体系，集评价、交易、多维大数据分析、知识库体系等功能于一体的行业绿色科技服务交…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>县域经济产业分析解决方案</h4>
                <a-tag color="cyan">智慧文旅</a-tag>
              </div>
            </div>
            <p class="card-desc">强化县域商业体系建设，构建县域商业大数据平台，致力于推动县域商业的数字化转型，提升政府治理和企…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>AI深度伪造检测工具</h4>
                <a-tag color="orange">AI智能</a-tag>
              </div>
            </div>
            <p class="card-desc">AI深度伪造检测工具是一款基于自研60亿参数大模型与行业最大规模伪造数据集的智能鉴伪解决方案。该工…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
        </div>
        <div class="provider-sidebar">
          <h4>实力数商</h4>
          <div class="provider-item" v-for="item in providers4" :key="item.id">
            <img :src="item.logo" alt="provider" class="provider-logo" />
            <div class="provider-info">
              <p class="provider-name">{{ item.name }}</p>
              <p class="provider-date">{{ item.date }}</p>
            </div>
            <a-tag :color="item.tagColor">{{ item.tag }}</a-tag>
          </div>
        </div>
      </div>

      <!-- 算法模型模块 -->
      <div class="product-section">
        <div class="section-sidebar">
          <h3>算法模型</h3>
          <p>数据挖掘、数据分析、建模计算，赋能交通、文旅、金融、医疗等多重场景。</p>
          <a href="#" class="section-more">→</a>
          <div class="sidebar-dots">
            <span class="dot active"></span>
          </div>
        </div>
        <div class="product-grid">
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>核保风控数据服务</h4>
                <a-tag color="gold">金融风控</a-tag>
              </div>
            </div>
            <p class="card-desc">该服务通过API接口实时调用医疗数据，在用户授权基础上，为保险公司提供核保风险评分与等级评估，实现…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>桥梁博士V5.0计算软件</h4>
                <a-tag color="cyan">数据工具</a-tag>
              </div>
            </div>
            <p class="card-desc">《桥梁博士V5.0》软件平台针对设计单位、业主单位、桥梁检测单位等客户在图形平台、建模技术、力学核…</p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>梧桐—多方安全计算</h4>
                <a-tag color="gold">金融风控</a-tag>
              </div>
            </div>
            <p class="card-desc"></p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
          <a-card class="product-card" hoverable>
            <div class="card-header">
              <img src="https://via.placeholder.com/40" alt="logo" class="card-logo" />
              <div>
                <h4>数据治理-ETL工具定制</h4>
                <a-tag color="purple">数据治理</a-tag>
              </div>
            </div>
            <p class="card-desc"></p>
            <p class="card-price">¥ 登录后可见</p>
          </a-card>
        </div>
        <div class="provider-sidebar">
          <h4>实力数商</h4>
          <div class="provider-item" v-for="item in providers5" :key="item.id">
            <img :src="item.logo" alt="provider" class="provider-logo" />
            <div class="provider-info">
              <p class="provider-name">{{ item.name }}</p>
              <p class="provider-date">{{ item.date }}</p>
            </div>
            <a-tag :color="item.tagColor">{{ item.tag }}</a-tag>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧悬浮工具栏 -->
    <div class="float-toolbar">
      <div class="toolbar-item">
        <a-icon type="customer-service" class="toolbar-icon" />
        <p>立即沟通</p>
      </div>
      <div class="toolbar-item">
        <a-icon type="mail" class="toolbar-icon" />
        <p>联系邮箱</p>
      </div>
      <div class="toolbar-item" @click="backToTop">
        <a-icon type="vertical-align-top" class="toolbar-icon" />
        <p>返回顶部</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TradeHall',
  data() {
    return {
      // 实力数商数据
      providers1: [
        { id: 1, name: '山西浩宜通科技有限公司', date: '2024-09-23', tag: '其他', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 2, name: '深圳优钱信息技术有限公司', date: '2023-10-30', tag: '其他', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 3, name: '北京鸣农数据科技有限公司', date: '2023-03-14', tag: '三农', tagColor: 'green', logo: 'https://via.placeholder.com/40' },
        { id: 4, name: '上海点微网络科技有限公司', date: '2023-05-16', tag: '企服', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 5, name: '云图数据科技（郑州）有限公司', date: '2024-02-26', tag: '企服', tagColor: 'blue', logo: 'https://via.placeholder.com/40' }
      ],
      providers2: [
        { id: 1, name: '河南君友数字科技有限公司', date: '2023-04-21', tag: '其他', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 2, name: '南阳商祺数贸科技有限公司', date: '2024-11-14', tag: '交通', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 3, name: '中科光启空间信息技术有限公司', date: '2023-03-24', tag: '时空', tagColor: 'cyan', logo: 'https://via.placeholder.com/40' },
        { id: 4, name: '河南安兴停车管理有限公司', date: '2024-11-07', tag: '交通', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 5, name: '许昌市智慧停车经营管理有限…', date: '2024-01-24', tag: '交通', tagColor: 'blue', logo: 'https://via.placeholder.com/40' }
      ],
      providers3: [
        { id: 1, name: '河南君友数字科技有限公司', date: '2023-04-21', tag: '其他', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 2, name: '河南大河网数字科技有限公司', date: '2024-04-22', tag: '其他', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 3, name: '中科光启空间信息技术有限公司', date: '2023-03-24', tag: '时空', tagColor: 'cyan', logo: 'https://via.placeholder.com/40' },
        { id: 4, name: '河南联融供应链科技有限公司', date: '', tag: '住建', tagColor: 'red', logo: 'https://via.placeholder.com/40' }
      ],
      providers4: [
        { id: 1, name: '中国移动通信集团河南有限公司', date: '2024-06-21', tag: '通信', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 2, name: '河南君友数字科技有限公司', date: '2023-04-21', tag: '其他', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 3, name: '江苏荣泽信息科技股份有限公司', date: '2023-06-02', tag: '其他', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 4, name: '山东胜软科技股份有限公司', date: '2024-03-13', tag: '其他', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 5, name: '北京海泰方圆科技股份有限公司', date: '2024-09-29', tag: '政务', tagColor: 'blue', logo: 'https://via.placeholder.com/40' }
      ],
      providers5: [
        { id: 1, name: '中国移动通信集团河南有限公司', date: '2024-06-21', tag: '通信', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 2, name: '河南数科数字科技有限公司', date: '2024-09-09', tag: '能源', tagColor: 'blue', logo: 'https://via.placeholder.com/40' },
        { id: 3, name: '深圳市指跃未来科技有限公司', date: '2024-09-30', tag: '住建', tagColor: 'red', logo: 'https://via.placeholder.com/40' }
      ]
    }
  },
  methods: {
    handleTabChange(key) {
      console.log('Tab changed to:', key);
    },
    onSearch(value) {
      console.log('Search:', value);
    },
    backToTop() {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }
};
</script>

<style scoped>
.trade-hall-page {
  max-width: 1440px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 顶部搜索区 */
.header-section {
  text-align: center;
  margin-bottom: 40px;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 100%);
  padding: 40px 20px;
  border-radius: 12px;
}
.page-title {
  font-size: 36px;
  font-weight: 700;
  color: #1f2329;
  margin-bottom: 8px;
}
.subtitle {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 24px;
}
.search-tabs {
  max-width: 800px;
  margin: 0 auto;
}
.search-box {
  margin-top: 16px;
}

/* 分类导航区 */
.category-nav {
  display: flex;
  justify-content: space-between;
  background: #f8fafc;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 32px;
}
.category-left, .category-right {
  width: 48%;
}
.category-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #1f2329;
  margin-bottom: 16px;
}
.view-all {
  margin-left: auto;
  font-size: 14px;
  color: #1890ff;
}
.category-tabs >>> .ant-tabs-nav {
  margin-bottom: 0;
}

/* 全部产品标题 */
.all-products-title {
  margin-bottom: 24px;
}
.all-products-title p {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}
.all-products-title h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1f2329;
}

/* 产品展示区 */
.products-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
}
.product-section {
  display: grid;
  grid-template-columns: 200px 1fr 280px;
  gap: 24px;
  padding: 24px 0;
  border-bottom: 1px solid #e2e8f0;
}
.section-sidebar {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.section-sidebar h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2329;
}
.section-sidebar p {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
}
.section-more {
  font-size: 24px;
  color: #1890ff;
  margin-top: auto;
}
.sidebar-dots {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #cbd5e1;
}
.dot.active {
  background: #1890ff;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.product-card {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}
.card-logo {
  width: 40px;
  height: 40px;
  border-radius: 4px;
}
.card-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2329;
  margin: 0 0 4px 0;
}
.card-desc {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 12px;
}
.card-price {
  font-size: 14px;
  color: #1890ff;
  font-weight: 500;
}

.provider-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.provider-sidebar h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2329;
  margin-bottom: 12px;
}
.provider-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.provider-logo {
  width: 40px;
  height: 40px;
  border-radius: 4px;
}
.provider-info {
  flex: 1;
}
.provider-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2329;
  margin: 0 0 4px 0;
}
.provider-date {
  font-size: 12px;
  color: #94a3b8;
  margin: 0;
}

/* 右侧悬浮工具栏 */
.float-toolbar {
  position: fixed;
  right: 20px;
  bottom: 100px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  padding: 8px;
}
.toolbar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}
.toolbar-item:hover {
  background: #f1f5f9;
}
.toolbar-icon {
  font-size: 20px;
  color: #1890ff;
  margin-bottom: 4px;
}
.toolbar-item p {
  font-size: 12px;
  color: #64748b;
  margin: 0;
}
</style>