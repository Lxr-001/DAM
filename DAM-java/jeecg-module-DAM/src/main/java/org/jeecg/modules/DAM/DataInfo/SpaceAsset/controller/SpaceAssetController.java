package org.jeecg.modules.DAM.DataInfo.SpaceAsset.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.SpaceAsset;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.service.ISpaceAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 数据资产主表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
@Api(tags = "数据资产")
@RestController
@RequestMapping("/spaceAsset")
@Slf4j
public class SpaceAssetController {

    @Autowired
    private ISpaceAssetService spaceAssetService;

    /**
     * 分页列表查询
     */
    @AutoLog(value = "数据资产-分页列表")
    @ApiOperation(value = "数据资产-分页列表", notes = "数据资产-分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<SpaceAsset>> queryPageList(
            SpaceAsset spaceAsset,
            @ApiParam(value = "空间ID", required = true) @RequestParam(name = "spaceId", required = true) String spaceId,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        QueryWrapper<SpaceAsset> queryWrapper = QueryGenerator.initQueryWrapper(spaceAsset, req.getParameterMap());
        queryWrapper.eq("space_id", spaceId);
        Page<SpaceAsset> page = new Page<>(pageNo, pageSize);
        IPage<SpaceAsset> pageList = spaceAssetService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 根据空间ID和筛选条件查询资产列表
     */
    @AutoLog(value = "数据资产-条件查询")
    @ApiOperation(value = "数据资产-条件查询", notes = "数据资产-条件查询")
    @GetMapping(value = "/queryAssetList")
    public Result<?> queryAssetList(
            @ApiParam(value = "空间ID", required = true) @RequestParam(name = "spaceId", required = true) String spaceId,
            @RequestParam(name = "assetType", required = false) String assetType,
            @RequestParam(name = "dataLevel", required = false) String dataLevel,
            @RequestParam(name = "dataStructure", required = false) String dataStructure,
            @RequestParam(name = "openDegree", required = false) String openDegree,
            @RequestParam(name = "operationType", required = false) String operationType,
            @RequestParam(name = "dataGranularity", required = false) String dataGranularity,
            @RequestParam(name = "dataFormat", required = false) String dataFormat,
            @RequestParam(name = "dataModality", required = false) String dataModality,
            @RequestParam(name = "annotationType", required = false) String annotationType,
            @RequestParam(name = "trainingStage", required = false) String trainingStage,
            @RequestParam(name = "dataSource", required = false) String dataSource,
            @RequestParam(name = "applicationScenario", required = false) String applicationScenario,
            @RequestParam(name = "subScenario", required = false) String subScenario,
            @RequestParam(name = "techSystem", required = false) String techSystem,
            @RequestParam(name = "assetName", required = false) String assetName,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        if (assetType != null && !"".equals(assetType)) params.put("assetType", assetType);
        if (dataLevel != null && !"".equals(dataLevel)) params.put("dataLevel", dataLevel);
        if (dataStructure != null && !"".equals(dataStructure)) params.put("dataStructure", dataStructure);
        if (openDegree != null && !"".equals(openDegree)) params.put("openDegree", openDegree);
        if (operationType != null && !"".equals(operationType)) params.put("operationType", operationType);
        if (dataGranularity != null && !"".equals(dataGranularity)) params.put("dataGranularity", dataGranularity);
        if (dataFormat != null && !"".equals(dataFormat)) params.put("dataFormat", dataFormat);
        if (dataModality != null && !"".equals(dataModality)) params.put("dataModality", dataModality);
        if (annotationType != null && !"".equals(annotationType)) params.put("annotationType", annotationType);
        if (trainingStage != null && !"".equals(trainingStage)) params.put("trainingStage", trainingStage);
        if (dataSource != null && !"".equals(dataSource)) params.put("dataSource", dataSource);
        if (applicationScenario != null && !"".equals(applicationScenario)) params.put("applicationScenario", applicationScenario);
        if (subScenario != null && !"".equals(subScenario)) params.put("subScenario", subScenario);
        if (techSystem != null && !"".equals(techSystem)) params.put("techSystem", techSystem);
        if (assetName != null && !"".equals(assetName)) params.put("assetName", assetName);

        // 直接查全量再分页（筛选条件较复杂，由Mapper处理）
        java.util.List<SpaceAsset> fullList = spaceAssetService.selectAssetListBySpaceId(spaceId, params);
        int total = fullList.size();
        int start = (pageNo - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        java.util.List<SpaceAsset> pageList = start < total ? fullList.subList(start, end) : java.util.Collections.emptyList();

        Page<SpaceAsset> page = new Page<>(pageNo, pageSize);
        page.setTotal(total);
        page.setRecords(pageList);
        return Result.OK(page);
    }

    /**
     * 添加（首次登记）
     */
    @AutoLog(value = "数据资产-首次登记")
    @ApiOperation(value = "数据资产-首次登记", notes = "数据资产-首次登记")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SpaceAsset spaceAsset, @RequestParam(name = "approverId") String approverId) {
        // 校验名称是否重复
        if (spaceAssetService.checkAssetNameDuplicate(spaceAsset.getAssetName(), spaceAsset.getSpaceId())) {
            return Result.error("资产名称已在当前空间内存在");
        }
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (user != null) {
            spaceAsset.setCreateBy(user.getUsername());
        }
        spaceAsset.setCreateTime(new Date());
        boolean ok = spaceAssetService.submitInitialRegistration(spaceAsset, approverId);
        if (ok) {
            return Result.OK("登记申请已提交");
        }
        return Result.error("提交失败");
    }

    /**
     * 编辑
     */
    @AutoLog(value = "数据资产-编辑")
    @ApiOperation(value = "数据资产-编辑", notes = "数据资产-编辑")
    @PutMapping(value = "/edit")
    public Result<String> edit(@RequestBody SpaceAsset spaceAsset) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (user != null) {
            spaceAsset.setUpdateBy(user.getUsername());
        }
        spaceAsset.setUpdateTime(new Date());
        spaceAssetService.updateById(spaceAsset);
        return Result.OK("编辑成功");
    }

