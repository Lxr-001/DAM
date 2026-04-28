import { EventBus } from '@/Bus'

const STORAGE_KEY = 'global_pbs_tree_state'

export const GlobalTreeStateMixin = {
  data() {
    return {
      _isRestoringState: false,
      _hasRestoredState: false
    }
  },
  mounted() {
    EventBus.$on('global-tree-node-selected', this.onGlobalTreeStateChanged)
    EventBus.$on('global-tree-state-cleared', this.onGlobalTreeStateCleared)

    if (this.onSelect && typeof this.onSelect === 'function') {
      this._originalOnSelect = this.onSelect.bind(this)
      this.onSelect = (selectedKeys, e) => {
        if (!this._isRestoringState) {
          this._originalOnSelect(selectedKeys, e)
        }
        this.$nextTick(() => {
          this.saveGlobalTreeState()
        })
      }
    }

    if (this.onClearSelected && typeof this.onClearSelected === 'function') {
      this._originalOnClearSelected = this.onClearSelected.bind(this)
      this.onClearSelected = () => {
        if (!this._isRestoringState) {
          this._originalOnClearSelected()
        }
        this.clearGlobalTreeState()
      }
    }

    this.$nextTick(() => {
      if (this.treeData && this.treeData.length > 0) {
        this.restoreGlobalTreeState()
      } else {
        this.$watch('treeData', (newVal) => {
          if (newVal && newVal.length > 0 && !this._hasRestoredState) {
            this._hasRestoredState = true
            this.$nextTick(() => {
              this.restoreGlobalTreeState()
            })
          }
        }, { immediate: true })
      }
    })
  },
  beforeDestroy() {
    EventBus.$off('global-tree-node-selected', this.onGlobalTreeStateChanged)
    EventBus.$off('global-tree-state-cleared', this.onGlobalTreeStateCleared)
  },
  methods: {
    saveGlobalTreeState() {
      if (this._isRestoringState) {
        return
      }

      const treeState = {
        nodeId: this.nodeId || '',
        selectedKeys: this.selectedKeys || [],
        checkedKeys: Array.isArray(this.checkedKeys) ? this.checkedKeys : (this.checkedKeys && this.checkedKeys.checked ? this.checkedKeys.checked : []),
        iExpandedKeys: this.iExpandedKeys || [],
        currSelected: this.currSelected || {},
        isLeaf: this.isLeaf || false,
        timestamp: new Date().getTime()
      }

      try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(treeState))
        EventBus.$emit('global-tree-node-selected', treeState)
      } catch (error) {
        console.error('保存全局树状态失败:', error)
      }
    },

    restoreGlobalTreeState() {
      try {
        const savedState = localStorage.getItem(STORAGE_KEY)
        if (!savedState) {
          return
        }

        const treeState = JSON.parse(savedState)
        if (!treeState || !treeState.nodeId) {
          return
        }

        this._isRestoringState = true

        if (treeState.iExpandedKeys && treeState.iExpandedKeys.length > 0) {
          this.iExpandedKeys = [...treeState.iExpandedKeys]
        }

        if (treeState.selectedKeys && treeState.selectedKeys.length > 0) {
          this.selectedKeys = [...treeState.selectedKeys]
        }

        if (treeState.checkedKeys && treeState.checkedKeys.length > 0) {
          if (Array.isArray(this.checkedKeys)) {
            this.checkedKeys = [...treeState.checkedKeys]
          } else {
            this.checkedKeys = { checked: [...treeState.checkedKeys] }
          }
        }

        if (treeState.nodeId) {
          this.nodeId = treeState.nodeId
          this.currSelected = treeState.currSelected || {}
          this.isLeaf = treeState.isLeaf || false
          this.nodeSelected = !!treeState.nodeId

          this.$nextTick(() => {
            if (this.treeData && this.treeData.length > 0 && treeState.nodeId) {
              this.selectNodeById(treeState.nodeId)
            }
          })
        }

        this._isRestoringState = false
      } catch (error) {
        console.error('恢复全局树状态失败:', error)
        this._isRestoringState = false
      }
    },

    selectNodeById(nodeId) {
      if (!this.treeData || this.treeData.length === 0) {
        return
      }

      const findNode = (nodes, targetId) => {
        for (const node of nodes) {
          if (node.id === targetId) {
            return node
          }
          if (node.children && node.children.length > 0) {
            const found = findNode(node.children, targetId)
            if (found) return found
          }
        }
        return null
      }

      const targetNode = findNode(this.treeData, nodeId)
      if (targetNode) {
        this.expandParentNodes(targetNode.id)

        this.$nextTick(() => {
          if (this._originalOnSelect) {
            this._isRestoringState = true
            this._originalOnSelect([targetNode.id], { node: { dataRef: targetNode, key: targetNode.id } })
            this._isRestoringState = false
          } else if (typeof this.onSelect === 'function') {
            this._isRestoringState = true
            this.onSelect([targetNode.id], { node: { dataRef: targetNode, key: targetNode.id } })
            this._isRestoringState = false
          }
        })
      }
    },

    expandParentNodes(nodeId) {
      if (!this.treeData || this.treeData.length === 0) {
        return
      }

      const findParentPath = (nodes, targetId, path = []) => {
        for (const node of nodes) {
          const currentPath = [...path, node.id]
          if (node.id === targetId) {
            return currentPath
          }
          if (node.children && node.children.length > 0) {
            const found = findParentPath(node.children, targetId, currentPath)
            if (found) return found
          }
        }
        return null
      }

      const parentPath = findParentPath(this.treeData, nodeId)
      if (parentPath && parentPath.length > 0) {
        const keysToExpand = parentPath.slice(0, -1)
        if (keysToExpand.length > 0) {
          this.iExpandedKeys = [...new Set([...this.iExpandedKeys, ...keysToExpand])]
        }
      }
    },

    clearGlobalTreeState() {
      try {
        localStorage.removeItem(STORAGE_KEY)
        EventBus.$emit('global-tree-state-cleared')
      } catch (error) {
        console.error('清除全局树状态失败:', error)
      }
    },

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
        }

        if (treeState.iExpandedKeys && treeState.iExpandedKeys.length > 0) {
          this.iExpandedKeys = [...treeState.iExpandedKeys]
        }

        this._isRestoringState = false
      }
    },

    onGlobalTreeStateCleared() {
      if (this._isRestoringState) {
        return
      }

      if (this._originalOnClearSelected) {
        this._isRestoringState = true
        this._originalOnClearSelected()
        this._isRestoringState = false
      } else {
        this.nodeId = ''
        this.selectedKeys = []
        this.currSelected = {}
        this.nodeSelected = false
      }
    }
  }
}
