<template>
  <div class="global-tree-container">
    <!-- 按钮操作区域 -->
    <div style="margin: 10px;">
      <a-button class="spaced-button" type="primary" @click="showOrHideModel">
        型号显示/隐藏
      </a-button>
    </div>
    
    <div style="background: #fff;padding-left:16px; padding-right: 16px;">
      <!-- 当前选择提示 -->
      <a-alert type="info" :showIcon="true">
        <div slot="message">
          当前选择：<span v-if="currSelected.title">{{ getCurrSelectedTitle() }}</span>
          <a v-if="currSelected.title" style="margin-left: 10px" @click="onClearSelected">取消选择</a>
        </div>
      </a-alert>
      
      <!-- 搜索按钮和搜索框 -->
      <div style="margin: 10px 0;">
        <a-button
          type="primary"
          icon="search"
          @click="toggleSearchBox"
          style="margin-bottom: 10px;"
        >
          {{ showSearchBox ? '隐藏搜索' : '显示搜索' }}
        </a-button>
        <div v-if="showSearchBox" style="margin-top: 10px;">
          <a-input-search
            placeholder="搜索产品目录"
            v-model="searchValue"
            @search="handleSearch"
            style="width: 100%;"
          />
        </div>
      </div>
      
      <!-- 树组件 -->
      <a-tree
        class="custom-tree"
        checkable
        :autoExpandParent="autoExpandParent"
        :checkStrictly="checkStrictly"
        :checkedKeys="checkedKeys"
        :expandedKeys="iExpandedKeys"
        :selectedKeys="selectedKeys"
        :treeData="filteredTreeData.length > 0 ? filteredTreeData : treeData"
        :replaceFields="{key:'id'}"
        @expand="onExpand"
        @rightClick="rightHandle"
        @select="onSelect"
        @check="handleCheck"
      />
    </div>
  </div>
</template>
<script>
import { queryPBSTreeList } from '@api/api'
import { TreeMixin } from '@/mixins/TreeMixin'
import { GlobalTreeStateMixin } from '@/mixins/GlobalTreeStateMixin'
import { getAction } from '@api/manage'
import { EventBus } from '@/Bus'

