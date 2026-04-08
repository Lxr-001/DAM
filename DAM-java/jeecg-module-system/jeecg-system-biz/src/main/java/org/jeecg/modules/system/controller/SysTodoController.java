package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.akSysData.entity.AkSysData;
import org.jeecg.modules.akSysData.service.IAkSysDataService;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.SysRolePermission;
import org.jeecg.modules.system.entity.SysTodo;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.service.ISysRolePermissionService;
import org.jeecg.modules.system.service.ISysTodoService;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.impl.SysRolePermissionServiceImpl;
import org.jeecg.modules.system.util.PermissionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 待办事项
 * @Author: jeecg-boot
 * @Date: 2025-07-16
 * @Version: V1.0
 */
@Api(tags = "待办事项")
@RestController
@RequestMapping("/system/sysTodo")
@Slf4j
public class SysTodoController extends JeecgController<SysTodo, ISysTodoService> {
    @Value("${k0.systemSecurity}")
    private Integer systemSecurity;
    @Autowired
    private ISysTodoService sysTodoService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysRolePermissionService rolePermissionService;
    @Autowired
    private IAkSysDataService akSysDataService;

    @Autowired
    private BaseCommonService baseCommonService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    /**
     * 分页列表查询
     *
     * @param sysTodo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "待办事项-分页列表查询")
    @ApiOperation(value = "待办事项-分页列表查询", notes = "待办事项-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<SysTodo>> queryPageList(SysTodo sysTodo, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<SysTodo> queryWrapper = QueryGenerator.initQueryWrapper(sysTodo, req.getParameterMap());
        Page<SysTodo> page = new Page<>(pageNo, pageSize);
        IPage<SysTodo> pageList = sysTodoService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param sysTodo
     * @return
     */
    @AutoLog(value = "待办事项-添加")
    @ApiOperation(value = "待办事项-添加", notes = "待办事项-添加")
    //@RequiresPermissions("system:sys_todo:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody SysTodo sysTodo) {

        sysTodoService.save(sysTodo);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param sysTodo
     * @return
     */
    @AutoLog(value = "待办事项-编辑")
    @ApiOperation(value = "待办事项-编辑", notes = "待办事项-编辑")
    //@RequiresPermissions("system:sys_todo:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody SysTodo sysTodo) throws JsonProcessingException {
        sysTodo.setAuditTime(new Date());
        sysTodoService.updateById(sysTodo);
        Integer type = sysTodo.getType();
        String userId = sysTodo.getPid();
        //类型：1-用户新增，2-用户编辑，3-用户删除，4-权限变更，5-添加系统设置参数，6-系统设置参数变更，7-系统设置参数删除，8-系统设置参数批量删除
        SysUser user = sysUserService.getById(userId);


