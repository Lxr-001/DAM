package org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 资产登记申请表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
@Data
@TableName("dam_asset_registration")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "dam_asset_registration对象", description = "资产登记申请表")
public class AssetRegistration implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /** 关联资产ID */
    @ApiModelProperty(value = "关联资产ID")
    private String assetId;

    /** 数据空间ID */
    @ApiModelProperty(value = "数据空间ID")
    private String spaceId;

    /** 登记类型：initial/transfer/change/cancel */
    @ApiModelProperty(value = "登记类型")
    private String registrationType;

    /** 申请人ID */
    @ApiModelProperty(value = "申请人ID")
    private String applicantId;

    /** 申请人姓名 */
    @ApiModelProperty(value = "申请人姓名")
    private String applicantName;

    /** 审批人ID */
    @ApiModelProperty(value = "审批人ID")
    private String approverId;

    /** 审批人姓名 */
    @ApiModelProperty(value = "审批人姓名")
    private String approverName;

    /** 审批状态：pending/approved/rejected */
    @ApiModelProperty(value = "审批状态")
    private String status;

    /** 原Owner用户ID（转让/变更登记用） */
    @ApiModelProperty(value = "原Owner用户ID")
    private String oldOwnerId;

    /** 原Owner姓名 */
    @ApiModelProperty(value = "原Owner姓名")
    private String oldOwnerName;

    /** 新Owner用户ID（转让登记用） */
    @ApiModelProperty(value = "新Owner用户ID")
    private String newOwnerId;

    /** 新Owner姓名 */
    @ApiModelProperty(value = "新Owner姓名")
    private String newOwnerName;

    /** 注销理由 */
    @ApiModelProperty(value = "注销理由")
    private String cancelReason;

    /** 登记表单完整数据（JSON格式） */
    @ApiModelProperty(value = "登记表单数据")
    private String registrationData;

    /** 提交时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提交时间")
    private Date submitTime;

    /** 审批时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审批时间")
    private Date approveTime;

    /** 创建人 */
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
