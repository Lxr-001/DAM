package org.jeecg.modules.base.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.dto.LogDTO;
import org.jeecg.modules.base.mapper.BaseCommonMapper;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.IpUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Description: common实现类
 * @author: jeecg-boot
 */
@Service
@Slf4j
public class BaseCommonServiceImpl implements BaseCommonService {
    @Value("${k0.systemSecurity}")
    private Integer systemSecurity;

    @Resource
    private BaseCommonMapper baseCommonMapper;

    /**
     * 保存日志
     *
     * @param security    密级（1.公开 2.内部 3.秘m 4.机m 5.jm）
     * @param logType     日志类型（1.系统业务功能 2.系统参数管理 3.系统运行告警 4.安全管理 5.安全审计）
     * @param operateType 操作类型（1.查询 2.添加 3.修改 4.删除 5.导入 6.导出 7.用户登录/退出 8.用户/角色变更 9.系统参数变更）
     * @param objectName  操作对象（数据表名或业务对象名称）
     * @param logContent  操作详细内容（自拟，不超过1000字）
     * @param result      操作结果（成功/失败）
     */
    @Override
    public void addLog(Integer security, Integer logType, Integer operateType, String objectName, String logContent, String result) {
        //获取登录用户信息
        LoginUser user = null;
        try {
            user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        this.addLog(security, logType, operateType, objectName, logContent, result, user);
    }

    public void addLog(Integer security, Integer logType, Integer operateType, String objectName, String logContent, String result, LoginUser user) {
        //获取连接IP
        String ip;
        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //获取IP地址
            ip = IpUtils.getIpAddr(request);
            if (ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
                ip = "127.0.0.1";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            ip = "未知来源";
        }

        LogDTO sysLog = new LogDTO();
        sysLog.setId(String.valueOf(IdWorker.getId()));
        sysLog.setCreateTime(new Date());
        sysLog.setIp(ip);
        if (user != null) {
            sysLog.setUserid(user.getUsername());
            sysLog.setUsername(user.getRealname());
        } else {
            sysLog.setUserid("");
            sysLog.setUsername("");
        }
        sysLog.setSecurity(security);
        sysLog.setLogType(logType);
        sysLog.setOperateType(operateType);
        sysLog.setObjectName(objectName);
        sysLog.setLogContent(logContent);
        sysLog.setResult(result);

        //保存日志（异常捕获处理，防止数据太大存储失败，导致业务失败）JT-238
        try {
            baseCommonMapper.saveLog(sysLog);
        } catch (Exception e) {
            log.error(" LogContent length : " + sysLog.getLogContent().length());
            log.error(e.getMessage());
        }
    }


    @Override
    public void addLog(LogDTO logDTO) {
        if(oConvertUtils.isEmpty(logDTO.getId())){
            logDTO.setId(String.valueOf(IdWorker.getId()));
        }
        //保存日志（异常捕获处理，防止数据太大存储失败，导致业务失败）JT-238
        try {
            logDTO.setCreateTime(new Date());
            baseCommonMapper.saveLog(logDTO);
        } catch (Exception e) {
            log.warn(" LogContent length : "+logDTO.getLogContent().length());
            log.warn(e.getMessage());
        }
    }

    @Override
    public void addLog(String logContent, Integer logType, Integer operatetype, LoginUser user) {
        LogDTO sysLog = new LogDTO();
        sysLog.setId(String.valueOf(IdWorker.getId()));
        //注解上的描述,操作日志内容
        sysLog.setLogContent(logContent);
        sysLog.setLogType(logType);
        sysLog.setOperateType(operatetype);
        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //设置IP地址
            sysLog.setIp(IpUtils.getIpAddr(request));
        } catch (Exception e) {
            sysLog.setIp("127.0.0.1");
        }
        //获取登录用户信息
        if(user==null){
            try {
                user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        if(user!=null){
            sysLog.setUserid(user.getUsername());
            sysLog.setUsername(user.getRealname());
        }
        sysLog.setCreateTime(new Date());
        //保存日志（异常捕获处理，防止数据太大存储失败，导致业务失败）JT-238
        try {
            baseCommonMapper.saveLog(sysLog);
        } catch (Exception e) {
            log.warn(" LogContent length : "+sysLog.getLogContent().length());
            log.warn(e.getMessage());
        }
    }

    @Override
    public void addLog(String logContent, Integer logType, Integer operateType) {
        addLog(logContent, logType, operateType, null);
    }



}
