package org.jeecg.modules.DAM.DataInfo.SpaceUser.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.entity.SpaceUser;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.mapper.SpaceUserMapper;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.service.ISpaceUserService;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.InvitableUserVo;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.SpaceUserOptionVo;
import org.jeecg.modules.DAM.DataInfo.SpaceUser.vo.SpaceUserVo;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 数据空间用户关联表
 * @Author: jeecg-boot
 * @Date:   2026-04-17
 * @Version: V1.0
 */
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements ISpaceUserService {

    @Override
    public IPage<SpaceUserVo> selectSpaceUserDetailPage(Page<SpaceUserVo> page, String spaceId) {
        return this.baseMapper.selectSpaceUserDetailPage(page, spaceId);
    }

    @Override
    public IPage<SpaceUserOptionVo> selectInvitableUserPage(Page<SpaceUserOptionVo> page,
                                                             String spaceId,
                                                             String username,
                                                             String realname,
                                                             String departId,
                                                             Integer maxDataSecurity) {
        return this.baseMapper.selectInvitableUserPage(page, spaceId, username, realname, departId, maxDataSecurity);
    }

    @Override
    public IPage<InvitableUserVo> selectInvitableUserWithRolesPage(Page<InvitableUserVo> page,
                                                                    String spaceId,
                                                                    String username,
                                                                    String realname,
                                                                    String departId,
                                                                    Integer maxDataSecurity) {
        return this.baseMapper.selectInvitableUserWithRolesPage(page, spaceId, username, realname, departId, maxDataSecurity);
    }

}