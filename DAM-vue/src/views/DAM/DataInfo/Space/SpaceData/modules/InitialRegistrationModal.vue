<template>
  <a-drawer
    :visible="visible"
    :width="720"
    title="首次登记"
    placement="right"
    @close="handleClose"
  >
    <div class="registration-steps">
      <a-steps :current="currentStep" size="small">
        <a-step title="选择资产类型" />
        <a-step title="填写资产信息" />
        <a-step title="数据资产确权" />
        <a-step title="选择审批者" />
      </a-steps>
    </div>

    <!-- Step 1: 选择资产类型 -->
    <div v-show="currentStep === 0" class="step-content">
      <a-form :form="step1Form" layout="vertical">
        <a-form-item label="数据资产类型" required>
          <a-select
            v-decorator="['assetType', { rules: [{ required: true, message: '请选择资产类型' }] }]"
            placeholder="请选择数据资产类型"
            @change="handleAssetTypeChange"
          >
            <a-select-option value="dataset">数据集</a-select-option>
            <a-select-option value="api">数据API</a-select-option>
            <a-select-option value="training_set">高质量训练集</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="已有数据资产">
          <a-select
            v-decorator="['existingAssetId']"
            placeholder="请选择数据资产的URL（可选）"
          >
            <a-select-option value="">无（新建）</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <div class="step-footer">
        <a-button type="primary" @click="handleNextStep">下一步</a-button>
      </div>
    </div>

    <!-- Step 2: 填写资产信息 -->
    <div v-show="currentStep === 1" class="step-content">
      <a-form :form="step2Form" layout="vertical">
        <!-- 通用字段 -->
        <a-divider>数据基本信息</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="资产名称" required>
              <a-input
                v-decorator="['assetName', { rules: [{ required: true, message: '请输入资产名称' }, { min: 2, max: 50, message: '长度为2-50字符' }] }]"
                placeholder="请输入资产名称"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="更新频率">
              <a-select v-decorator="['updateFrequency']" placeholder="请选择更新频率">
                <a-select-option value="按天更新">按天更新</a-select-option>
                <a-select-option value="按周更新">按周更新</a-select-option>
                <a-select-option value="按月更新">按月更新</a-select-option>
                <a-select-option value="按年更新">按年更新</a-select-option>
                <a-select-option value="按需更新">按需更新</a-select-option>
                <a-select-option value="不更新">不更新</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-form-item label="资产描述">
          <a-textarea v-decorator="['assetDesc']" :rows="3" placeholder="请输入资产描述" />
        </a-form-item>
        <a-form-item label="时间范围">
          <a-range-picker v-decorator="['timeRange']" style="width: 100%" />
        </a-form-item>

        <!-- 数据集特有字段 -->
        <div v-if="assetFormData.assetType === 'dataset'">
          <a-divider>数据分类信息</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="数据形态" required>
                <a-select
                  v-decorator="['dataStructure', { rules: [{ required: true, message: '请选择数据形态' }] }]"
                  placeholder="请选择数据形态"
                  @change="handleDataStructureChange"
                >
                  <a-select-option value="structured">结构化数据</a-select-option>
                  <a-select-option value="unstructured">非结构化数据</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="数据密级" required>
                <a-select
                  v-decorator="['dataLevel', { rules: [{ required: true, message: '请选择数据密级' }] }]"
                  placeholder="请选择数据密级"
                >
                  <a-select-option value="公开">公开</a-select-option>
                  <a-select-option value="内部">内部</a-select-option>
                  <a-select-option value="秘密">秘密</a-select-option>
                  <a-select-option value="机密">机密</a-select-option>
                  <a-select-option value="绝密">绝密</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item v-if="assetFormData.dataStructure === 'structured'" label="应用场景" required>
            <a-select
              v-decorator="['applicationScenario', { rules: [{ required: true, message: '请选择应用场景' }] }]"
              placeholder="请选择应用场景"
              @change="handleScenarioChange"
            >
              <a-select-option value="共享服务域">共享服务域</a-select-option>
              <a-select-option value="科研生产域">科研生产域</a-select-option>
              <a-select-option value="经营管理域">经营管理域</a-select-option>
              <a-select-option value="试验生产域">试验生产域</a-select-option>
              <a-select-option value="运行支撑域">运行支撑域</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item v-if="assetFormData.dataStructure === 'structured' && assetFormData.applicationScenario" label="细分场景">
            <a-tree-select
              v-decorator="['subScenario']"
              :treeData="subScenarioTree"
              placeholder="请选择细分场景"
              allow-clear
            />
          </a-form-item>

          <a-form-item v-if="assetFormData.dataStructure === 'unstructured'" label="应用场景" required>
            <a-select
              v-decorator="['applicationScenario', { rules: [{ required: true, message: '请选择应用场景' }] }]"
              placeholder="请选择应用场景"
            >
              <a-select-option value="规章制度">规章制度</a-select-option>
              <a-select-option value="标准规范">标准规范</a-select-option>
              <a-select-option value="科技报告">科技报告</a-select-option>
              <a-select-option value="行政公文">行政公文</a-select-option>
              <a-select-option value="新闻资讯">新闻资讯</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item v-if="assetFormData.dataStructure === 'unstructured'" label="技术体系">
            <a-tree-select
              v-decorator="['techSystem']"
              :treeData="techSystemTree"
              placeholder="请选择技术体系"
              allow-clear
            />
          </a-form-item>
        </div>

        <!-- API特有字段 -->
        <div v-if="assetFormData.assetType === 'api'">
          <a-divider>API分类信息</a-divider>
          <a-row :gutter="16">
            <a-col :span="8">
              <a-form-item label="开放程度" required>
                <a-select
                  v-decorator="['openDegree', { rules: [{ required: true, message: '请选择开放程度' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="open">开放API</a-select-option>
                  <a-select-option value="internal">内部API</a-select-option>
                  <a-select-option value="private">私有API</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="操作类型" required>
                <a-select
                  v-decorator="['operationType', { rules: [{ required: true, message: '请选择操作类型' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="read_only">只读API</a-select-option>
                  <a-select-option value="write_only">只写API</a-select-option>
                  <a-select-option value="read_write">读写API</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="8">
              <a-form-item label="数据粒度" required>
                <a-select
                  v-decorator="['dataGranularity', { rules: [{ required: true, message: '请选择数据粒度' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="detail">明细API</a-select-option>
                  <a-select-option value="summary">汇总API</a-select-option>
                  <a-select-option value="wide_table">宽表API</a-select-option>
                  <a-select-option value="theme_domain">主题域API</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>

          <a-divider>API调用信息</a-divider>
          <a-form-item label="调用地址" required>
            <a-input
              v-decorator="['apiAddress', { rules: [{ required: true, message: '请输入调用地址' }] }]"
              placeholder="请输入API调用地址"
            />
          </a-form-item>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="请求方式" required>
                <a-select
                  v-decorator="['apiMethod', { rules: [{ required: true, message: '请选择请求方式' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="GET">GET</a-select-option>
                  <a-select-option value="POST">POST</a-select-option>
                  <a-select-option value="PUT">PUT</a-select-option>
                  <a-select-option value="DELETE">DELETE</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="返回方式" required>
                <a-select
                  v-decorator="['apiReturnType', { rules: [{ required: true, message: '请选择返回方式' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="JSON">JSON</a-select-option>
                  <a-select-option value="XML">XML</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item label="请求参数">
            <a-table
              :columns="paramColumns"
              :dataSource="requestParams"
              :pagination="false"
              rowKey="key"
              size="small"
            >
              <template v-slot:paramName="{ text, record }">
                <a-input v-model="record.paramName" placeholder="参数名称" />
              </template>
              <template v-slot:paramType="{ text, record }">
                <a-input v-model="record.paramType" placeholder="类型" />
              </template>
              <template v-slot:required="{ text, record }">
                <a-checkbox v-model="record.required" />
              </template>
              <template v-slot:description="{ text, record }">
                <a-input v-model="record.description" placeholder="说明" />
              </template>
              <template v-slot:action="{ record }">
                <a @click="handleDeleteParam(record)">删除</a>
              </template>
            </a-table>
            <a-button type="dashed" block @click="handleAddParam" style="margin-top: 8px">新增参数</a-button>
          </a-form-item>

          <a-form-item label="请求示例" required>
            <a-textarea v-decorator="['apiRequestExample', { rules: [{ required: true, message: '请输入请求示例' }] }]" :rows="4" placeholder="请输入请求示例代码" />
          </a-form-item>
          <a-form-item label="成功返回示例" required>
            <a-textarea v-decorator="['apiSuccessExample', { rules: [{ required: true, message: '请输入成功返回示例' }] }]" :rows="4" placeholder="请输入成功返回示例代码" />
          </a-form-item>
          <a-form-item label="失败返回示例">
            <a-textarea v-decorator="['apiErrorExample']" :rows="3" placeholder="请输入失败返回示例" />
          </a-form-item>
        </div>

        <!-- 训练集特有字段 -->
        <div v-if="assetFormData.assetType === 'training_set'">
          <a-divider>训练集分类信息</a-divider>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="数据格式" required>
                <a-select
                  v-decorator="['dataFormat', { rules: [{ required: true, message: '请选择数据格式' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="json">json</a-select-option>
                  <a-select-option value="csv">csv</a-select-option>
                  <a-select-option value="text">text</a-select-option>
                  <a-select-option value="imagefolder">imagefolder</a-select-option>
                  <a-select-option value="soundfolder">soundfolder</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="数据模态" required>
                <a-select
                  v-decorator="['dataModality', { rules: [{ required: true, message: '请选择数据模态' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="text">文本数据</a-select-option>
                  <a-select-option value="image">图像数据</a-select-option>
                  <a-select-option value="audio">音频数据</a-select-option>
                  <a-select-option value="video">视频数据</a-select-option>
                  <a-select-option value="multimodal">多模态数据</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="标注方式" required>
                <a-select
                  v-decorator="['annotationType', { rules: [{ required: true, message: '请选择标注方式' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="supervised">有监督标注数据</a-select-option>
                  <a-select-option value="unsupervised">无监督原始数据</a-select-option>
                  <a-select-option value="weakly">弱监督数据</a-select-option>
                  <a-select-option value="semi">半监督数据</a-select-option>
                  <a-select-option value="self">自监督数据</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="训练阶段" required>
                <a-select
                  v-decorator="['trainingStage', { rules: [{ required: true, message: '请选择训练阶段' }] }]"
                  placeholder="请选择"
                >
                  <a-select-option value="pretrain">预训练数据集</a-select-option>
                  <a-select-option value="finetune">微调数据集</a-select-option>
                  <a-select-option value="validation">验证集</a-select-option>
                  <a-select-option value="test">测试集</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
          <a-form-item label="数据来源" required>
            <a-select
              v-decorator="['dataSource', { rules: [{ required: true, message: '请选择数据来源' }] }]"
              placeholder="请选择"
            >
              <a-select-option value="business">业务部门</a-select-option>
              <a-select-option value="opensource">公开开源</a-select-option>
              <a-select-option value="crawler">爬虫互联网数据</a-select-option>
              <a-select-option value="authorized">合作方授权数据</a-select-option>
              <a-select-option value="synthetic">人工合成数据</a-select-option>
            </a-select>
          </a-form-item>
        </div>

        <!-- 数据体量 -->
        <a-divider>数据体量</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="数据规模（条）">
              <a-input-number v-decorator="['dataScale']" style="width: 100%" :min="0" placeholder="请输入" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="数据体量" required>
              <a-slider v-model="assetFormData.dataVolumeSlider" :marks="{ 0: '0K', 500: '500G', 1000: '1T' }" :min="0" :max="1000" />
              <div style="text-align: center">{{ formatDataVolume(assetFormData.dataVolumeSlider) }}</div>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <div class="step-footer">
        <a-button @click="handlePrevStep">上一步</a-button>
        <a-button type="primary" style="margin-left: 8px" @click="handleNextStep">下一步</a-button>
      </div>
    </div>

    <!-- Step 3: 数据资产确权 -->
    <div v-show="currentStep === 2" class="step-content">
      <a-form :form="step3Form" layout="vertical">
        <a-divider>数据Owner</a-divider>
        <a-form-item label="选择数据Owner" required>
          <a-select
            v-decorator="['ownerId', { rules: [{ required: true, message: '请选择数据Owner' }] }]"
            placeholder="请从当前空间用户列表中选择"
            mode="multiple"
          >
            <a-select-option v-for="user in spaceUsers" :key="user.userId" :value="user.userId">
              {{ user.realname || user.username || '未知用户' }}{{ user.roleNames ? '-' + user.roleNames : '' }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-divider>登记信息确认</a-divider>
        <div class="info-preview">
          <a-descriptions :column="2" bordered size="small">
            <a-descriptions-item label="资产名称">{{ assetFormData.assetName }}</a-descriptions-item>
            <a-descriptions-item label="资产类型">{{ getAssetTypeText(assetFormData.assetType) }}</a-descriptions-item>
            <a-descriptions-item label="数据格式">{{ assetFormData.dataFormat || '-' }}</a-descriptions-item>
            <a-descriptions-item label="数据体量">{{ formatDataVolume(assetFormData.dataVolumeSlider) }}</a-descriptions-item>
          </a-descriptions>
        </div>

        <a-divider>数据资产证书</a-divider>
        <div class="cert-preview">
          <div class="cert-box" v-if="assetFormData.certGenerated">
            <img :src="assetFormData.certPath" alt="证书" />
          </div>
          <div class="cert-placeholder" v-else>
            <a-icon type="file-image" style="font-size: 48px; color: #ccc" />
            <p>点击下方按钮生成证书</p>
          </div>
          <a-button type="primary" @click="handleGenerateCert" :loading="certGenerating">
            {{ assetFormData.certGenerated ? '重新生成证书' : '生成证书' }}
          </a-button>
        </div>
      </a-form>
      <div class="step-footer">
        <a-button @click="handlePrevStep">上一步</a-button>
        <a-button type="primary" style="margin-left: 8px" @click="handleNextStep">下一步</a-button>
      </div>
    </div>

    <!-- Step 4: 选择审批者 -->
    <div v-show="currentStep === 3" class="step-content">
      <a-form :form="step4Form" layout="vertical">
        <a-form-item label="选择审批者" required>
          <a-select
            v-decorator="['approverId', { rules: [{ required: true, message: '请选择审批者' }] }]"
            placeholder="请从当前空间用户中选择一个作为审批者"
          >
            <a-select-option v-for="user in spaceUsers" :key="user.userId" :value="user.userId">
              {{ user.realname || user.username || '未知用户' }}{{ user.roleNames ? '-' + user.roleNames : '' }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <div class="step-footer">
        <a-button @click="handlePrevStep">上一步</a-button>
        <a-button type="primary" style="margin-left: 8px" @click="handleSubmit">提交</a-button>
        <a-button style="margin-left: 8px" @click="handleCancel">取消</a-button>
      </div>
    </div>
  </a-drawer>
</template>

<script>
import { addSpaceAsset, getSpaceAssetList } from '@/api/spaceAsset'
import { getAction } from '@/api/manage'

export default {
  name: 'InitialRegistrationModal',
  props: {
    visible: { type: Boolean, default: false },
    spaceInfo: { type: Object, default: () => ({}) },
    activeNav: { type: String, default: 'dataset' }
  },
  data() {
    return {
      currentStep: 0,
      step1Form: this.$form.createForm(this),
      step2Form: this.$form.createForm(this),
      step3Form: this.$form.createForm(this),
      step4Form: this.$form.createForm(this),
      assetFormData: {
        assetType: 'dataset',
        dataStructure: '',
        applicationScenario: '',
        subScenario: '',
        techSystem: '',
        dataVolumeSlider: 0,
        certGenerated: false,
        certPath: ''
      },
      spaceUsers: [],
      requestParams: [],
      paramColumns: [
        { title: '参数名称', dataIndex: 'paramName', scopedSlots: { customRender: 'paramName' } },
        { title: '类型', dataIndex: 'paramType', scopedSlots: { customRender: 'paramType' } },
        { title: '必填', dataIndex: 'required', width: 60, scopedSlots: { customRender: 'required' } },
        { title: '说明', dataIndex: 'description', scopedSlots: { customRender: 'description' } },
        { title: '操作', key: 'action', width: 60, scopedSlots: { customRender: 'action' } }
      ],
      certGenerating: false,
      structuredSubScenarios: {
        '共享服务域': [
          { label: '标准共享服务', value: '标准共享服务' },
          { label: '综合保障服务', value: '综合保障服务' },
          { label: '质量分析服务', value: '质量分析服务' }
        ],
        '科研生产域': [
          { label: '科研协同', value: '科研协同' },
          { label: '软件开发', value: '软件开发' }
        ],
        '经营管理域': [
          { label: '人才管理', value: '人才管理' },
          { label: '合同管理', value: '合同管理' },
          { label: '市场管理', value: '市场管理' },
          { label: '综合计划', value: '综合计划' },
          { label: '财务管理', value: '财务管理' },
          { label: '质量管理', value: '质量管理' },
          { label: '采购管理', value: '采购管理' },
          { label: '项目管理', value: '项目管理' }
        ],
        '试验生产域': [
          { label: '元器件试验检测', value: '元器件试验检测' },
          { label: '基础产品试验检测', value: '基础产品试验检测' },
          { label: '测试性试验检测', value: '测试性试验检测' },
          { label: '环境与可靠性试验', value: '环境与可靠性试验' },
          { label: '电磁兼容性试验检测', value: '电磁兼容性试验检测' },
          { label: '软件测试评价', value: '软件测试评价' }
        ],
        '运行支撑域': [
          { label: 'CBB管理', value: 'CBB管理' },
          { label: '固定资产管理', value: '固定资产管理' },
          { label: '档案管理', value: '档案管理' },
          { label: '知识产权与成果管理', value: '知识产权与成果管理' },
          { label: '知识管理', value: '知识管理' },
          { label: '科技情报管理', value: '科技情报管理' },
          { label: '规章制度管理', value: '规章制度管理' },
          { label: '设备管理', value: '设备管理' }
        ]
      }
    }
  },
  computed: {
    subScenarioTree() {
      const scenarios = this.structuredSubScenarios[this.assetFormData.applicationScenario] || []
      return scenarios.map(item => ({ label: item.label, value: item.value }))
    },
    techSystemTree() {
      return [
        {
          label: '标准化',
          value: '标准化',
          children: [
            { label: '标准化共性', value: '标准化-标准化共性' },
            { label: '机载系统标准化', value: '标准化-机载系统标准化' },
            { label: '制造工程标准化', value: '标准化-制造工程标准化' },
            { label: '标准化政策与管理', value: '标准化-标准化政策与管理' }
          ]
        },
        {
          label: '专业工程',
          value: '专业工程',
          children: [
            { label: '人机工程', value: '专业工程-人机工程' },
            { label: '测试性与PHM', value: '专业工程-测试性与PHM' }
          ]
        },
        {
          label: '质量工程',
          value: '质量工程',
          children: [
            { label: '宏观质量评价', value: '质量工程-宏观质量评价' },
            { label: '产品质量保证', value: '质量工程-产品质量保证' }
          ]
        },
        {
          label: '数据与软件工程',
          value: '数据与软件工程',
          children: [
            { label: '软件测试', value: '数据与软件工程-软件测试' },
            { label: '人工智能', value: '数据与软件工程-人工智能' },
            { label: '软件开发', value: '数据与软件工程-软件开发' }
          ]
        },
        {
          label: '本质安全与适航',
          value: '本质安全与适航'
        },
        {
          label: '基础产品',
          value: '基础产品',
          children: [
            { label: '基础产品型谱构建', value: '基础产品-基础产品型谱构建' },
            { label: '基础产品检测', value: '基础产品-基础产品检测' }
          ]
        }
      ]
    }
  },
  created() {
    this.loadSpaceUsers()
  },
  methods: {
    show() {
      this.resetForm()
      this.currentStep = 0
      this.assetFormData.assetType = this.activeNav
    },
    handleClose() {
      this.$emit('close')
      this.resetForm()
    },
    resetForm() {
      this.step1Form.resetFields()
      this.step2Form.resetFields()
      this.step3Form.resetFields()
      this.step4Form.resetFields()
      this.assetFormData = {
        assetType: this.activeNav,
        dataStructure: '',
        applicationScenario: '',
        subScenario: '',
        techSystem: '',
        dataVolumeSlider: 0,
        certGenerated: false,
        certPath: ''
      }
      this.requestParams = []
      this.currentStep = 0
    },
    loadSpaceUsers() {
      getAction('/spaceUser/listBySpaceId', { spaceId: this.spaceInfo.id, pageSize: 100 }).then(res => {
        if (res.success) {
          this.spaceUsers = res.result.records || res.result || []
        }
      })
    },
    handleAssetTypeChange(value) {
      this.assetFormData.assetType = value
    },
    handleDataStructureChange(value) {
      this.assetFormData.dataStructure = value
      this.assetFormData.applicationScenario = ''
      this.assetFormData.subScenario = ''
      this.assetFormData.techSystem = ''
    },
    handleScenarioChange(value) {
      this.assetFormData.applicationScenario = value
      this.assetFormData.subScenario = ''
    },
    handleAddParam() {
      this.requestParams.push({ key: Date.now(), paramName: '', paramType: 'string', required: false, description: '' })
    },
    handleDeleteParam(record) {
      this.requestParams = this.requestParams.filter(item => item.key !== record.key)
    },
    handleNextStep() {
      if (this.currentStep === 0) {
        this.step1Form.validateFields(['assetType'], (err) => {
          if (!err) this.currentStep++
        })
      } else if (this.currentStep === 1) {
        this.step2Form.validateFields((err) => {
          if (!err) this.currentStep++
        })
      } else if (this.currentStep === 2) {
        this.step3Form.validateFields(['ownerId'], (err) => {
          if (!err) this.currentStep++
        })
      }
    },
    handlePrevStep() {
      if (this.currentStep > 0) this.currentStep--
    },
    handleGenerateCert() {
      this.certGenerating = true
      setTimeout(() => {
        this.assetFormData.certPath = '/cert_' + Date.now() + '.png'
        this.assetFormData.certGenerated = true
        this.certGenerating = false
      }, 1500)
    },
    handleSubmit() {
      this.step4Form.validateFields(['approverId'], (err, values) => {
        if (err) return
        const step2Values = this.step2Form.getFieldsValue()
        const step3Values = this.step3Form.getFieldsValue()
        const data = {
          spaceId: this.spaceInfo.id,
          assetType: this.assetFormData.assetType,
          assetName: step2Values.assetName,
          assetDesc: step2Values.assetDesc,
          updateFrequency: step2Values.updateFrequency,
          dataLevel: step2Values.dataLevel,
          dataStructure: step2Values.dataStructure,
          applicationScenario: step2Values.applicationScenario,
          subScenario: step2Values.subScenario,
          techSystem: step2Values.techSystem,
          dataScale: step2Values.dataScale,
          dataVolume: this.formatDataVolume(this.assetFormData.dataVolumeSlider),
          openDegree: step2Values.openDegree,
          operationType: step2Values.operationType,
          dataGranularity: step2Values.dataGranularity,
          apiAddress: step2Values.apiAddress,
          apiMethod: step2Values.apiMethod,
          apiReturnType: step2Values.apiReturnType,
          apiRequestExample: step2Values.apiRequestExample,
          apiSuccessExample: step2Values.apiSuccessExample,
          apiErrorExample: step2Values.apiErrorExample,
          dataFormat: step2Values.dataFormat,
          dataModality: step2Values.dataModality,
          annotationType: step2Values.annotationType,
          trainingStage: step2Values.trainingStage,
          dataSource: step2Values.dataSource,
          ownerId: Array.isArray(step3Values.ownerId) ? step3Values.ownerId[0] : step3Values.ownerId,
          provider: this.spaceInfo.departName,
          providerId: this.spaceInfo.departId,
          status: 'submitted'
        }
        if (this.assetFormData.certGenerated) {
          data.certPath = this.assetFormData.certPath
        }
        if (step2Values.timeRange && step2Values.timeRange.length === 2) {
          data.timeRangeStart = step2Values.timeRange[0].format('YYYY-MM-DD')
          data.timeRangeEnd = step2Values.timeRange[1].format('YYYY-MM-DD')
        }
        addSpaceAsset(data, values.approverId).then(res => {
          if (res.success) {
            this.$message.success('登记申请已提交')
            this.$emit('success')
            this.handleClose()
          } else {
            this.$message.error(res.message || '提交失败')
          }
        })
      })
    },
    handleCancel() {
      this.$confirm({
        title: '提示',
        content: '是否要取消登记数据资产？',
        onOk: () => {
          this.handleClose()
        }
      })
    },
    getAssetTypeText(type) {
      const map = { dataset: '数据集', api: '数据API', training_set: '高质量训练集' }
      return map[type] || type
    },
    formatDataVolume(value) {
      if (value <= 0) return '0K'
      if (value < 500) return value + 'K'
      if (value < 1000) return value + 'G'
      return '1T'
    }
  }
}
</script>

<style scoped>
.registration-steps {
  margin-bottom: 24px;
}

.step-content {
  min-height: 400px;
}

.step-footer {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.info-preview {
  margin-bottom: 16px;
}

.cert-preview {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.cert-box {
  margin-bottom: 16px;
}

.cert-box img {
  max-width: 100%;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.cert-placeholder {
  padding: 40px;
  color: #999;
}
</style>
