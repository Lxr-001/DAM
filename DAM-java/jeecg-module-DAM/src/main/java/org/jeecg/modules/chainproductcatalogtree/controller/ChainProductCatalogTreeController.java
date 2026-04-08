package org.jeecg.modules.chainproductcatalogtree.controller;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.StringHelper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.chainproductcatalogtree.entity.ChainProductCatalogTree;
import org.jeecg.modules.chainproductcatalogtree.service.IChainProductCatalogTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 产品目录树结构表
 * @Author: jeecg-boot
 * @Date: 2024-02-29
 * @Version: V1.0
 */
@Api(tags = "产品目录树结构表")
@RestController
@RequestMapping("/chainProductCatalogTree")
@Slf4j
public class ChainProductCatalogTreeController extends JeecgController<ChainProductCatalogTree, IChainProductCatalogTreeService> {
    @Autowired
    private IChainProductCatalogTreeService chainProductCatalogTreeService;

    /**
     * 查询数据 查出所有产品目录,并以树结构数据格式响应给前端
     *
     * @return
     */
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<List<Tree<String>>> queryTreeList(@RequestParam(name = "ids", required = false) String ids) {
        Result<List<Tree<String>>> result = new Result<>();
        try {
            List<Tree<String>> list = chainProductCatalogTreeService.queryTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 分页列表查询
     *
     * @param chainProductCatalogTree
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "产品目录树结构表-分页列表查询")
    @ApiOperation(value = "产品目录树结构表-分页列表查询", notes = "产品目录树结构表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ChainProductCatalogTree>> queryPageList(ChainProductCatalogTree chainProductCatalogTree,
                                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                HttpServletRequest req) {
        QueryWrapper<ChainProductCatalogTree> queryWrapper = QueryGenerator.initQueryWrapper(chainProductCatalogTree, req.getParameterMap());
        Page<ChainProductCatalogTree> page = new Page<ChainProductCatalogTree>(pageNo, pageSize);
        IPage<ChainProductCatalogTree> pageList = chainProductCatalogTreeService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param chainProductCatalogTree
     * @return
     */
    //@AutoLog(value = "产品目录树结构表-添加")
    @ApiOperation(value = "产品目录树结构表-添加", notes = "产品目录树结构表-添加")
    //@RequiresPermissions("chainproductcatalogtree:chain_product_catalog_tree:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ChainProductCatalogTree chainProductCatalogTree) {
//		如果 parentid 不为空且对应一级菜单的 isLeaf 为 true 时需要把对应一级菜单的 isLeaf 设置为 false
        String parentId = chainProductCatalogTree.getParentId();
        if (!StringHelper.isNullOrEmptyString(parentId)) {
            QueryWrapper<ChainProductCatalogTree> wrapper = new QueryWrapper<>();
            wrapper.eq("id", parentId);
            List<ChainProductCatalogTree> list = chainProductCatalogTreeService.list(wrapper);
            if (!list.isEmpty()) {
                boolean isLeaf = list.get(0).getIsLeaf();
                if (isLeaf) {
                    list.get(0).setIsLeaf(false);
                    chainProductCatalogTreeService.updateById(list.get(0));
                }
            }
        }
        chainProductCatalogTreeService.save(chainProductCatalogTree);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param chainProductCatalogTree
     * @return
     */
    //@AutoLog(value = "产品目录树结构表-编辑")
    @ApiOperation(value = "产品目录树结构表-编辑", notes = "产品目录树结构表-编辑")
    //@RequiresPermissions("chainproductcatalogtree:chain_product_catalog_tree:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody ChainProductCatalogTree chainProductCatalogTree) {
        chainProductCatalogTreeService.updateById(chainProductCatalogTree);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "产品目录树结构表-通过id删除")
    @ApiOperation(value = "产品目录树结构表-通过id删除", notes = "产品目录树结构表-通过id删除")
    //@RequiresPermissions("chainproductcatalogtree:chain_product_catalog_tree:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
//        先获取
        String id1 = chainProductCatalogTreeService.getById(id).getParentId();
//        再删除，删除是级联删除，如果 id 作为其他数据的 parentId, 先删除对应的其他数据，再删除本条数据
        Map<String, Object> map = new HashMap<>();
        map.put("parent_id", id);
        chainProductCatalogTreeService.removeByMap(map);
        chainProductCatalogTreeService.removeById(id);
//        删除后要进行遍历检查，如果此条数据 parentId 对应的 id 没有作为任何其他数据的 parentId,
//        则应该把 id 对应数据的 isLeaf 字段设置为 true
        List<ChainProductCatalogTree> list = chainProductCatalogTreeService.list();
        if (!StringHelper.isNullOrEmptyString(id1)) {
            List<ChainProductCatalogTree> collect = list.stream().filter(l -> id1.equals(l.getParentId())).collect(Collectors.toList());
            if (collect.isEmpty()) {
                ChainProductCatalogTree tree = chainProductCatalogTreeService.getById(id1);
                tree.setIsLeaf(true);
                chainProductCatalogTreeService.updateById(tree);
            }
        }
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    //@AutoLog(value = "产品目录树结构表-批量删除")
    @ApiOperation(value = "产品目录树结构表-批量删除", notes = "产品目录树结构表-批量删除")
    //@RequiresPermissions("chainproductcatalogtree:chain_product_catalog_tree:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.chainProductCatalogTreeService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "产品目录树结构表-通过id查询")
    @ApiOperation(value = "产品目录树结构表-通过id查询", notes = "产品目录树结构表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<ChainProductCatalogTree> queryById(@RequestParam(name = "id", required = true) String id) {
        ChainProductCatalogTree chainProductCatalogTree = chainProductCatalogTreeService.getById(id);
        if (chainProductCatalogTree == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(chainProductCatalogTree);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param chainProductCatalogTree
     */
    //@RequiresPermissions("chainproductcatalogtree:chain_product_catalog_tree:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ChainProductCatalogTree chainProductCatalogTree) {
        return super.exportXls(request, chainProductCatalogTree, ChainProductCatalogTree.class, "产品目录树结构表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("chainproductcatalogtree:chain_product_catalog_tree:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ChainProductCatalogTree.class);
    }

}
