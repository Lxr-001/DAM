package org.jeecg.modules.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * <p>
 * 系统日志表
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysLogExport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
//    @Excel(name = "ID", width = 20)
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 32)
    private String createTime;

    /**
     * IP
     */
    @Excel(name = "IP地址", width = 15)
    private String ip;

    /**
     * 操作人用户名称
     */
    @Excel(name = "操作人名称", width = 15)
    private String username;

    /**
     * 操作人用户账户
     */
    @Excel(name = "操作人ID", width = 15)
    private String userid;

    /**
     * 密级（1.公开 2.内部 3.秘密 4.机m 5.jm）
     */
    @Excel(dicCode = "system_security", name = "密级", width = 15)
    @Dict(dicCode = "system_security")
    private Integer security;

    /**
     * 日志类型（1.系统业务功能 2.系统参数管理 3.系统运行告警 4.安全管理 5.安全审计）
     */
    @Excel(dicCode = "log_type", name = "日志类型", width = 15)
    @Dict(dicCode = "log_type")
    private Integer logType;


    /**
     * 操作类型（1.查询 2.添加 3.修改 4.删除 5.导入 6.导出 7.用户登录/退出 8.用户/角色变更 9.系统参数变更）
     */
    @Excel(dicCode = "operate_type", name = "操作类型", width = 15)
    @Dict(dicCode = "operate_type")
    private Integer operateType;

    /**
     * 操作对象（数据表名或业务对象名称）
     */
    @Excel(name = "操作对象", width = 15)
    private String objectName;

    /**
     * 操作详细内容（自拟，不超过1000字）
     */
    @Excel(name = "日志内容", width = 64)
    private String logContent;

    /**
     * 操作结果（成功/失败）
     */
    @Excel(name = "操作结果", width = 15)
    private String result;
}
