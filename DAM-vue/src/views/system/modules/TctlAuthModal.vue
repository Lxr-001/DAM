<template>
  <a-card :bordered="false">
    <template v-if="this.departId">
      <a-spin :spinning="loading">
        <a-form>
          <a-form-item label="所拥有的权限">
            <a-tree
              checkable
              :autoExpandParent="autoExpandParent"
              :checkStrictly="checkStrictly"
              :checkedKeys="checkedKeys"
              :expandedKeys="expandedKeysss"
              :selectedKeys="selectedKeys"
              :treeData="treeData"
              :replaceFields="{key:'id'}"
              @expand="onExpand"
              @rightClick="rightHandle"
              @select="onTreeNodeSelect"
              @check="handleCheck"
            />
          </a-form-item>
        </a-form>
      </a-spin>
      <div class="anty-form-btn">
        <a-dropdown style="float: left" :trigger="['click']" placement="topCenter">
          <a-menu slot="overlay">
            <!-- 简化Tree逻辑，使用默认checkStrictly为false的行为，即默认父子关联
            <a-menu-item key="1" @click="switchCheckStrictly(1)">父子关联</a-menu-item>
            <a-menu-item key="2" @click="switchCheckStrictly(2)">取消关联</a-menu-item>
            -->
            <a-menu-item key="3" @click="checkALL">全部勾选</a-menu-item>
            <a-menu-item key="4" @click="cancelCheckALL">取消全选</a-menu-item>
            <a-menu-item key="5" @click="expandAll">展开所有</a-menu-item>
            <a-menu-item key="6" @click="closeAll">合并所有</a-menu-item>
          </a-menu>
          <a-button>
            树操作
            <a-icon type="up" />
          </a-button>
        </a-dropdown>
        <a-button style="float: right" @click="handleSubmit" type="primary" htmlType="button" icon="form">保存
        </a-button>
      </div>
    </template>
    <a-card v-else :bordered="false" style="height:200px">
      <a-empty>
        <span slot="description"> 请先选择一个部门! </span>
      </a-empty>
    </a-card>
    <depart-datarule-modal ref="datarule" />
  </a-card>
</template>

<script>
import { queryTctlNewTreeList1, queryDepartPermission,saveDepartPermission } from '@/api/api'
import DepartDataruleModal from './DepartDataruleModal'
import { TctlTreeMixin } from '@/mixins/TctlTreeMixin'

export default {
  name: 'TctlAuthModal',
  mixins: [TctlTreeMixin],
  components: { DepartDataruleModal },
  data() {
    return {
      departId: '',
      treeData: [],
      defaultCheckedKeys: [],
      checkedKeys: [],
      halfCheckedKeys: [],
      expandedKeysss: [],
      allTreeKeys: [],
      autoExpandParent: true,
      checkStrictly: false,
      title: '同厂同类权限配置',
      visible: false,
      loading: false,
      selectedKeys: [],
      url: {
        // list: '/akPbsInfo/akPbsInfo/list1',
        list: '/akTctlNew/akTctlNew/list',
        delete: '/akTctlNew/akTctlNew/delete',
        deleteBatch: '/akTctlNew/akTctlNew/deleteBatch',
        exportXlsUrl: '/akTctlNew/akTctlNew/exportXls',
        importExcelUrl: 'akTctlNew/akTctlNew/importExcel',
        importGradeUrl: 'akscore/akScore/importExcel'
      }
    }
  },
  methods: {
    onTreeNodeSelect(id) {
      if (id && id.length > 0) {
        this.selectedKeys = id
      }
      this.$refs.datarule.show(this.selectedKeys[0], this.departId)
    },
    onCheck(checkedKeys, { halfCheckedKeys }) {
      // 保存选中的和半选中的，后面保存的时候合并提交
      this.checkedKeys = checkedKeys
      this.halfCheckedKeys = halfCheckedKeys
    },
    show(departId) {
      this.departId = departId
      this.loadData()
      if (this.departId) {
        this.loading = true;
        // 先调用你原有的 loadData 方法加载权限树
        // loadData 成功后，再加载已勾选的权限
        queryDepartPermission({ departId: this.departId,type:1 }).then((checkedRes) => {
          if (checkedRes.success) {
            this.checkedKeys = checkedRes.result;
            this.defaultCheckedKeys = [...checkedRes.result];
          } else {
            this.$message.warning('加载已选权限失败：' + (checkedRes.message || '未知错误'));
            this.checkedKeys = [];
          }
        }).catch(error => {
          console.error('获取部门权限失败:', error);
          this.$message.error('获取部门权限失败，请检查网络或联系管理员。');
          this.checkedKeys = [];
        }).finally(() => {
          this.loading = false; // 无论成功失败，都停止加载
        });
      }
    },
    close() {
      this.reset()
      this.$emit('close')
      this.visible = false
    },
    onExpand(expandedKeys) {
      this.expandedKeysss = expandedKeys
      this.autoExpandParent = false
    },
    reset() {
      this.expandedKeysss = []
      this.checkedKeys = []
      this.defaultCheckedKeys = []
      this.loading = false
    },
    expandAll() {
      this.expandedKeysss = this.allTreeKeys
    },
    closeAll() {
      this.expandedKeysss = []
    },
    checkALL() {
      this.checkedKeys = this.allTreeKeys
    },
    cancelCheckALL() {
      this.checkedKeys = []
    },
    handleCancel() {
      this.close()
    },
    handleSubmit() {
      let that = this
      if (!that.departId) {
        this.$message.warning('请点击选择一个部门!')
      }
      let checkedKeys = [...that.checkedKeys, ...that.halfCheckedKeys]
      const permissionIds = checkedKeys.join(',')
      let params = {
        departId: that.departId,
        type: 1,
        permissionIds,
        lastpermissionIds: that.defaultCheckedKeys.join(',')
      }
      that.loading = true
      saveDepartPermission(params).then((res) => {
        if (res.success) {
          that.$message.success(res.message)
          that.loading = false
          that.loadData()
        } else {
          that.$message.error(res.message)
          that.loading = false
        }
      })
    },
    convertTreeListToKeyLeafPairs(treeList, keyLeafPair = []) {
      for (const { key, isLeaf, children } of treeList) {
        keyLeafPair.push({ key, isLeaf })
        if (children && children.length > 0) {
          this.convertTreeListToKeyLeafPairs(children, keyLeafPair)
        }
      }
      return keyLeafPair
    },
    emptyCurrForm() {
      this.form.resetFields()
    },
    loadData() {
      var that = this
      that.treeData = []
      that.loading = true
      queryTctlNewTreeList1().then((res) => {
        if (res.success) {
          //产品目录全选后，再添加产品目录，选中数量增多
          this.allTreeKeys = []
          for (let i = 0; i < res.result.length; i++) {
            let temp = res.result[i]
            that.treeData.push(temp)
            that.catalogTree.push(temp)
            that.setThisExpandedKeys(temp)
            that.getAllKeys(temp)
          }
          this.treeData = this.processTreeData(this.treeData, 0, this.id2LevelMap)
          this.loading = false
        }
      })
    },
    getAllKeys(node) {
      if (Array.isArray(node)) {
        node.forEach(item => {
          this.allTreeKeys.push(item.id); // 使用 id
          if (item.children && item.children.length > 0) {
            this.getAllKeys(item.children);
          }
        });
      } else if (node) {
        this.allTreeKeys.push(node.id); // 使用 id
        if (node.children && node.children.length > 0) {
          this.getAllKeys(node.children);
        }
      }
    }
  }
}
</script>

<style scoped>

</style>