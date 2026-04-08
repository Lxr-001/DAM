package org.jeecg.modules.chainproductcatalogtree.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.chainproductcatalogtree.entity.ChainProductCatalogTree;

import java.util.List;

/**
 * @Description: 产品目录树结构表
 * @Author: jeecg-boot
 * @Date: 2024-02-29
 * @Version: V1.0
 */
public interface IChainProductCatalogTreeService extends IService<ChainProductCatalogTree> {

    List<ChainProductCatalogTree> queryTreeList(String ids);

    List<Tree<String>> queryTreeList();
}
