<template>
  <a-drawer
    title="交易备案登记"
    :width="1000"
    :visible="visible"
    @close="onClose"
    :maskClosable="false"
  >
    <a-steps :current="0" style="margin-bottom: 30px;">
      <a-step title="基本信息" />
      <a-step title="提交备案证明材料" />
    </a-steps>

    <a-form-model layout="vertical" :model="form" :rules="rules" ref="formRef">
      <!-- 交易主体信息 -->
      <div class="form-section">
        <h3 class="section-title">交易主体信息</h3>
        <div class="form-row">
          <div class="form-col">
            <span>登记身份：</span>
            <a-radio-group v-model="form.identity">
              <a-radio value="seller">我是卖家</a-radio>
              <a-radio value="buyer">我是买家</a-radio>
            </a-radio-group>
          </div>
          <div class="form-col">
            <span>备案类型：</span>
            <a-radio-group v-model="form.recordType">
              <a-radio value="once">一次性</a-radio>
              <a-radio value="periodic">周期性</a-radio>
            </a-radio-group>
          </div>
        </div>
        <div class="form-row">
          <a-form-model-item label="产品提供商名称" prop="providerName" class="form-col" required>
            <a-input placeholder="请选择或输入产品提供商名称" v-model="form.providerName" />
          </a-form-model-item>
          <a-form-model-item label="产品应用商名称" prop="appName" class="form-col" required>
            <a-input placeholder="请选择或输入产品应用商名称" v-model="form.appName" />
          </a-form-model-item>
        </div>
      </div>

      <!-- 交易产品信息 -->
      <div class="form-section">
        <h3 class="section-title">交易产品信息</h3>
        <div class="form-row">
          <a-form-model-item label="备案产品分类" prop="productCategory" class="form-col" required>
            <a-select v-model="form.productCategory" placeholder="请选择备案产品分类">
              <a-select-option value="dataProduct">数据产品</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="产品类型" prop="productType" class="form-col" required>
            <a-select v-model="form.productType" placeholder="请选择产品类型">
              <a-select-option value="type1">类型1</a-select-option>
              <a-select-option value="type2">类型2</a-select-option>
            </a-select>
          </a-form-model-item>
        </div>
        <div class="form-row">
          <a-form-model-item label="产品名称" prop="productName" class="form-col" required>
            <a-input placeholder="请输入或选择产品名称" v-model="form.productName" />
          </a-form-model-item>
          <a-form-model-item label="定价模式" prop="pricingMode" class="form-col">
            <a-select v-model="form.pricingMode" placeholder="请选择定价模式">
              <a-select-option value="mode1">模式1</a-select-option>
              <a-select-option value="mode2">模式2</a-select-option>
            </a-select>
          </a-form-model-item>
        </div>
        <a-form-model-item label="产品用途简述" prop="productPurpose" required>
          <a-textarea
            placeholder="请输入产品用途，最多支持300字"
            v-model="form.productPurpose"
            :maxlength="300"
            show-count
            rows="4"
          />
        </a-form-model-item>
      </div>

      <!-- 交易合同合约信息 -->
      <div class="form-section">
        <h3 class="section-title">交易合同合约信息</h3>
        <div class="form-row">
          <a-form-model-item label="合同起止日期" prop="contractDate" class="form-col" required>
            <a-range-picker
              v-model="form.contractDate"
              placeholder="开始日期 → 结束日期"
              style="width: 100%"
            />
          </a-form-model-item>
          <a-form-model-item label="备案金额" prop="recordAmount" class="form-col" required>
            <a-input-number
              v-model="form.recordAmount"
              placeholder="请输入数据产品交易额"
              style="width: 100%"
              :min="0"
              formatter="元"
              parser="元"
            />
          </a-form-model-item>
        </div>
        <div class="form-row">
          <a-form-model-item label="数据规模" prop="dataScale" class="form-col">
            <a-input-group>
              <a-input-number v-model="form.dataScale" placeholder="请输入数据产品规模" style="width: 80%" />
              <a-input-addon>MB</a-input-addon>
            </a-input-group>
          </a-form-model-item>
          <a-form-model-item label="调用量" prop="callCount" class="form-col">
            <a-input-group>
              <a-input-number v-model="form.callCount" placeholder="请输入产品调用量" style="width: 80%" />
              <a-input-addon>笔</a-input-addon>
            </a-input-group>
          </a-form-model-item>
        </div>
      </div>

      <!-- 推荐码信息 -->
      <div class="form-section">
        <h3 class="section-title">推荐码信息</h3>
        <a-form-model-item label="业务推荐码" prop="recommendCode" required>
          <a-input placeholder="无业务推荐码时请填写0000" v-model="form.recommendCode" />
        </a-form-model-item>
      </div>

      <!-- 底部按钮 -->
      <div class="form-footer">
        <a-button @click="onClose">取消</a-button>
        <a-button type="primary" @click="onSave" style="margin-left: 10px;">保存</a-button>
        <a-button type="primary" @click="onNext" style="margin-left: 10px;">下一步</a-button>
      </div>
    </a-form-model>
  </a-drawer>
</template>

<script>
export default {
  name: 'DataListingOperate',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      form: {
        identity: 'seller',
        recordType: 'once',
        providerName: '',
        appName: '',
        productCategory: 'dataProduct',
        productType: '',
        productName: '',
        pricingMode: '',
        productPurpose: '',
        contractDate: [],
        recordAmount: null,
        dataScale: null,
        callCount: null,
        recommendCode: '0000'
      },
      rules: {
        providerName: [{ required: true, message: '请输入产品提供商名称', trigger: 'blur' }],
        appName: [{ required: true, message: '请输入产品应用商名称', trigger: 'blur' }],
        productCategory: [{ required: true, message: '请选择备案产品分类', trigger: 'change' }],
        productType: [{ required: true, message: '请选择产品类型', trigger: 'change' }],
        productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
        productPurpose: [{ required: true, message: '请输入产品用途简述', trigger: 'blur' }],
        contractDate: [{ required: true, message: '请选择合同起止日期', trigger: 'change' }],
        recordAmount: [{ required: true, message: '请输入备案金额', trigger: 'blur' }],
        recommendCode: [{ required: true, message: '请输入业务推荐码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    onClose() {
      this.$emit('update:visible', false)
    },
    onSave() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          console.log('保存表单:', this.form)
          this.$message.success('保存成功')
        }
      })
    },
    onNext() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          console.log('下一步:', this.form)
          this.$message.success('进入提交备案证明材料步骤')
        }
      })
    }
  }
}
</script>

<style scoped>
.form-section {
  margin-bottom: 30px;
}
.section-title {
  font-size: 16px;
  color: #1f2937;
  margin-bottom: 15px;
  font-weight: 600;
}
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
}
.form-col {
  flex: 1;
}
.form-footer {
  text-align: right;
  margin-top: 30px;
}
</style>