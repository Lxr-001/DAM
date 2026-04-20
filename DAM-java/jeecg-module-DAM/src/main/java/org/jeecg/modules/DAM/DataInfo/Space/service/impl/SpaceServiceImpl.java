package org.jeecg.modules.DAM.DataInfo.Space.service.impl;

import org.jeecg.modules.DAM.DataInfo.Space.entity.Space;
import org.jeecg.modules.DAM.DataInfo.Space.mapper.SpaceMapper;
import org.jeecg.modules.DAM.DataInfo.Space.service.ISpaceService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 数据空间
 * @Author: jeecg-boot
 * @Date:   2026-03-23
 * @Version: V1.0
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements ISpaceService {

    @Override
    public void updateUserCount(String spaceId, Integer userCount) {
        this.baseMapper.updateUserCount(spaceId, userCount);
    }

}