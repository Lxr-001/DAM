package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.IpUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.NameValuePair;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysRolePermission;
import org.jeecg.modules.system.entity.SysTodo;
import org.jeecg.modules.system.mapper.SysRolePermissionMapper;
import org.jeecg.modules.system.service.ISysRolePermissionService;
import org.jeecg.modules.system.service.ISysRoleService;
import org.jeecg.modules.system.service.ISysTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {
    private static final Gson gson = new Gson();
    @Autowired
    private ISysTodoService sysTodoService;
    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private SysPermissionServiceImpl sysPermissionServiceImpl;

    @Override
    public void saveRolePermission(String roleId, String permissionIds) {
        String ip = "";
        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //获取IP地址
            ip = IpUtils.getIpAddr(request);
        } catch (Exception e) {
            ip = "127.0.0.1";
        }
        LambdaQueryWrapper<SysRolePermission> query = new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId);
        this.remove(query);
        List<SysRolePermission> list = new ArrayList<SysRolePermission>();
        String[] arr = permissionIds.split(",");
        for (String p : arr) {
            if (oConvertUtils.isNotEmpty(p)) {
                SysRolePermission rolepms = new SysRolePermission(roleId, p);
                rolepms.setOperateDate(new Date());
                rolepms.setOperateIp(ip);
                list.add(rolepms);
            }
        }
        this.saveBatch(list);
    }

    public static String toJson(List<SysRolePermission> add, List<String> del, String roleId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("add", add);
        map.put("del", del);
        map.put("roleId", roleId);
        return gson.toJson(map);
    }

    /**
     * 接受动态参数个数的方法
     *
     * @param nameValues
     * @return
     */
    public static String toJson(NameValuePair... nameValues) {
        HashMap<String, Object> map = new HashMap<>();
        for (NameValuePair pair : nameValues) {
            map.put(pair.getKey(), pair.getValue());
        }
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static Map<String, Object> fromJson(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }

    @Override
    public String saveRolePermission(String roleId, String permissionIds, String lastPermissionIds) {
        String ip = "";
        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //获取IP地址
            ip = IpUtils.getIpAddr(request);
        } catch (Exception e) {
            ip = "127.0.0.1";
        }
        List<String> add = getDiff(lastPermissionIds, permissionIds);
        List<SysRolePermission> list = new ArrayList<>();
        List<String> permIds = new ArrayList<>();
        if (add != null && !add.isEmpty()) {
            for (String p : add) {
                if (oConvertUtils.isNotEmpty(p)) {
                    SysRolePermission rolepms = new SysRolePermission(roleId, p);
                    rolepms.setOperateDate(new Date());
                    rolepms.setOperateIp(ip);
                    permIds.add(rolepms.getPermissionId());
                    list.add(rolepms);
                }
            }
//            this.saveBatch(list);
        }
        SysRole role = sysRoleService.getById(roleId);

        //判断是否可提交角色变更申请
        QueryWrapper<SysTodo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", 4);
        queryWrapper.eq("status", 1);
        queryWrapper.like("content", "角色" + role.getRoleName()+"：增加");
        List<SysTodo> todoList = sysTodoService.list(queryWrapper);
        if(!CollectionUtils.isEmpty(todoList)){
            return "保存失败： 该角色存在[权限变更]类的未审批操作！";
        }


        List<String> addPermNames = sysPermissionServiceImpl.convertIdsToNames(permIds);
        List<String> delete = getDiff(permissionIds, lastPermissionIds);
        List<String> deleteName = sysPermissionServiceImpl.convertIdsToNames(delete);
        NameValuePair add1 = new NameValuePair("add", list);
        NameValuePair del = new NameValuePair("del", delete);
        NameValuePair roleId1 = new NameValuePair("roleId", roleId);
        String remark = toJson(add1, del, roleId1), content = "";
        if (isNotBothEmpty(addPermNames, deleteName)) {
            content += "角色" + role.getRoleName() + "：增加" + addPermNames + "权限，删除" + deleteName + "权限";
            LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            //  先往 sysTodo 发请求，先把 list 和 delete 放到 remark 字段里，得到确认后再执行对应的操作
            sysTodoService.save(new SysTodo(4, content, remark, 1, user.getUsername(), "授权管理员"));
        } else {
            System.out.println("角色" + role.getRoleName() + "：无权限变更");
        }
//        if (delete != null && !delete.isEmpty()) {
//            for (String permissionId : delete) {
//                this.remove(new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId).eq(SysRolePermission::getPermissionId, permissionId));
//            }
//        }
        return "";
    }

    private boolean isNotBothEmpty(List<String> addPermNames, List<String> deleteName) {
        return !(CollectionUtils.isEmpty(addPermNames) && CollectionUtils.isEmpty(deleteName));
    }

    /**
     * 从diff中找出main中没有的元素
     *
     * @param main
     * @param diff
     * @return
     */
    private List<String> getDiff(String main, String diff) {
        if (oConvertUtils.isEmpty(diff)) {
            return null;
        }
        if (oConvertUtils.isEmpty(main)) {
            return Arrays.asList(diff.split(","));
        }

        String[] mainArr = main.split(",");
        String[] diffArr = diff.split(",");
        Map<String, Integer> map = new HashMap<>(5);
        for (String string : mainArr) {
            map.put(string, 1);
        }
        List<String> res = new ArrayList<>();
        for (String key : diffArr) {
            if (oConvertUtils.isNotEmpty(key) && !map.containsKey(key)) {
                res.add(key);
            }
        }
        return res;
    }

}
