package org.jeecg.modules.chainattachment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 产业链附件
 * @Author: jeecg-boot
 * @Date: 2024-01-29
 * @Version: V1.0
 */
@Data
@TableName("chain_attachment")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "chain_attachment对象", description = "产业链附件")
public class ChainAttachment implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 文件名称
     */
    @Excel(name = "文件名称", width = 15)
    @ApiModelProperty(value = "文件名称")
    private String fileName;
    /**
     * 文件标识
     */
    @Excel(name = "文件标识", width = 15)
    @ApiModelProperty(value = "文件标识")
    private String fileKey;
    /**
     * 附件父id
     */
    @Excel(name = "附件父id", width = 15)
    @ApiModelProperty(value = "附件父id")
    private String pid;
    /**
     * 密级
     */
    @Excel(name = "密级", width = 15, dicCode = "system_security")
    @Dict(dicCode = "system_security")
    @ApiModelProperty(value = "密级")
    private String security;
    /**
     * 附件密级
     */
    @Dict(dicCode = "system_security")
    @ApiModelProperty(value = "附件密级")
    private Integer fileSecurity;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 附件类型
     */
    @Excel(name = "附件类型", width = 15)
    @ApiModelProperty(value = "附件类型")
    private String attachmentType;
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
}