export default {
  name: 'GlobalTreeComponent',
  mixins: [TreeMixin, GlobalTreeStateMixin],
  data() {
    return {
      treeLevel: 1,
      disableMixinCreated: true,
      showModel: false,
      timer: '',
      searchValue: '',
      filteredTreeData: [],
      originalTreeData: [],
      showSearchBox: false
    }
  },
  created() {
    this.loadTree()
  },
  mounted() {
    this.$nextTick(() => {
      if (this._hasRestoredState && this.nodeId && this.currSelected && this.currSelected.id) {
        this.$emit('nodeChange', this.nodeId, this.currSelected)
      }
    })
  },
  watch: {
    treeData: {
      handler(newVal) {
        // 当treeData变化时，保存原始数据
        if (newVal && newVal.length > 0) {
          this.originalTreeData = JSON.parse(JSON.stringify(newVal));
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    onGlobalTreeStateChanged(treeState) {
      if (this._isRestoringState) {
        return
      }

      const currentTimestamp = (this.currSelected && this.currSelected.timestamp) || 0
      if (treeState.timestamp && treeState.timestamp > currentTimestamp) {
        this._isRestoringState = true

        if (treeState.nodeId) {
          this.nodeId = treeState.nodeId
          this.selectedKeys = [...(treeState.selectedKeys || [])]
          this.currSelected = treeState.currSelected || {}
          this.isLeaf = treeState.isLeaf || false
          this.nodeSelected = true

          if (this.treeData && this.treeData.length > 0) {
            this.selectNodeById(treeState.nodeId)
          }

          this.$emit('nodeChange', this.nodeId, this.currSelected)
        }

        if (treeState.iExpandedKeys && treeState.iExpandedKeys.length > 0) {
          this.iExpandedKeys = [...treeState.iExpandedKeys]
        }

        this._isRestoringState = false
      }
    },
    showOrHideModel() {
      this.showModel = !this.showModel
      this.loadTree()
    },
    // 重写loadTree方法，添加showModel参数
    loadTree() {
      var that = this
      that.treeData = []
      that.catalogTree = []
      queryPBSTreeList({ showModel: this.showModel }).then((res) => {
        if (res.success) {
          this.allTreeKeys = []
          for (let i = 0; i < res.result.length; i++) {
            let temp = res.result[i]
            that.treeData.push(temp)
            that.catalogTree.push(temp)
            // 如果没有保存的展开状态，自动展开所有节点
            if (!this.iExpandedKeys || this.iExpandedKeys.length === 0) {
              that.setThisExpandedKeys(temp)
            }
            that.getAllKeys(temp)
          }
          this.treeData = this.processTreeData(this.treeData, 0, this.id2LevelMap)
          this.loading = false
        }
      })
    },
    getTreeLevel(id) {
      getAction('/akPbsInfo/akPbsInfo/queryById', { id: id }).then(res => {
        this.treeLevel = res.result.secretLevel
        this.timer = new Date().getTime()
      })
    },
    onSelect(selectedKeys, e) {
      this.nodeSelected = true
      this.hiding = false
      let record = e.node.dataRef
      this.nodeId = record.id
      this.getTreeLevel(record.id)
      this.isLeaf = record.isLeaf
      this.currSelected = Object.assign({}, record)
      this.model = this.currSelected
      this.selectedKeys = [record.id]
      this.model.parentId = record.parentid
      this.$emit('nodeChange', this.nodeId, record)
    },
    onClearSelected() {
      this.hiding = true
      this.nodeId = ''
      this.nodeSelected = false
      this.checkedKeys = []
      this.currSelected = {}
      this.selectedKeys = []
      this.timer = new Date().getTime()
      this.$emit('nodeChange', '', {})
    },
    onExpand(expandedKeys) {
      this.iExpandedKeys = expandedKeys
      this.autoExpandParent = false
    },
    handleCheck(checkedKeys) {
      this.checkedKeys = checkedKeys
    },
    // 搜索相关方法
    toggleSearchBox() {
      this.showSearchBox = !this.showSearchBox;
      if (!this.showSearchBox) {
        // 隐藏搜索框时清空搜索值和过滤数据
        this.searchValue = '';
        this.filteredTreeData = [];
      }
    },
    handleSearch(value) {
      if (!value || value.trim() === '') {
        this.filteredTreeData = [];
        return;
      }
      
      // 使用原始数据进行过滤
      const sourceData = this.originalTreeData.length > 0 ? this.originalTreeData : this.treeData;
      this.filteredTreeData = this.filterTreeData(sourceData, value.trim());
    },
    filterTreeData(data, searchValue) {
      if (!searchValue) return data;
      
      const filtered = [];
      
      data.forEach(item => {
        // 检查当前节点是否匹配
        const nodeMatches = item.title && item.title.toLowerCase().includes(searchValue.toLowerCase());
        
        // 检查子节点是否匹配
        let childrenMatch = false;
        let filteredChildren = [];
        
        if (item.children && item.children.length > 0) {
          filteredChildren = this.filterTreeData(item.children, searchValue);
          childrenMatch = filteredChildren.length > 0;
        }
        
        // 如果当前节点匹配或子节点匹配，则保留该节点
        if (nodeMatches || childrenMatch) {
          filtered.push({
            ...item,
            children: childrenMatch ? filteredChildren : (nodeMatches && item.children ? item.children : [])
          });
        }
      });
      
      return filtered;
    }
  }
}
</script>
<style scoped>
.global-tree-container {
  height: 100%;
  overflow-y: auto;
}

.spaced-button {
  margin-bottom: 10px;
}

:deep(.custom-tree .ant-tree-node-selected) {
  background-color: #e6f7ff !important;
  border-radius: 4px !important;
}

:deep(.custom-tree .ant-tree-node-selected:hover) {
  background-color: #bae7ff !important;
}

:deep(.custom-tree .ant-tree-checkbox) {
  margin-right: 8px !important;
}

:deep(.custom-tree .ant-tree-node-content-wrapper) {
  padding: 4px 8px !important;
  margin: 2px 0 !important;
  width: 100% !important;
  box-sizing: border-box !important;
}
</style>