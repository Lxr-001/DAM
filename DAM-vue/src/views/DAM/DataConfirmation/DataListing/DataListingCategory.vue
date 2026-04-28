<template>
  <div class="data-listing-category-page">
    <!-- 顶部搜索栏 -->
    <div class="top-search">
      <a-input-search
        placeholder="请输入数据名称/登记编号/主体名称查询"
        style="width: 70%"
        @search="onSearch"
      />
      <a-button type="primary" class="register-btn">立即登记</a-button>
    </div>

    <div class="content-wrapper">
      <!-- 左侧筛选栏 -->
      <div class="filter-sidebar">
        <a-collapse v-model="activeKey" :bordered="false" expand-icon-position="right">
          <a-collapse-panel key="1" header="登记主体行业">
            <div class="filter-options">
              <a-checkbox-group v-model="industryFilter">
                <a-checkbox value="all" class="filter-item">
                  <span>全部 (148)</span>
                </a-checkbox>
                <a-checkbox value="agriculture" class="filter-item">
                  <span>农、林、牧、渔业 (0)</span>
                </a-checkbox>
                <a-checkbox value="mining" class="filter-item">
                  <span>采矿业 (0)</span>
                </a-checkbox>
                <a-checkbox value="manufacture" class="filter-item">
                  <span>制造业 (6)</span>
                </a-checkbox>
                <a-checkbox value="energy" class="filter-item">
                  <span>电力、热力、燃气及水生产和供应业 (25)</span>
                </a-checkbox>
                <a-checkbox value="construction" class="filter-item">
                  <span>建筑业 (8)</span>
                </a-checkbox>
                <a-checkbox value="wholesale" class="filter-item">
                  <span>批发和零售业 (5)</span>
                </a-checkbox>
                <a-checkbox value="transport" class="filter-item">
                  <span>交通运输、仓储和邮政业 (4)</span>
                </a-checkbox>
                <a-checkbox value="hotel" class="filter-item">
                  <span>住宿和餐饮业 (1)</span>
                </a-checkbox>
                <a-checkbox value="it" class="filter-item">
                  <span>信息传输、软件和信息技术服务业 (27)</span>
                </a-checkbox>
                <a-checkbox value="finance" class="filter-item">
                  <span>金融业 (6)</span>
                </a-checkbox>
                <a-checkbox value="realestate" class="filter-item">
                  <span>房地产业 (1)</span>
                </a-checkbox>
                <a-checkbox value="leasing" class="filter-item">
                  <span>租赁和商务服务业 (12)</span>
                </a-checkbox>
              </a-checkbox-group>
            </div>
          </a-collapse-panel>
        </a-collapse>
      </div>

      <!-- 右侧列表 -->
      <div class="list-content">
        <div class="filter-bar">
          <a-radio-group v-model="objectFilter" button-style="solid" class="object-radio">
            <a-radio-button value="all">全部</a-radio-button>
            <a-radio-button value="resource">数据资源</a-radio-button>
            <a-radio-button value="product">数据产品</a-radio-button>
            <a-radio-button value="asset">数据资产</a-radio-button>
          </a-radio-group>
          <div class="filter-tags">
            <span>产权类型：</span>
            <a-checkbox-group v-model="rightFilter">
              <a-checkbox value="hold">数据持有权</a-checkbox>
              <a-checkbox value="use">数据使用权</a-checkbox>
              <a-checkbox value="manage">数据经营权</a-checkbox>
            </a-checkbox-group>
          </div>
          <div class="filter-tags">
            <span>登记类型：</span>
            <a-checkbox-group v-model="typeFilter">
              <a-checkbox value="first">首次登记</a-checkbox>
              <a-checkbox value="license">许可登记</a-checkbox>
              <a-checkbox value="transfer">转让登记</a-checkbox>
              <a-checkbox value="change">变更登记</a-checkbox>
              <a-checkbox value="cancel">注销登记</a-checkbox>
            </a-checkbox-group>
          </div>
          <div class="filter-tags">
            <span>地域范围：</span>
            <a-checkbox-group v-model="areaFilter">
              <a-checkbox value="international">国际级</a-checkbox>
              <a-checkbox value="national">国家级</a-checkbox>
              <a-checkbox value="provincial">省级</a-checkbox>
              <a-checkbox value="city">市级</a-checkbox>
              <a-checkbox value="county">区县级</a-checkbox>
              <a-checkbox value="below">区县以下级</a-checkbox>
            </a-checkbox-group>
          </div>
          <div class="filter-tags">
            <a-button size="small" @click="resetFilter">全部重置</a-button>
          </div>
          <div class="data-count">共有 148 条数据</div>
        </div>

        <div class="card-list">
          <a-row :gutter="20">
            <!-- 条目1 -->
            <a-col :span="24">
              <a-card class="data-card" hoverable>
                <div class="card-content">
                  <div class="card-info">
                    <h3 class="card-title" @click="$router.push('/DataListingDetail')">汉寿甲鱼养殖加工数据产品</h3>
                    <p class="card-subtitle">
                      <a-icon type="info-circle" /> 汉寿县现代农业投资开发有限公司
                    </p>
                    <div class="card-row">
                      <div class="card-col-left">
                        <p class="card-item">登记编号：SZDEX-RE021769097600000001</p>
                        <p class="card-item">登记对象：数据产品</p>
                      </div>
                      <div class="card-col-right">
                        <p class="card-item">产权类型：数据持有权、数据使用权、数据经营权</p>
                        <p class="card-item">首次登记：2026-02-07</p>
                      </div>
                    </div>
                  </div>
                  <div class="card-cert">
                    <!-- 修复：固定宽高，防止图片溢出 -->
                    <img src="@/views/DAM/DataConfirmation/DataListing/img/p1.png" alt="数据产权登记证书" class="cert-img" />
                    <div class="cert-label">数据产权登记证书</div>
                  </div>
                </div>
              </a-card>
            </a-col>
            <!-- 条目2 -->
            <a-col :span="24">
              <a-card class="data-card" hoverable>
                <div class="card-content">
                  <div class="card-info">
                    <h3 class="card-title">商业地产物理模型标准库</h3>
                    <p class="card-subtitle">
                      <a-icon type="info-circle" /> 深圳泛和科技有限公司
                    </p>
                    <div class="card-row">
                      <div class="card-col-left">
                        <p class="card-item">登记编号：SZDEX-RE021768924800000001</p>
                        <p class="card-item">登记对象：数据产品</p>
                      </div>
                      <div class="card-col-right">
                        <p class="card-item">产权类型：数据持有权、数据使用权、数据经营权</p>
                        <p class="card-item">首次登记：2026-02-04</p>
                      </div>
                    </div>
                  </div>
                  <div class="card-cert">
                    <div class="cert-label">数据产权登记证书</div>
                  </div>
                </div>
              </a-card>
            </a-col>


            <!-- 条目3（图片2新增） -->
            <a-col :span="24">
              <a-card class="data-card" hoverable>
                <div class="card-content">
                  <div class="card-info">
                    <h3 class="card-title">新会陈皮全链路溯源数据产品</h3>
                    <p class="card-subtitle">
                      <a-icon type="environment" /> 深圳前海链藏数据科技有限公司
                    </p>
                    <div class="card-row">
                      <div class="card-col">
                        <p class="card-item">登记编号：SZDEX-RE021768492800000001</p>
                        <p class="card-item">登记对象：数据产品</p>
                      </div>
                      <div class="card-col">
                        <p class="card-item">产权类型：数据持有权、数据使用权、数据经营权</p>
                        <p class="card-item">首次登记：2026-01-31</p>
                      </div>
                    </div>
                  </div>
                  <div class="card-cert">
                    <div class="cert-placeholder">数据产权登记证书</div>
                  </div>
                </div>
              </a-card>
            </a-col>
            <!-- 条目4（图片2新增） -->
            <a-col :span="24">
              <a-card class="data-card" hoverable>
                <div class="card-content">
                  <div class="card-info">
                    <h3 class="card-title">安义县铝型材产业链精准营销数据产品</h3>
                    <p class="card-subtitle">
                      <a-icon type="environment" /> 南昌职城教育投资发展有限公司
                    </p>
                    <div class="card-row">
                      <div class="card-col">
                        <p class="card-item">登记编号：SZDEX-RE021768320000000001</p>
                        <p class="card-item">登记对象：数据产品</p>
                      </div>
                      <div class="card-col">
                        <p class="card-item">产权类型：数据使用权、数据经营权</p>
                        <p class="card-item">首次登记：2026-01-29</p>
                      </div>
                    </div>
                  </div>
                  <div class="card-cert">
                    <div class="cert-placeholder">数据产权登记证书</div>
                  </div>
                </div>
              </a-card>
            </a-col>

          </a-row>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <span class="pagination-total">共 148 条</span>
          <a-pagination
            :total="148"
            :page-size="4"
            :current="1"
            :show-size-changer="false"
            :show-quick-jumper="true"
            @change="onPageChange"
          />
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
  name: 'DataListingCategory',
  data() {
    return {
      activeKey: ['1'],
      industryFilter: ['all'],
      objectFilter: 'all',
      rightFilter: [],
      typeFilter: [],
      areaFilter: []
    }
  },
  methods: {
    onSearch(value) {
      console.log('搜索:', value)
    },
    resetFilter() {
      this.industryFilter = ['all']
      this.objectFilter = 'all'
      this.rightFilter = []
      this.typeFilter = []
      this.areaFilter = []
    },
    onPageChange(page) {
      console.log('页码:', page)
    },
    scrollToTop() {
      window.scrollTo({ top: 0, behavior: 'smooth' })
    }
  }
}
</script>

