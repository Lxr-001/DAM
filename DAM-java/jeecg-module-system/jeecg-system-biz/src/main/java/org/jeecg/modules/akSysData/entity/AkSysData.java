package org.jeecg.modules.akSysData.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 系统参数表
 * @Author: jeecg-boot
 * @Date:   2025-07-21
 * @Version: V1.0
 */
@Data
@TableName("ak_sys_data")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ak_sys_data对象", description="系统参数表")
public class AkSysData implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**启用密码策略*/
	@Excel(name = "启用密码策略", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "启用密码策略")
    private Integer usePassword;
	/**用户口令最小长度*/
	@Excel(name = "用户口令最小长度", width = 15)
    @ApiModelProperty(value = "用户口令最小长度")
    private Integer passwordMin;
	/**用户口令最大长度*/
	@Excel(name = "用户口令最大长度", width = 15)
    @ApiModelProperty(value = "用户口令最大长度")
    private Integer passwordMax;
	/**启用密码强度(3级)*/
	@Excel(name = "启用密码强度(3级)", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "启用密码强度(3级)")
    private Integer usePasswordStrength;
	/**新密码与历史密码重复检查次数*/
	@Excel(name = "新密码与历史密码重复检查次数", width = 15)
    @ApiModelProperty(value = "新密码与历史密码重复检查次数")
    private Integer testCount;
	/**密码到期提前提示（单位：天）*/
	@Excel(name = "密码到期提前提示（单位：天）", width = 15)
    @ApiModelProperty(value = "密码到期提前提示（单位：天）")
    private Integer beforeAlert;
	/**口令更改周期（单位：天）*/
	@Excel(name = "口令更改周期（单位：天）", width = 15)
    @ApiModelProperty(value = "口令更改周期（单位：天）")
    private Integer changeDate;
	/**允许的连续登录失败次数*/
	@Excel(name = "允许的连续登录失败次数", width = 15)
    @ApiModelProperty(value = "允许的连续登录失败次数")
    private Integer failTime;
	/**启用审计功能*/
	@Excel(name = "启用审计功能", width = 15, dicCode = "yn")
	@Dict(dicCode = "yn")
    @ApiModelProperty(value = "启用审计功能")
    private Integer useLog;
	/**token失效时间*/
	@Excel(name = "token失效时间", width = 15)
    @ApiModelProperty(value = "token失效时间")
    private Integer tokenTime;
}
