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
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.AssetRegistration;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.service.IAssetRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 资产登记申请
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
@Api(tags = "资产登记申请")
@RestController
@RequestMapping("/assetRegistration")
@Slf4j
public class AssetRegistrationController {

    @Autowired
    private IAssetRegistrationService assetRegistrationService;

    /**
     * 分页列表查询
     */
    @AutoLog(value = "资产登记申请-分页列表")
    @ApiOperation(value = "资产登记申请-分页列表", notes = "资产登记申请-分页列表")
    @GetMapping(value = "/list")
    public Result<IPage<AssetRegistration>> queryPageList(
            AssetRegistration assetRegistration,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req) {
        QueryWrapper<AssetRegistration> queryWrapper = QueryGenerator.initQueryWrapper(assetRegistration, req.getParameterMap());
        Page<AssetRegistration> page = new Page<>(pageNo, pageSize);
        IPage<AssetRegistration> pageList = assetRegistrationService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 根据资产ID查询登记申请记录
     */
    @AutoLog(value = "资产登记申请-根据资产ID查询")
    @ApiOperation(value = "资产登记申请-根据资产ID查询", notes = "资产登记申请-根据资产ID查询")
    @GetMapping(value = "/queryByAssetId")
    public Result<?> queryByAssetId(@ApiParam(value = "资产ID", required = true) @RequestParam(name = "assetId", required = true) String assetId) {
        java.util.List<AssetRegistration> list = assetRegistrationService.getRegistrationsByAssetId(assetId);
        return Result.OK(list);
    }

    /**
     * 根据空间ID查询待审批的登记申请
     */
    @AutoLog(value = "资产登记申请-待审批列表")
    @ApiOperation(value = "资产登记申请-待审批列表", notes = "资产登记申请-待审批列表")
    @GetMapping(value = "/queryPendingBySpaceId")
    public Result<?> queryPendingBySpaceId(@ApiParam(value = "空间ID", required = true) @RequestParam(name = "spaceId", required = true) String spaceId) {
        java.util.List<AssetRegistration> list = assetRegistrationService.getPendingRegistrations(spaceId);
        return Result.OK(list);
    }

    /**
     * 通过id查询
     */
    @AutoLog(value = "资产登记申请-查询详情")
    @ApiOperation(value = "资产登记申请-查询详情", notes = "资产登记申请-查询详情")
    @GetMapping(value = "/queryById")
    public Result<AssetRegistration> queryById(@RequestParam(name = "id", required = true) String id) {
        AssetRegistration registration = assetRegistrationService.getById(id);
        if (registration == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(registration);
    }

    /**
     * 审批通过
     */
    @AutoLog(value = "资产登记申请-审批通过")
    @ApiOperation(value = "资产登记申请-审批通过", notes = "资产登记申请-审批通过")
    @PutMapping(value = "/approve")
    public Result<String> approve(@RequestParam(name = "id", required = true) String id) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        log.info("用户 " + (user != null ? user.getUsername() : "unknown") + " 审批通过登记申请: " + id);
        boolean ok = assetRegistrationService.approveRegistration(id);
        if (ok) {
            return Result.OK("审批通过");
        }
        return Result.error("操作失败");
    }

    /**
     * 审批驳回
     */
    @AutoLog(value = "资产登记申请-审批驳回")
    @ApiOperation(value = "资产登记申请-审批驳回", notes = "资产登记申请-审批驳回")
    @PutMapping(value = "/reject")
    public Result<String> reject(@RequestParam(name = "id", required = true) String id) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        log.info("用户 " + (user != null ? user.getUsername() : "unknown") + " 驳回登记申请: " + id);
        boolean ok = assetRegistrationService.rejectRegistration(id);
        if (ok) {
            return Result.OK("已驳回");
        }
        return Result.error("操作失败");
    }
}
