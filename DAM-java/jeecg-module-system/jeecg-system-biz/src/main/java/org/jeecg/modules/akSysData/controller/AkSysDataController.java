package org.jeecg.modules.akSysData.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.akSysData.entity.AkSysData;
import org.jeecg.modules.akSysData.service.IAkSysDataService;
import org.jeecg.modules.system.entity.SysTodo;
import org.jeecg.modules.system.service.ISysTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 系统参数表
 * @Author: jeecg-boot
 * @Date: 2025-07-21
 * @Version: V1.0
 */
@Api(tags = "系统参数表")
@RestController
@RequestMapping("/akSysData/akSysData")
@Slf4j
public class AkSysDataController extends JeecgController<AkSysData, IAkSysDataService> {
    @Autowired
    private IAkSysDataService akSysDataService;
    @Autowired
    private ISysTodoService sysTodoService;

    /**
     * 分页列表查询
     *
     * @param akSysData
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "系统参数表-分页列表查询")
    @ApiOperation(value = "系统参数表-分页列表查询", notes = "系统参数表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<AkSysData>> queryPageList(AkSysData akSysData, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<AkSysData> queryWrapper = QueryGenerator.initQueryWrapper(akSysData, req.getParameterMap());
        Page<AkSysData> page = new Page<AkSysData>(pageNo, pageSize);
        IPage<AkSysData> pageList = akSysDataService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param akSysData
     * @return
     */
    @AutoLog(value = "系统参数表-添加")
    @ApiOperation(value = "系统参数表-添加", notes = "系统参数表-添加")
    //@RequiresPermissions("akSysData:ak_sys_data:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AkSysData akSysData) throws JsonProcessingException {
//		不能直接 add, 要先提交到授权管理员的待办事项，批准后才添加成功
//        akSysDataService.save(akSysData);
//        NameValuePair nameValuePair = new NameValuePair("akSysData", akSysData);
//        String remark = SysRolePermissionServiceImpl.toJson(nameValuePair),
        String content = "";
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        content += user.getRealname() + "添加系统参数-【允许的连续登录失败次数：" + akSysData.getFailTime() + "; token失效时间(分钟): " + akSysData.getTokenTime() + "；口令更改周期（单位：天）：" + akSysData.getChangeDate() + "】";
        // 创建ObjectMapper并禁用嵌套对象的自动包装
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        // 序列化为JSON字符串
        String json = mapper.writeValueAsString(akSysData);
        //  先往 sysTodo 发请求，先把 list 和 delete 放到 remark 字段里，得到确认后再执行对应的操作
        sysTodoService.save(new SysTodo(5, content, json, 1, user.getUsername(), "授权管理员"));
        return Result.OK("系统参数添加申请已经提交到授权管理员的待办事项！");
    }

    /**
     * 编辑
     *
     * @param akSysData
     * @return
     */
    @AutoLog(value = "系统参数表-编辑")
    @ApiOperation(value = "系统参数表-编辑", notes = "系统参数表-编辑")
    //@RequiresPermissions("akSysData:ak_sys_data:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AkSysData akSysData) throws JsonProcessingException {
//        akSysDataService.updateById(akSysData);
        String content = "";
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        content += user.getRealname() + "变更系统参数-【允许的连续登录失败次数：" + akSysData.getFailTime() + "; token失效时间(分钟): " + akSysData.getTokenTime() + "；口令更改周期（单位：天）：" + akSysData.getChangeDate() + "】";
        // 创建ObjectMapper并禁用嵌套对象的自动包装
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        //判断是否可提交
        QueryWrapper<SysTodo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", 6);
        queryWrapper.eq("status", 1);
        queryWrapper.like("content", "变更系统参数-");
        List<SysTodo> todoList = sysTodoService.list(queryWrapper);
        if(!CollectionUtils.isEmpty(todoList)){
            return Result.error("提交失败： 存在未审批的记录！");
        }
        // 序列化为JSON字符串
        String json = mapper.writeValueAsString(akSysData);
        //  先往 sysTodo 发请求，先把 list 和 delete 放到 remark 字段里，得到确认后再执行对应的操作
        sysTodoService.save(new SysTodo(6, content, json, 1, user.getUsername(), "授权管理员"));
        return Result.OK("系统参数变更申请已经提交到授权管理员的待办事项！");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "系统参数表-通过id删除")
    @ApiOperation(value = "系统参数表-通过id删除", notes = "系统参数表-通过id删除")
    //@RequiresPermissions("akSysData:ak_sys_data:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) throws JsonProcessingException {
//        akSysDataService.removeById(id);
        AkSysData akSysData = akSysDataService.getById(id);
        String content = "";
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        content += user.getRealname() + "删除系统参数-【允许的连续登录失败次数：" + akSysData.getFailTime() + "; token失效时间(分钟): " + akSysData.getTokenTime() + "；口令更改周期（单位：天）：" + akSysData.getChangeDate() + "】";

        //  先往 sysTodo 发请求，先把 list 和 delete 放到 remark 字段里，得到确认后再执行对应的操作
        sysTodoService.save(new SysTodo(7, content, id, 1, user.getUsername(), "授权管理员"));
        return Result.OK("系统参数删除申请已经提交到授权管理员的待办事项！");
//        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "系统参数表-批量删除")
    @ApiOperation(value = "系统参数表-批量删除", notes = "系统参数表-批量删除")
    //@RequiresPermissions("akSysData:ak_sys_data:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") String ids) {
//        this.akSysDataService.removeByIds(Arrays.asList(ids.split(",")));
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String content = user.getRealname() + "批量删除系统参数-" + String.join("\n", Arrays.stream(ids.split(",")).map(id -> {
            Long lId = Long.parseLong(id);
            AkSysData ak = akSysDataService.getById(lId);
            return "【允许的连续登录失败次数：" + ak.getFailTime() + "; token失效时间(分钟): " + ak.getTokenTime() + "；口令更改周期（单位：天）:" + ak.getChangeDate() + "】";
        }).collect(Collectors.toList()));
//        content += user.getRealname() + "批量删除系统参数-【允许的连续登录失败次数：" + akSysData.getFailTime() + "; token失效时间(分钟): " + akSysData.getTokenTime() + "；口令更改周期（单位：天）：" + akSysData.getChangeDate() + "】";
        //  先往 sysTodo 发请求，先把 list 和 delete 放到 remark 字段里，得到确认后再执行对应的操作
        sysTodoService.save(new SysTodo(8, content, ids, 1, user.getUsername(), "授权管理员"));
        return Result.OK("系统参数批量删除申请已经提交到授权管理员的待办事项！");
//        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "系统参数表-通过id查询")
    @ApiOperation(value = "系统参数表-通过id查询", notes = "系统参数表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AkSysData> queryById(@RequestParam(name = "id", required = true) String id) {
        AkSysData akSysData = akSysDataService.getById(id);
        if (akSysData == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(akSysData);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param akSysData
     */
    //@RequiresPermissions("akSysData:ak_sys_data:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AkSysData akSysData) {
        return super.exportXls(request, akSysData, AkSysData.class, "系统参数表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("akSysData:ak_sys_data:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AkSysData.class);
    }

}