        //状态：1-待审核、2-已通过、3-已拒绝
        Integer status = sysTodo.getStatus();
        String remark = sysTodo.getRemark();
//		根据 status 还要对相应的 SysUser 关联数据进行处理
        switch (type) {
            case 1:
                if (status == 2) {//通过
                    user.setAuditStatus(status);
                    sysUserService.updateById(user);
                    //        String logContent, String rt,Integer logType, Integer operateType, String objectName
//        logType     日志类型（1.系统业务功能 2.系统参数管理 3.系统运行告警 4.授权管理 5.安全审计）
//     * @param operateType 操作类型（1.查询 2.添加 3.修改 4.删除 5.导入 6.导出 7.用户登录/退出 8.用户/角色变更 9.系统参数变更, 10.数据分享，11.撤回）
//     * @param objectName  操作对象（数据表名或业务对象名称）
//     * @param logContent  操作详细内容（自拟，不超过1000字）
//     * @param result      操作结果（成功/失败）
                    this.addLog("用户新增：用户名: " + user.getUsername() + "，用户密级为：" + this.personSecurityText(user.getSecurity()) + " 通过！", "通过", 4, 2, "用户");
                } else {//拒绝
                    user.setAuditStatus(status);
                    sysUserService.updateById(user);
                    this.addLog("用户新增：用户名: " + user.getUsername() + "，用户密级为：" + this.personSecurityText(user.getSecurity()) + " 不通过！", "不通过", 4, 2, "用户");
                }
                break;
            case 2:
//                用户编辑
                if (status == 2) {//通过
                    // 反序列化回 JSONObject 对象
                    ObjectMapper mapper = new ObjectMapper();
                    JSONObject deserializedObj = mapper.readValue(remark, JSONObject.class);
                    user = JSON.parseObject(deserializedObj.toJSONString(), SysUser.class);
                    user.setUpdateTime(new Date());
                    //String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
                    String roles = deserializedObj.getString("selectedroles");
                    String departs = deserializedObj.getString("selecteddeparts");
                    user.setAuditStatus(status);
                    sysUserService.editUserNew(user, roles, departs);
                    this.addLog("用户编辑：" + sysTodo.getContent() + " 通过！", "通过", 4, 3, "用户");
                } else {//拒绝
                    user.setAuditStatus(status);
                    sysUserService.updateById(user);
                    this.addLog("用户编辑：" + sysTodo.getContent() + " 不通过！", "不通过", 4, 3, "用户");
                }
                break;
            case 3:
                if (status == 2) {
                    sysUserService.deleteUser(userId);
                    this.addLog("用户删除：用户名: " + user.getUsername() + " 通过！", "通过", 4, 4, "用户");
                } else {
//                    审核状态设置为已拒绝
                    user.setAuditStatus(3);
                    sysUserService.updateById(user);
                    this.addLog("用户删除：用户名: " + user.getUsername() + " 不通过！", "不通过", 4, 4, "用户");
                }
                break;
            case 4:
                if (status == 2) {
//                        从 remark 中 抽取出 add 和 delete 然后执行
                    Map<String, Object> map = SysRolePermissionServiceImpl.fromJson(remark);
                    List<LinkedTreeMap<String, Object>> add = (List<LinkedTreeMap<String, Object>>) map.get("add");

                    // 创建当前时间的 LocalDateTime 对象
                    LocalDateTime dateObj = LocalDateTime.now();

                    // 定义目标日期时间格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    try {
                    for (LinkedTreeMap<String, Object> map1 : add) {
                        String formattedDateStr = formatter.format(dateObj);
                        // 更新map中的日期值
                        map1.put("operateDate", formattedDateStr);
                    }
//                    } catch (Exception e) {
//                        System.out.println("日期解析失败：" + e.getMessage());
//                    }
                    List<SysRolePermission> sysRolePermissions = PermissionConverter.convertList(add);
                    rolePermissionService.saveBatch(sysRolePermissions);
                    List<String> delete = (List<String>) map.get("del");
                    String roleId = (String) map.get("roleId");
                    if (delete != null && !delete.isEmpty()) {
                        for (String permissionId : delete) {
                            rolePermissionService.remove(new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId).eq(SysRolePermission::getPermissionId, permissionId));
                        }
                    }
                    this.addLog("角色权限变更：" + sysTodo.getContent() + " 通过！", "通过", 4, 3, "角色权限");
                } else {
                    this.addLog("角色权限变更：" + sysTodo.getContent() + " 不通过！", "不通过", 4, 3, "角色权限");
                }
                break;
            case 5:
//                添加系统参数设置
                if (status == 2) {
//                        从 remark 中 抽取出需要 add 的系统参数设置
                    ObjectMapper objectMapper = new ObjectMapper();
                    AkSysData akSysData = objectMapper.readValue(remark, AkSysData.class);
                    if (akSysData.getTokenTime() != null || akSysData.getFailTime() != null || akSysData.getChangeDate() != null) {
                        akSysDataService.save(akSysData);
                    } else {
                        return Result.error("token失效时间、允许的连续登录失败次数、口令更改周期（单位：天）均为空，无法通过");
                    }
                    this.addLog("添加系统参数设置：" + sysTodo.getContent() + " 通过！", "通过", 2, 2, "系统参数设置");
                } else {
                    this.addLog("添加系统参数设置：" + sysTodo.getContent() + " 不通过！", "不通过", 2, 2, "系统参数设置");
                }
                break;
            case 6:
//                变更系统参数设置
                if (status == 2) {
//                        从 remark 中 抽取出需要 edit 的系统参数设置
                    ObjectMapper objectMapper = new ObjectMapper();
                    AkSysData akSysData = objectMapper.readValue(remark, AkSysData.class);
                    if (akSysData.getTokenTime() != null || akSysData.getFailTime() != null || akSysData.getChangeDate() != null) {
                        akSysDataService.updateById(akSysData);
                    } else {
                        return Result.error("token失效时间、允许的连续登录失败次数、口令更改周期（单位：天）均为空，无法通过");
                    }
                    this.addLog("变更系统参数设置：" + sysTodo.getContent() + " 通过！", "通过", 2, 3, "系统参数设置");
                } else {
                    this.addLog("变更系统参数设置：" + sysTodo.getContent() + " 不通过！", "不通过", 2, 3, "系统参数设置");
                }
                break;
            case 7:
//                删除系统参数设置
                if (status == 2) {
                    String id = sysTodo.getRemark();
                    akSysDataService.removeById(id);
                    this.addLog("删除系统参数设置：" + sysTodo.getContent() + " 通过！", "通过", 2, 4, "系统参数设置");
                } else {
                    this.addLog("删除系统参数设置：" + sysTodo.getContent() + " 不通过！", "不通过", 2, 4, "系统参数设置");
                }
                break;
            case 8:
//                批量删除系统参数设置
                if (status == 2) {
                    String ids = sysTodo.getRemark();
                    ids = StringUtils.stripEnd(ids, ",");
                    this.akSysDataService.removeByIds(Arrays.asList(ids.split(",")));
                    this.addLog("批量删除系统参数设置：" + sysTodo.getContent() + " 通过！", "通过", 2, 4, "系统参数设置");
                } else {
                    this.addLog("批量删除系统参数设置：" + sysTodo.getContent() + " 不通过！", "不通过", 2, 4, "系统参数设置");
                }
                break;
            case 9:
                if (status == 2) {
                    ObjectMapper mapper = new ObjectMapper();
                    JSONObject deserializedObj = mapper.readValue(remark, JSONObject.class);
                    SysUserRole sysUserRole = JSON.parseObject(deserializedObj.toJSONString(), SysUserRole.class);
                    String role_id = deserializedObj.getString("role_id");
                    String user_id = deserializedObj.getString("user_id");
                    String real_name = deserializedObj.getString("real_name");
                    String role_Name = deserializedObj.getString("role_Name");
                    sysUserRole.setRoleId(role_id);
                    sysUserRole.setUserId(user_id);
                    sysUserRoleService.save(sysUserRole);
                    String content = "新增用户角色，将用户 " + real_name + " 添加到 " + role_Name + " 角色";
                    this.addLog(content + " 通过！", "通过", 4, 8, "新增用户角色");
                } else {
                    ObjectMapper mapper = new ObjectMapper();
                    JSONObject deserializedObj = mapper.readValue(remark, JSONObject.class);
                    String real_name = deserializedObj.getString("real_name");
                    String role_Name = deserializedObj.getString("role_Name");
                    String content = "新增用户角色，将用户 " + real_name + " 添加到 " + role_Name + " 角色";
                    this.addLog(content + " 不通过！", "不通过", 4, 8, "新增用户角色");
                }
                break;
            case 10:
                if (status == 2) {
                    ObjectMapper mapper = new ObjectMapper();
                    JSONObject deserializedObj = mapper.readValue(remark, JSONObject.class);
                    String role_id = deserializedObj.getString("role_id");
                    String user_Ids = deserializedObj.getString("user_Ids");
                    String role_Name = deserializedObj.getString("role_Name");
                    String user_Names = deserializedObj.getString("user_Names");
                    QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("role_id", role_id).in("user_id", Arrays.asList(user_Ids.split(",")));
                    sysUserRoleService.remove(queryWrapper);
                    String content = "修改用户角色，删除角色 " + role_Name + " 下的 " + String.join("、", user_Names) + " 用户";
                    this.addLog(content + " 通过！", "通过", 4, 8, "新增用户角色");
                } else {
                }
                break;
        }
        return Result.OK("操作成功!");
    }

    private String personSecurityText(int security) {
        String[] texts = {"非密", "非密", "非密", "一般", "重要", "核心"};
        return texts[security];
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "待办事项-通过id删除")
    @ApiOperation(value = "待办事项-通过id删除", notes = "待办事项-通过id删除")
    //@RequiresPermissions("system:sys_todo:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") String id) {
        sysTodoService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "待办事项-批量删除")
    @ApiOperation(value = "待办事项-批量删除", notes = "待办事项-批量删除")
    //@RequiresPermissions("system:sys_todo:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.sysTodoService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "待办事项-通过id查询")
    @ApiOperation(value = "待办事项-通过id查询", notes = "待办事项-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<SysTodo> queryById(@RequestParam(name = "id", required = true) String id) {
        SysTodo sysTodo = sysTodoService.getById(id);
        if (sysTodo == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(sysTodo);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sysTodo
     */
    //@RequiresPermissions("system:sys_todo:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysTodo sysTodo) {
        return super.exportXls(request, sysTodo, SysTodo.class, "待办事项");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("system:sys_todo:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysTodo.class);
    }

}
