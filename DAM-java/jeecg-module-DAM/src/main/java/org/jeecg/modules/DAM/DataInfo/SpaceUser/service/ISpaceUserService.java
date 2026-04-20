package org.jeecg.modules.DAM.DataInfo.SpaceUser.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.entity.SpaceUser;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.InvitableUserVo;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.SpaceUserOptionVo;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.SpaceUserVo;

/**
 * @Description: 数据空间用户关联表
 * @Author: jeecg-boot
 * @Date:   2026-04-17
 * @Version: V1.0
 */
public interface ISpaceUserService extends IService<SpaceUser> {

    /**
     * 分页查询数据空间用户详情
     * @param page 分页参数
     * @param spaceId 数据空间ID
     * @return 用户详情分页列表
     */
    IPage<SpaceUserVo> selectSpaceUserDetailPage(Page<SpaceUserVo> page, String spaceId);

    /**
     * 分页查询可邀请的用户列表（包含角色信息）
     * @param page 分页参数
     * @param spaceId 数据空间ID（用于排除已邀请的用户）
     * @param username 用户账号（模糊查询）
     * @param realname 用户姓名（模糊查询）
     * @param departId 部门ID
     * @param maxDataSecurity 密级上限
     * @return 可邀请的用户列表
     */
    IPage<SpaceUserOptionVo> selectInvitableUserPage(Page<SpaceUserOptionVo> page,
                                                       String spaceId,
                                                       String username,
                                                       String realname,
                                                       String departId,
                                                       Integer maxDataSecurity);

    /**
     * 分页查询可邀请的用户列表（包含角色信息，通过USER_ROLE关联）
     * @param page 分页参数
     * @param spaceId 数据空间ID（用于排除已邀请的用户）
     * @param username 用户账号（模糊查询）
     * @param realname 用户姓名（模糊查询）
     * @param departId 部门ID
     * @param maxDataSecurity 密级上限
     * @return 可邀请的用户列表
     */
    IPage<InvitableUserVo> selectInvitableUserWithRolesPage(Page<InvitableUserVo> page,
                                                               String spaceId,
                                                               String username,
                                                               String realname,
                                                               String departId,
                                                               Integer maxDataSecurity);

}