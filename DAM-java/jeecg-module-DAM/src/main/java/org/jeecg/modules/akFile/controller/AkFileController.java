package org.jeecg.modules.akFile.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.akFile.entity.AkFile;
import org.jeecg.modules.akFile.service.IAkFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @Description: 文件管理
 * @Author: jeecg-boot
 * @Date: 2025-09-24
 * @Version: V1.0
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/akFile/akFile")
@Slf4j
public class AkFileController extends JeecgController<AkFile, IAkFileService> {
    @Autowired
    private IAkFileService akFileService;

    /**
     * 分页列表查询
     *
     * @param akFile
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "文件管理-分页列表查询")
    @ApiOperation(value = "文件管理-分页列表查询", notes = "文件管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<AkFile>> queryPageList(AkFile akFile,
                                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                               HttpServletRequest req) {
        QueryWrapper<AkFile> queryWrapper = QueryGenerator.initQueryWrapper(akFile, req.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        设置用户只能查看自己填报或导入的数据，只有管理员才能查看所有数据
        if (!sysUser.getUsername().equals("admin")) {
            queryWrapper.eq("create_by", sysUser.getUsername());
        }
        Page<AkFile> page = new Page<>(pageNo, pageSize);
        IPage<AkFile> pageList = akFileService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param akFile
     * @return
     */
    @AutoLog(value = "文件管理-添加")
    @ApiOperation(value = "文件管理-添加", notes = "文件管理-添加")
    //@RequiresPermissions("akFile:ak_file:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AkFile akFile) {
        akFileService.save(akFile);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param akFile
     * @return
     */
    @AutoLog(value = "文件管理-编辑")
    @ApiOperation(value = "文件管理-编辑", notes = "文件管理-编辑")
    //@RequiresPermissions("akFile:ak_file:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AkFile akFile) {
        akFileService.updateById(akFile);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "文件管理-通过id删除")
    @ApiOperation(value = "文件管理-通过id删除", notes = "文件管理-通过id删除")
    //@RequiresPermissions("akFile:ak_file:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        akFileService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "文件管理-批量删除")
    @ApiOperation(value = "文件管理-批量删除", notes = "文件管理-批量删除")
    //@RequiresPermissions("akFile:ak_file:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.akFileService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "文件管理-通过id查询")
    @ApiOperation(value = "文件管理-通过id查询", notes = "文件管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AkFile> queryById(@RequestParam(name = "id", required = true) String id) {
        AkFile akFile = akFileService.getById(id);
        if (akFile == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(akFile);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param akFile
     */
    //@RequiresPermissions("akFile:ak_file:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AkFile akFile) {
        return super.exportXls(request, akFile, AkFile.class, "文件管理");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("akFile:ak_file:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AkFile.class);
    }
}
