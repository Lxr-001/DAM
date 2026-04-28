<template>
  <a-radio-group v-if="tagType==='radio'" @change="handleInput" :value="getValueSting" :disabled="disabled">
    <a-radio v-for="(item, key) in dictOptions" :key="key" :value="item.value">{{ item.text }}</a-radio>
  </a-radio-group>

  <a-radio-group v-else-if="tagType==='radioButton'" buttonStyle="solid" @change="handleInput" :value="getValueSting"
                 :disabled="disabled">
    <a-radio-button v-for="(item, key) in dictOptions" :key="key" :value="item.value">{{ item.text }}</a-radio-button>
  </a-radio-group>

  <a-select v-else-if="tagType==='select'" :getPopupContainer="getPopupContainer" :placeholder="placeholder"
            :disabled="disabled" :value="getValueSting" @change="handleInput">
    <a-select-option v-if="showKOption" :value="undefined">请选择</a-select-option>
    <a-select-option v-for="(item, key) in dictOptions" :key="key" :value="item.value">
      <span style="display: inline-block;width: 100%" :title=" item.text || item.label ">
        {{ item.text || item.label }}
      </span>
    </a-select-option>
  </a-select>
</template>

<script>
import { ajaxGetDictItems } from '@/api/api'

export default {
  name: 'JSecretLevelSelect',
  props: {
    maxLevel: {
      type: Number,
      default: 1
    },
    minLevel: {
      type: Number,
      default: 1
    },
    placeholder: String,
    disabled: Boolean,
    value: [String, Number],
    type: String,
    getPopupContainer: {
      type: Function,
      default: (node) => node.parentNode
    },
    showKOption: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      dictCode: 'system_security',
      dictOptions: [{ label: '', value: '1' }],
      tagType: ''
    }
  },
  watch: {
    dictCode: {
      immediate: true,
      handler() {
        this.initDictData()
      }
    }
  },
  created() {
    if (!this.type || this.type === 'list') {
      this.tagType = 'select'
    } else {
      this.tagType = this.type
    }
    //获取字典数据
    // this.initDictData();
  },
  computed: {
    getValueSting() {
      // update-begin author:wangshuai date:20200601 for: 不显示placeholder的文字 ------
      // 当有null或“” placeholder不显示
      return this.value != null ? this.value.toString() : undefined
      // update-end author:wangshuai date:20200601 for: 不显示placeholder的文字 ------
    }
  },
  methods: {
    initDictData() {
      //优先从缓存中读取字典配置
      // if(getDictItemsFromCache(this.dictCode)){
      //   this.dictOptions = getDictItemsFromCache(this.dictCode);
      //   return
      // }

      //根据字典Code, 初始化字典数组
      ajaxGetDictItems(this.dictCode, null).then((res) => {
        if (res.success) {
//                console.log(res.result);
          this.dictOptions = res.result
          if (this.dictCode === 'operate_type') {
            this.dictOptions = this.dictOptions.filter(e => e.value !== '8' && e.value !== '9')
          }
          if (this.dictCode === 'system_security') {
            this.dictOptions = this.dictOptions.filter(e => e.value <= this.maxLevel)
            this.dictOptions = this.dictOptions.filter(e => e.value >= this.minLevel)
          }
        }
      })
    },
    handleInput(e = '') {
      let val
      if (Object.keys(e).includes('target')) {
        val = e.target.value
      } else {
        val = e
      }
      this.$emit('change', val)
      //LOWCOD-2146 【菜单】数据规则，选择自定义SQL 规则值无法输入空格
      this.$emit('input', val)
    },
    setCurrentDictOptions(dictOptions) {
      this.dictOptions = dictOptions
    },
    getCurrentDictOptions() {
      return this.dictOptions
    }
  },
  model: {
    prop: 'value',
    event: 'change'
  }
}
</script>

<style scoped>
</style>