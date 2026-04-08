package org.jeecg.common.api.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 日志对象
 * cloud api 用到的接口传输对象
 *
 * @author: jeecg-boot
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogDTO implements Serializable {

    private static final long serialVersionUID = 8482720462943906924L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 密级（1.公开 2.内部 3.秘m 4.机m 5.jm）
     */
    @Dict(dicCode = "system_security")
    private Integer security;

    /**
     * IP
     */
    private String ip;

    /**
     * 操作人名称
     */
    private String username;

    /**
     * 操作人ID
     */
    private String userid;

    /**
     * 日志类型（1.系统业务功能 2.系统参数管理 3.系统运行告警 4.安全管理 5.安全审计）
     */
    @Dict(dicCode = "log_type")
    private Integer logType;

    /**
     * 操作类型（1.查询 2.添加 3.修改 4.删除 5.导入 6.导出 7.用户登录/退出 8.用户/角色变更 9.系统参数变更）
     */
    @Dict(dicCode = "operate_type")
    private Integer operateType;

    /**
     * 操作对象（数据表名或业务对象名称）
     */
    private String objectName;

    /**
     * 操作详细内容（自拟，不超过1000字）
     */
    private String logContent;

    /**
     * 操作结果（成功/失败）
     */
    private String result;

    /**
     * 登录用户
     */
    private LoginUser loginUser;


    private String createBy;
    private String updateBy;
    private Date updateTime;
    private Long costTime;
    private String requestParam;
    private String requestType;
    private String requestUrl;
    private String method;
}
