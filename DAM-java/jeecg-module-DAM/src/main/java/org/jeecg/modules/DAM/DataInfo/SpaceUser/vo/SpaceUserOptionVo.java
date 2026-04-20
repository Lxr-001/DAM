package org.jeecg.modules.DAM.DataInfo.SpaceUser.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 数据空间邀请用户VO
 * 包含用户账号、姓名、角色等信息
 */
@Data
@ApiModel(value="数据空间邀请用户VO", description="数据空间邀请用户VO")
public class SpaceUserOptionVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private String id;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "用户姓名")
    private String realname;

    @ApiModelProperty(value = "角色名称（多个用逗号分隔）")
    private String roleNames;
}