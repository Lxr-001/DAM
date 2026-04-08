package org.jeecg.modules.DAM.DataInfo.Space.entity;

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
 * @Description: 数据空间
 * @Author: jeecg-boot
 * @Date:   2026-03-23
 * @Version: V1.0
 */
@Data
@TableName("dam_space")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="dam_space对象", description="数据空间")
public class Space implements Serializable {
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
	/**部门ID*/
	@Excel(name = "部门ID", width = 15)
    @ApiModelProperty(value = "部门ID")
    private String departId;
	/**部门名称*/
	@Excel(name = "部门名称", width = 15)
    @ApiModelProperty(value = "部门名称")
    private String departName;
    /**空间名称*/
    @Excel(name = "空间名称", width = 15)
    @ApiModelProperty(value = "空间名称")
    private java.lang.String spaceName;
	/**空间类型*/
	@Excel(name = "空间类型", width = 15)
    @ApiModelProperty(value = "空间类型")
    private String spaceType;
	/**空间容量*/
	@Excel(name = "空间容量", width = 15)
    @ApiModelProperty(value = "空间容量")
    private String spaceCapacity;
	/**成员数*/
	@Excel(name = "成员数", width = 15)
    @ApiModelProperty(value = "成员数")
    private String userCount;
}
