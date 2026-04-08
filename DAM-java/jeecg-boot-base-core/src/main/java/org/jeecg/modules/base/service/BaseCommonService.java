package org.jeecg.modules.base.service;

import org.jeecg.common.api.dto.LogDTO;
import org.jeecg.common.system.vo.LoginUser;

/**
 * common接口
 * @author: jeecg-boot
 */
public interface BaseCommonService {
    /**
     * 保存日志
     *
     * @param security    密级（1.公开 2.内部 3.秘m 4.机m 5.jm）
     * @param logType     日志类型（1.系统业务功能 2.系统参数管理 3.系统运行告警 4.安全管理 5.安全审计）
     * @param operateType 操作类型（1.查询 2.添加 3.修改 4.删除 5.导入 6.导出 7.用户登录/退出 8.用户/角色变更 9.系统参数变更, 10.数据分享）
     * @param objectName  操作对象（数据表名或业务对象名称）
     * @param logContent  操作详细内容（自拟，不超过1000字）
     * @param result      操作结果（成功/失败）
     */
    void addLog(Integer security, Integer logType, Integer operateType, String objectName, String logContent, String result);

    /**
     * 登录登出操作时，shiro中不存在LoginUser，只能手动传入
     */
    void addLog(Integer security, Integer logType, Integer operateType, String objectName, String logContent, String result, LoginUser user);

    /**
     * 保存日志
     * @param logDTO
     */
    void addLog(LogDTO logDTO);

    /**
     * 保存日志
     * @param logContent
     * @param logType
     * @param operateType
     * @param user
     */
    void addLog(String logContent, Integer logType, Integer operateType, LoginUser user);

    /**
     * 保存日志
     * @param logContent
     * @param logType
     * @param operateType
     */
    void addLog(String logContent, Integer logType, Integer operateType);

}