    /**
     * 通过id删除
     */
    @AutoLog(value = "数据资产-删除")
    @ApiOperation(value = "数据资产-删除", notes = "数据资产-删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        spaceAssetService.removeById(id);
        return Result.OK("删除成功");
    }

    /**
     * 批量删除
     */
    @AutoLog(value = "数据资产-批量删除")
    @ApiOperation(value = "数据资产-批量删除", notes = "数据资产-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        for (String id : ids.split(",")) {
            spaceAssetService.removeById(id);
        }
        return Result.OK("批量删除成功");
    }

    /**
     * 根据id查询
     */
    @AutoLog(value = "数据资产-查询详情")
    @ApiOperation(value = "数据资产-查询详情", notes = "数据资产-查询详情")
    @GetMapping(value = "/queryById")
    public Result<SpaceAsset> queryById(@RequestParam(name = "id", required = true) String id) {
        SpaceAsset spaceAsset = spaceAssetService.getById(id);
        if (spaceAsset == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(spaceAsset);
    }

    /**
     * 提交转让登记
     */
    @AutoLog(value = "数据资产-提交转让登记")
    @ApiOperation(value = "数据资产-提交转让登记", notes = "数据资产-提交转让登记")
    @PostMapping(value = "/submitTransfer")
    public Result<String> submitTransfer(
            @RequestParam(name = "assetId", required = true) String assetId,
            @RequestParam(name = "newOwnerId", required = true) String newOwnerId,
            @RequestParam(name = "approverId", required = true) String approverId) {
        boolean ok = spaceAssetService.submitTransferRegistration(assetId, newOwnerId, approverId);
        if (ok) {
            return Result.OK("转让登记申请已提交");
        }
        return Result.error("提交失败");
    }

    /**
     * 提交变更登记
     */
    @AutoLog(value = "数据资产-提交变更登记")
    @ApiOperation(value = "数据资产-提交变更登记", notes = "数据资产-提交变更登记")
    @PostMapping(value = "/submitChange")
    public Result<String> submitChange(@RequestBody SpaceAsset spaceAsset, @RequestParam(name = "approverId") String approverId) {
        boolean ok = spaceAssetService.submitChangeRegistration(spaceAsset, approverId);
        if (ok) {
            return Result.OK("变更登记申请已提交");
        }
        return Result.error("提交失败");
    }

    /**
     * 提交注销登记
     */
    @AutoLog(value = "数据资产-提交注销登记")
    @ApiOperation(value = "数据资产-提交注销登记", notes = "数据资产-提交注销登记")
    @PostMapping(value = "/submitCancel")
    public Result<String> submitCancel(
            @RequestParam(name = "assetId", required = true) String assetId,
            @RequestParam(name = "cancelReason", required = true) String cancelReason,
            @RequestParam(name = "approverId", required = true) String approverId) {
        boolean ok = spaceAssetService.submitCancelRegistration(assetId, cancelReason, approverId);
        if (ok) {
            return Result.OK("注销登记申请已提交");
        }
        return Result.error("提交失败");
    }

    /**
     * 更新证书信息
     */
    @AutoLog(value = "数据资产-更新证书信息")
    @ApiOperation(value = "数据资产-更新证书信息", notes = "数据资产-更新证书信息")
    @PutMapping(value = "/updateCert")
    public Result<String> updateCert(
            @RequestParam(name = "assetId", required = true) String assetId,
            @RequestParam(name = "certPath", required = true) String certPath,
            @RequestParam(name = "certNo", required = true) String certNo) {
        SpaceAsset asset = new SpaceAsset();
        asset.setId(assetId);
        asset.setCertPath(certPath);
        asset.setCertNo(certNo);
        asset.setUpdateTime(new Date());
        spaceAssetService.updateById(asset);
        return Result.OK("证书信息更新成功");
    }
}
