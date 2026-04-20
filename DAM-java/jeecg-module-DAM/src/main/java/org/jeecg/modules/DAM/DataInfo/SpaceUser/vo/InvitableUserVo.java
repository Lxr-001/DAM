package org.jeecg.modules.DAM.DataInfo.SpaceUser.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 可邀请用户VO
 * 包含用户信息和角色信息
 */
@Data
@ApiModel(value="可邀请用户VO", description="可邀请用户VO")
public class InvitableUserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private String id;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "用户姓名")
    private String realname;

    @ApiModelProperty(value = "角色名称")
    private String roleNames;
}