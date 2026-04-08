package org.jeecg.modules.system.util;

import com.google.gson.internal.LinkedTreeMap;
import org.jeecg.modules.system.entity.SysRolePermission;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionConverter {
    public static SysRolePermission mapToSysPermission(LinkedTreeMap<String, Object> permissionMap) {
        SysRolePermission sysPermission = new SysRolePermission();

        // 设置 id
        if (permissionMap.containsKey("id")) {
            sysPermission.setId((String) permissionMap.get("id"));
        } else {
            // 如果没有 id，可以设置默认值或抛出异常

        }

        // 设置 name
        sysPermission.setRoleId((String) permissionMap.get("roleId"));

        // 设置 type
        sysPermission.setPermissionId((String) permissionMap.get("permissionId"));

        // 设置 url
        String operateDate = (String) permissionMap.get("operateDate");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateObj = LocalDateTime.parse(operateDate, formatter);

        // 将LocalDateTime转换为Instant，需要指定时区
        ZoneId zoneId = ZoneId.systemDefault();
        Date javaDate = Date.from(dateObj.atZone(zoneId).toInstant());
        sysPermission.setOperateDate(javaDate);

        // 设置 description
        sysPermission.setOperateIp((String) permissionMap.get("operateIp"));

        return sysPermission;
    }

    public static List<SysRolePermission> convertList(List<LinkedTreeMap<String, Object>> permissionMaps) {
        return permissionMaps.stream()
                .map(PermissionConverter::mapToSysPermission)
                .collect(Collectors.toList());
    }
}
