<template>
<div
  class="j-super-query-box"
  style="top:5%;width:98%;max-height:95%;"
  >
    <a-spin :spinning="loading">
      <a-form layout="inline">
        <!--条件区域 start-->
        <a-row>
          <!--固定条件-->
          <a-col :md="12" v-for="(item, index) in queryFixedList" :key="index+'_'">
            <a-row :gutter="8" >
              <a-col :md="6" :xs="24" style="margin-bottom: 12px;">
                <a-tree-select
                  showSearch
                  v-model="item.field"
                  :treeData="fieldTreeData"
                  :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="选择查询字段"
                  allowClear
                  treeDefaultExpandAll
                  :getPopupContainer="node=>node.parentNode"
                  style="width: 100%"
                  @select="(val,option)=>handleSelected(option,item)"
                >
                </a-tree-select>
              </a-col>
              <!-- 条件 -->
              <a-col :md="4" :xs="24" style="margin-bottom: 12px;">
                <a-select placeholder="匹配规则" :value="item.rule" :getPopupContainer="node=>node.parentNode" @change="handleRuleChange(item,$event)">
                  <a-select-option value="like">包含</a-select-option>
                  <a-select-option value="eq">等于</a-select-option>
                  <a-select-option value="right_like">以..开始</a-select-option>
                  <a-select-option value="left_like">以..结尾</a-select-option>
                  <a-select-option value="in">在...中</a-select-option>
                  <a-select-option value="ne">不等于</a-select-option>
                  <a-select-option value="gt">大于</a-select-option>
                  <a-select-option value="ge">大于等于</a-select-option>
                  <a-select-option value="lt">小于</a-select-option>
                  <a-select-option value="le">小于等于</a-select-option>
                </a-select>
              </a-col>
              <!-- 查询值 -->
              <a-col :md="12" :xs="24" style="margin-bottom: 12px;">
                <!-- 下拉搜索 -->
                <j-search-select-tag v-if="item.type==='sel_search'" v-model="item.val" :dict="getDictInfo(item)" placeholder="请选择"/>
                <!-- 下拉框 -->
                <j-search-select-tag v-else-if="item.type==='list' && item.dictTable" v-model="item.val" :dict="getDictInfo(item)" placeholder="请选择"/>
                <!-- 下拉多选 -->
                <template v-else-if="item.type==='list_multi'">
                  <j-multi-select-tag v-if="item.options" v-model="item.val" :options="item.options" placeholder="请选择"/>
                  <j-multi-select-tag v-else v-model="item.val" :dictCode="getDictInfo(item)" placeholder="请选择"/>
                </template>

                <template v-else-if="item.dictCode">
                  <template v-if="item.type === 'table-dict'">
                    <j-popup
                      v-model="item.val"
                      :code="item.dictTable"
                      :field="item.dictCode"
                      :orgFields="item.dictCode"
                      :destFields="item.dictCode"
                      :multi="true"
                    ></j-popup>
                  </template>
                  <template v-else>
                    <j-multi-select-tag v-show="allowMultiple(item)" v-model="item.val" :dictCode="item.dictCode" placeholder="请选择"/>
                    <j-dict-select-tag v-show="!allowMultiple(item)" v-model="item.val" :dictCode="item.dictCode" placeholder="请选择"/>
                  </template>
                </template>
                <j-popup
                  v-else-if="item.type === 'popup'"
                  :value="item.val"
                  v-bind="item.popup"
                  group-id="superQuery"
                  @input="(e,v)=>handleChangeJPopup(item,e,v)"
                  :multi="true"/>
                <j-select-multi-user
                  v-else-if="item.type === 'select-user' || item.type === 'sel_user'"
                  v-model="item.val"
                  :buttons="false"
                  :multiple="allowMultiple(item)"
                  placeholder="请选择用户"
                  :returnKeys="['id', item.customReturnField || 'username']"
                />
                <j-select-depart
                  v-else-if="item.type === 'select-depart' || item.type === 'sel_depart'"
                  v-model="item.val"
                  :multi="allowMultiple(item)"
                  placeholder="请选择部门"
                  :customReturnField="item.customReturnField || 'id'"
                />
                <a-select
                  v-else-if="item.options instanceof Array"
                  v-model="item.val"
                  :options="item.options"
                  allowClear
                  placeholder="请选择"
                  :mode="allowMultiple(item)?'multiple':''"
                />
                <j-area-linkage v-model="item.val" v-else-if="item.type==='area-linkage' || item.type==='pca'" style="width: 100%"/>
                <j-date v-else-if=" item.type=='date' " v-model="item.val" placeholder="请选择日期" style="width: 100%"></j-date>
                <j-date v-else-if=" item.type=='datetime' " v-model="item.val" placeholder="请选择时间" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"></j-date>
                <a-time-picker v-else-if="item.type==='time'" :value="item.val ? moment(item.val,'HH:mm:ss') : null" format="HH:mm:ss" style="width: 100%" @change="(time,value)=>item.val=value"/>
                <a-input-number v-else-if=" item.type=='int'||item.type=='number' " style="width: 100%" placeholder="请输入数值" v-model="item.val"/>
                <a-select v-else-if="item.type=='switch'" placeholder="请选择" v-model="item.val">
                  <a-select-option value="Y">是</a-select-option>
                  <a-select-option value="N">否</a-select-option>
                </a-select>
                <a-input v-else v-model="item.val" placeholder="请输入值"/>
              </a-col>
              <!--增加-->
              <a-col v-if="(index == queryFixedList.length-1) && (addSize <= 4)" :md="2" :xs="4" style="margin-bottom: 12px;">
                <a-button @click="handleAdd" icon="plus"></a-button>&nbsp;
              </a-col>
            </a-row>
          </a-col>
          <!--其他可选择条件-->
          <a-col :md="12" v-for="(item, index) in queryParamsModel" :key="index">
            <a-row :gutter="8" >
              <!-- 字段选择 -->
              <a-col :md="6" :xs="24" style="margin-bottom: 12px;">
                <a-tree-select
                  showSearch
                  v-model="item.field"
                  :treeData="fieldTreeData"
                  :dropdownStyle="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="选择查询字段"
                  allowClear
                  treeDefaultExpandAll
                  :getPopupContainer="node=>node.parentNode"
                  style="width: 100%"
                  @select="(val,option)=>handleSelected(option,item)"
                >
                </a-tree-select>
              </a-col>
              <!-- 条件 -->
              <a-col :md="4" :xs="24" style="margin-bottom: 12px;">
                <a-select placeholder="匹配规则" :value="item.rule" :getPopupContainer="node=>node.parentNode" @change="handleRuleChange(item,$event)">
                  <a-select-option value="like">包含</a-select-option>
                  <a-select-option value="eq">等于</a-select-option>
                  <a-select-option value="right_like">以..开始</a-select-option>
                  <a-select-option value="left_like">以..结尾</a-select-option>
                  <a-select-option value="in">在...中</a-select-option>
                  <a-select-option value="ne">不等于</a-select-option>
                  <a-select-option value="gt">大于</a-select-option>
                  <a-select-option value="ge">大于等于</a-select-option>
                  <a-select-option value="lt">小于</a-select-option>
                  <a-select-option value="le">小于等于</a-select-option>
                </a-select>
              </a-col>
              <!-- 查询值 -->
              <a-col :md="12" :xs="24" style="margin-bottom: 12px;">
                <!-- 下拉搜索 -->
                <j-search-select-tag v-if="item.type==='sel_search'" v-model="item.val" :dict="getDictInfo(item)" placeholder="请选择"/>
                <!-- 下拉框 -->
                <j-search-select-tag v-else-if="item.type==='list' && item.dictTable" v-model="item.val" :dict="getDictInfo(item)" placeholder="请选择"/>
                <!-- 下拉多选 -->
                <template v-else-if="item.type==='list_multi'">
                  <j-multi-select-tag v-if="item.options" v-model="item.val" :options="item.options" placeholder="请选择"/>
                  <j-multi-select-tag v-else v-model="item.val" :dictCode="getDictInfo(item)" placeholder="请选择"/>
                </template>

                <template v-else-if="item.dictCode">
                  <template v-if="item.type === 'table-dict'">
                    <j-popup
                      v-model="item.val"
                      :code="item.dictTable"
                      :field="item.dictCode"
                      :orgFields="item.dictCode"
                      :destFields="item.dictCode"
                      :multi="true"
                    ></j-popup>
                  </template>
                  <template v-else>
                    <j-multi-select-tag v-show="allowMultiple(item)" v-model="item.val" :dictCode="item.dictCode" placeholder="请选择"/>
                    <j-dict-select-tag v-show="!allowMultiple(item)" v-model="item.val" :dictCode="item.dictCode" placeholder="请选择"/>
                  </template>
                </template>
                <j-popup
                  v-else-if="item.type === 'popup'"
                  :value="item.val"
                  v-bind="item.popup"
                  group-id="superQuery"
                  @input="(e,v)=>handleChangeJPopup(item,e,v)"
                  :multi="true"/>
                <j-select-multi-user
                  v-else-if="item.type === 'select-user' || item.type === 'sel_user'"
                  v-model="item.val"
                  :buttons="false"
                  :multiple="allowMultiple(item)"
                  placeholder="请选择用户"
                  :returnKeys="['id', item.customReturnField || 'username']"
                />
                <j-select-depart
                  v-else-if="item.type === 'select-depart' || item.type === 'sel_depart'"
                  v-model="item.val"
                  :multi="allowMultiple(item)"
                  placeholder="请选择部门"
                  :customReturnField="item.customReturnField || 'id'"
                />
                <a-select
                  v-else-if="item.options instanceof Array"
                  v-model="item.val"
                  :options="item.options"
                  allowClear
                  placeholder="请选择"
                  :mode="allowMultiple(item)?'multiple':''"
                />
                <j-area-linkage v-model="item.val" v-else-if="item.type==='area-linkage' || item.type==='pca'" style="width: 100%"/>
                <j-date v-else-if=" item.type=='date' " v-model="item.val" placeholder="请选择日期" style="width: 100%"></j-date>
                <j-date v-else-if=" item.type=='datetime' " v-model="item.val" placeholder="请选择时间" :show-time="true" date-format="YYYY-MM-DD HH:mm:ss" style="width: 100%"></j-date>
                <a-time-picker v-else-if="item.type==='time'" :value="item.val ? moment(item.val,'HH:mm:ss') : null" format="HH:mm:ss" style="width: 100%" @change="(time,value)=>item.val=value"/>
                <a-input-number v-else-if=" item.type=='int'||item.type=='number' " style="width: 100%" placeholder="请输入数值" v-model="item.val"/>
                <a-select v-else-if="item.type=='switch'" placeholder="请选择" v-model="item.val">
                  <a-select-option value="Y">是</a-select-option>
                  <a-select-option value="N">否</a-select-option>
                </a-select>
                <a-input v-else v-model="item.val" placeholder="请输入值"/>
              </a-col>
              <!--增加删除-->
              <a-col :md="2" :xs="4" style="margin-bottom: 12px;">
                <a-button @click="handleDel( index )" icon="minus"></a-button>
              </a-col>
            </a-row>
          </a-col>
        </a-row>
        <!--条件区域 查询重置按钮-->
        <a-row type="flex" justify="end">
          <a-col :md="12" :xs="12">
            <a-form-item label="过滤条件匹配" :labelCol="{md: 6,xs:24}" :wrapperCol="{md: 18,xs:24}" style="width: 100%;">
              <a-select v-model="matchType" :getPopupContainer="node=>node.parentNode" style="width: 65%;">
                <a-select-option value="and">AND（所有条件都要求匹配）</a-select-option>
                <a-select-option value="or">OR（条件中的任意一个匹配）</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="12" :xs="12" style="text-align: right;">
            <a-dropdown v-if="selectedRowKeys.length > 0">
              <a-button type="primary" @click="selectedStdOk" style="margin-right: 5px;">确认选择 <a-icon type="caret-down" /></a-button>
            </a-dropdown>
            <a-button :loading="loading" type="primary" @click="handleOk" icon="search">查询</a-button>
            <a-button :loading="loading" @click="handleReset" style="margin-left: 5px;" icon="reload">重置</a-button>
          </a-col>
        </a-row>
        <!--条件区域 end-->
      </a-form>
    </a-spin>