<style scoped>
.data-listing-category-page {
  background: #fff;
  min-height: 100vh;
}
.top-search {
  background: #1890ff;
  padding: 10px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.register-btn {
  background: #fff;
  color: #1890ff;
  border-color: #fff;
}
.content-wrapper {
  display: flex;
  padding: 20px;
}
.filter-sidebar {
  width: 220px;
  margin-right: 20px;
  background: #fff;
  border-radius: 4px;
}
.filter-options >>> .ant-checkbox-wrapper {
  display: flex;
  justify-content: space-between;
  margin: 8px 0;
}
.filter-item {
  width: 100%;
  display: flex;
  justify-content: space-between;
}
.list-content {
  flex: 1;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
}
.filter-bar {
  margin-bottom: 20px;
}
.object-radio >>> .ant-radio-button-wrapper-checked {
  background: #1890ff;
  border-color: #1890ff;
  color: #fff;
}
.filter-tags {
  margin: 10px 0;
}
.data-count {
  margin-top: 10px;
  color: #666;
  text-align: right;
}
.card-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 5px;
}
.card-row {
  display: flex;
  justify-content: space-between;
  width: 80%;
}
.card-col-left {
  flex: 1;
}
.card-col-right {
  flex: 1;
  text-align: left;
}
.card-item {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}
.card-cert {
  width: 160px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
/* 核心修复：固定证书图片尺寸，防止溢出 */
.cert-img {
  width: 140px;
  height: 200px;
  object-fit: cover;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}
.cert-label {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 20px;
  gap: 10px;
}
.pagination-total {
  color: #666;
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