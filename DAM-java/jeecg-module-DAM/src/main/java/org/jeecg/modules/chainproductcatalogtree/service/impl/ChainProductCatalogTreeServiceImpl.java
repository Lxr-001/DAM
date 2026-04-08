package org.jeecg.modules.chainproductcatalogtree.service.impl;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hibernate.validator.internal.util.StringHelper;
import org.jeecg.modules.chainproductcatalogtree.entity.ChainProductCatalogTree;
import org.jeecg.modules.chainproductcatalogtree.mapper.ChainProductCatalogTreeMapper;
import org.jeecg.modules.chainproductcatalogtree.service.IChainProductCatalogTreeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 产品目录树结构表
 * @Author: jeecg-boot
 * @Date: 2024-02-29
 * @Version: V1.0
 */
@Service
public class ChainProductCatalogTreeServiceImpl extends ServiceImpl<ChainProductCatalogTreeMapper, ChainProductCatalogTree> implements IChainProductCatalogTreeService {

    /**
     * @param ids
     * @return
     */
    @Override
    public List<ChainProductCatalogTree> queryTreeList(String ids) {
        return null;
    }

    /**
     * @return
     */
    @Override
    public List<Tree<String>> queryTreeList() {
        QueryWrapper<ChainProductCatalogTree> productQueryWrapper = new QueryWrapper<>();
        List<ChainProductCatalogTree> catalogTrees = this.list(productQueryWrapper);
        for (ChainProductCatalogTree catalogTree : catalogTrees) {
            String parentId = catalogTree.getParentId();
            if (!StringHelper.isNullOrEmptyString(parentId) && !parentId.equals("0")) {
                ChainProductCatalogTree tree = this.getById(parentId);
                catalogTree.setParent(tree.getTitle());
            } else {
                catalogTree.setParent(catalogTree.getTitle());
            }
        }
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        treeNodeConfig.setIdKey("id"); // 主键
        treeNodeConfig.setDeep(10); //  最大递归深度
        treeNodeConfig.setParentIdKey("parentid");  // 父 id
        // 转换器
        return TreeUtil.build(catalogTrees, "0", treeNodeConfig, (treeNode, tree) -> {
            tree.setId(treeNode.getId());
            tree.setParentId(treeNode.getParentId());
            tree.setName(treeNode.getTitle());
            tree.putExtra("children", treeNode.getChildren());
            // 前端 ui 显示字段
            tree.putExtra("title", treeNode.getTitle());
            tree.putExtra("isLeaf", treeNode.getIsLeaf());
            tree.putExtra("parent", treeNode.getParent());
        });
    }
}