</div>
</template>

<script>
  import moment from 'moment'
  import * as utils from '@/utils/util'
  import { mixinDevice } from '@/utils/mixin'
  import JDate from '@/components/jeecg/JDate.vue'
  import JSelectDepart from '@/components/jeecgbiz/JSelectDepart'
  import JSelectMultiUser from '@/components/jeecgbiz/JSelectMultiUser'
  import JMultiSelectTag from '@/components/dict/JMultiSelectTag'
  import JAreaLinkage from '@comp/jeecg/JAreaLinkage'

  export default {
    name: 'CustomStandardQuery',
    mixins: [mixinDevice],
    components: { JAreaLinkage, JMultiSelectTag, JDate, JSelectDepart, JSelectMultiUser },
    props: {
      fieldList: {
        type: Array,
        required: true
      },
      /*固定查询条件*/
      fixedFieldList: {
        type: Array,
        required: false
      },
      /*
      * 这个回调函数接收一个数组参数 即查询条件
      * */
      callback: {
        type: String,
        required: false,
        default: 'handleSuperQuery'
      },

      // 当前是否在加载中
      loading: {
        type: Boolean,
        default: false
      },
      //是否允许查询空条件
      searchNull:{
        type: Boolean,
        required: false,
        default: false
      },
      //已选择的值，用于在执行查询中使用
      selectedRowKeys:{
        type: Array,
        required: false,
        default: ()=> []
      },

    },
    data() {
      return {
        moment,
        addSize: 1, //增加的查询条件个数
        fieldTreeData: [],
        queryParamsModel: [],
        queryFixedList:[], //用于存储初始值，重置按钮用
        treeIcon: <a-icon type="file-text"/>,
        // 查询类型，过滤条件匹配（and、or）
        matchType: 'and',
      }
    },
    computed: {

    },
    watch: {
      fieldList: {
        deep: true,
        immediate: true,
        handler(val) {
          let mainData = [], subData = []
          val.forEach(item => {
            let data = { ...item }
            data.label = data.label || data.text
            let hasChildren = (data.children instanceof Array)
            data.disabled = hasChildren
            data.selectable = !hasChildren
            if (hasChildren) {
              data.children = data.children.map(item2 => {
                let child = { ...item2 }
                child.label = child.label || child.text
                child.label = data.label + '-' + child.label
                // update--begin--author:sunjianlei-----date:20220121------for：【JTC-1167】【表单设计器】高级查询，一对一字段查询不好使
                // 是否仅包含字段名，不需要拼接子表表名
                if (!data.onlyFieldName) {
                  child.value = data.value + ',' + child.value
                }
                // update--end--author:sunjianlei-----date:20220121------for：【JTC-1167】【表单设计器】高级查询，一对一字段查询不好使
                child.val = ''
                return child
              })
              data.val = ''
              subData.push(data)
            } else {
              mainData.push(data)
            }
          })
          this.fieldTreeData = mainData.concat(subData)
        }
      }
    },
    mounted() {
      this.initFixedList();
    },
    methods: {
      getDictInfo(item) {
        let str = ''
        if(!item.dictTable){
          str = item.dictCode
        }else{
          str = item.dictTable+','+item.dictText+','+item.dictCode
        }
        console.log('高级查询字典信息',str)
        return str
      },
      handleOk() {
        if (!this.isNullArray(this.queryParamsModel) || !this.isNullValArray(this.queryFixedList) || this.searchNull) {
          var dynamicParams = this.removeEmptyObject(this.queryParamsModel);
          var fixedParams = this.removeEmptyObject(this.queryFixedList);
          let queryParams = fixedParams.concat(dynamicParams);//合并查询参数

          let event = {
            matchType: this.matchType,
            params: queryParams
          }
          // 移动端模式下关闭弹窗
          if (this.izMobile) {
            this.visible = false
          }
          this.emitCallback(event)
        } else {
          this.$message.warn("不能查询空条件")
        }
      },
      emitCallback(event = {}, loadStatus = true) {
        let { params = [], matchType = this.matchType } = event
        for (let param of params) {
          if (Array.isArray(param.val)) {
            param.val = param.val.join(',')
          }
        }
        console.debug('---高级查询参数--->', { params, matchType })
        this.$emit(this.callback, params, matchType, loadStatus)
      },
      handleAdd() {
        this.addNewLine()
      },
      addNewLine() {
        this.queryParamsModel.push({ rule: 'eq' })
        this.addSize ++;
      },
      resetLine() {
        this.queryParamsModel = []
        this.addSize = 1;
        // this.addNewLine()
      },
      resetFixedVal(){
        this.queryFixedList = utils.cloneObject(this.fixedFieldList);
      },
      handleDel(index) {
        this.queryParamsModel.splice(index, 1)
        this.addSize --;
      },
      handleSelected(node, item) {
        let { type, dbType, options, dictCode, dictTable, dictText, customReturnField, popup } = node.dataRef
        item['type'] = type
        item['dbType'] = dbType
        item['options'] = options
        item['dictCode'] = dictCode
        item['dictTable'] = dictTable
        item['dictText'] = dictText
        item['customReturnField'] = customReturnField
        if (popup) {
          item['popup'] = popup
        }
        this.$set(item, 'val', undefined)
      },
      handleOutReset(loadStaus=true) {
        this.resetLine()
        this.emitCallback({}, loadStaus)
      },
      handleReset() {
        this.resetLine()
        this.resetFixedVal();

        var dynamicParams = this.removeEmptyObject(this.queryParamsModel);
        var fixedParams = this.removeEmptyObject(this.queryFixedList);
        let queryParams = fixedParams.concat(dynamicParams);//合并查询参数
        let event = {
          matchType: this.matchType,
          params: queryParams
        }
        this.emitCallback(event)
      },
      handleTreeSelect(idx, event) {
        if (event.selectedNodes[0]) {
          let { matchType, records } = event.selectedNodes[0].data.props
          // 将保存的matchType取出，兼容旧数据，如果没有保存就还是使用原来的
          this.matchType = matchType || this.matchType
          this.queryParamsModel = utils.cloneObject(records)
        }
      },
      isNullArray(array) {
        //判断是不是空数组对象
        if (!array || array.length === 0) {
          return true
        }
        if (array.length === 1) {
          let obj = array[0]
          if (!obj.field || (obj.val == null || obj.val === '') || !obj.rule) {
            return true
          }
        }
        return false
      },
      //判断数组的值是否为空-用于固定条件的判断
      isNullValArray(array){
        if(this.isNullArray(array)){
          return;
        }
        var isNullVal = true;
        for(var i=0; i < array.length; i++){
          let obj = array[i];
          if(obj.val != null && obj.val != ''){
            isNullVal = false;
            break;
          }
        }
        return isNullVal;
      },
      /** 判断是否允许多选 */
      allowMultiple(item) {
        return item.rule === 'in'
      },

      handleRuleChange(item, newValue) {
        let oldValue = item.rule
        this.$set(item, 'rule', newValue)
        // 上一个规则是否是 in，且type是字典或下拉
        if (oldValue === 'in') {
          if (item.dictCode || item.options instanceof Array) {
            let value = item.val
            if (typeof item.val === 'string') {
              value = item.val.split(',')[0]
            } else if (Array.isArray(item.val)) {
              value = item.val[0]
            }
            this.$set(item, 'val', value)
          }
        }
      },
      // 去掉数组中的空对象
      removeEmptyObject(arr) {
        let array = utils.cloneObject(arr)
        for (let i = 0; i < array.length; i++) {
          let item = array[i]
          if (item == null || Object.keys(item).length <= 0) {
            array.splice(i--, 1)
          } else {
            if (Array.isArray(item.options)) {
              // 如果有字典属性，就不需要保存 options 了
              //update-begin-author:taoyan date:20200819 for:【开源问题】 高级查询 下拉框作为并且选项很多多多 LOWCOD-779
              delete item.options
              //update-end-author:taoyan date:20200819 for:【开源问题】 高级查询 下拉框作为并且选项很多多多 LOWCOD-779
            }
          }
        }
        return array
      },

      handleChangeJPopup(item, e, values) {
        item.val = values[item.popup['destFields']]
      },
      initFixedList(){
        if(this.fixedFieldList){
          this.queryFixedList = utils.cloneObject(this.fixedFieldList);
        }
      },
      //确认选择
      selectedStdOk(){
        this.$emit('selectedStdOk');
      }

    }
  }
