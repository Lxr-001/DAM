package org.jeecg.modules.DAM.DataInfo.SpaceUser.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据空间用户详情VO
 * 包含用户账号、姓名、部门、角色等信息
 */
@Data
@ApiModel(value="数据空间用户详情VO", description="数据空间用户详情VO")
public class SpaceUserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据空间用户关联ID")
    private String id;

    @ApiModelProperty(value = "数据空间ID")
    private String spaceId;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "用户姓名")
    private String realname;

    @ApiModelProperty(value = "部门ID")
    private String departId;

    @ApiModelProperty(value = "部门名称")
    private String departName;

    @ApiModelProperty(value = "角色名称（多个用逗号分隔）")
    private String roleNames;

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
