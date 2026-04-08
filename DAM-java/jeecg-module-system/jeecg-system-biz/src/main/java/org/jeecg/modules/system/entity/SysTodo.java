package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 待办事项
 * @Author: jeecg-boot
 * @Date: 2025-07-16
 * @Version: V1.0
 */
@NoArgsConstructor
@Data
@TableName("sys_todo")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sys_todo对象", description = "待办事项")
public class SysTodo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 关联数据 id
     */
    @Excel(name = "关联数据 id", width = 15)
    @ApiModelProperty(value = "关联数据 id")
    private String pid;
    /**
     * 类型：1-用户新增，2-用户编辑，3-用户删除，4-权限变更，5-添加系统设置参数，6-系统设置参数变更，7-系统设置参数删除，8-系统设置参数批量删除，9-角色变更-新增用户, 10-角色变更-批量删除
     */
    @Excel(name = "类型", width = 15, dicCode = "todo_type")
    @Dict(dicCode = "todo_type")
    @ApiModelProperty(value = "类型")
    private Integer type;
    /**
     * 内容
     */
    @Excel(name = "内容", width = 15)
    @ApiModelProperty(value = "内容")
    private String content;
    /**
     * 状态：1-待审核、2-已通过、3-已拒绝
     */
    @Excel(name = "状态", width = 15, dicCode = "audit_status")
    @Dict(dicCode = "audit_status")
    @ApiModelProperty(value = "状态")
    private Integer status;
    /**
     * 审核人
     */
    @Excel(name = "审核人", width = 15)
    @ApiModelProperty(value = "审核人")
    private String auditor;
    /**
     * 审核意见
     */
    @Excel(name = "审核意见", width = 15)
    @ApiModelProperty(value = "审核意见")
    private String auditOpinion;
    /**
     * 审核时间
     */
    @Excel(name = "审核时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核时间")
    private Date auditTime;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * pid, 类型，内容，状态（待审核：1），申请人：当前登录用户，审核人
     *
     * @param userId
     * @param type
     * @param content
     * @param status
     * @param username
     * @param auditor
     */
    public SysTodo(String userId, int type, String content, int status, String username, String auditor) {
        this.pid = userId;
        this.type = type;
        this.content = content;
        this.status = status;
        this.createBy = username;
        this.auditor = auditor;
    }

    /**
     * @param type
     * @param remark
     * @param status
     * @param username
     * @param auditor
     */
    public SysTodo(int type, String content, String remark, int status, String username, String auditor) {
        this.type = type;
        this.content = content;
        this.remark = remark;
        this.status = status;
        this.createBy = username;
        this.auditor = auditor;
    }

    public SysTodo(String userId, int type, String content, String remark, int status, String username, String auditor) {
        this.pid = userId;
        this.type = type;
        this.content = content;
        this.remark = remark;
        this.status = status;
        this.createBy = username;
        this.auditor = auditor;
    }
}