</script>

<style lang="less" scoped>

  .j-super-query-box {
    display: inline-block;
  }

  .j-super-query-modal {

    .j-super-query-history-card {
      /deep/ .ant-card-body,
      /deep/ .ant-card-head-title {
        padding: 0;
      }

      /deep/ .ant-card-head {
        padding: 4px 8px;
        min-height: initial;
      }
    }

    .j-super-query-history-empty {
      /deep/ .ant-empty-image {
        height: 80px;
        line-height: 80px;
        margin-bottom: 0;
      }

      /deep/ img {
        width: 80px;
        height: 65px;
      }

      /deep/ .ant-empty-description {
        color: #afafaf;
        margin: 8px 0;
      }
    }

    .j-super-query-history-tree {

      .j-history-tree-title {
        width: calc(100% - 24px);
        position: relative;
        display: inline-block;

        &-closer {
          color: #999999;
          position: absolute;
          top: 0;
          right: 0;
          width: 24px;
          height: 24px;
          text-align: center;
          opacity: 0;
          transition: opacity 0.3s, color 0.3s;

          &:hover {
            color: #666666;
          }

          &:active {
            color: #333333;
          }
        }

        &:hover {
          .j-history-tree-title-closer {
            opacity: 1;
          }
        }

      }

      /deep/ .ant-tree-switcher {
        display: none;
      }

      /deep/ .ant-tree-node-content-wrapper {
        width: 100%;
      }
    }

  }

</style>