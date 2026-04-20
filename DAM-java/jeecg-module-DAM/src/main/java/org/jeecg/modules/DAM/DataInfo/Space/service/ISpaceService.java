package org.jeecg.modules.DAM.DataInfo.Space.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.DAM.DataInfo.Space.entity.Space;

/**
 * @Description: 数据空间
 * @Author: jeecg-boot
 * @Date:   2026-03-23
 * @Version: V1.0
 */
public interface ISpaceService extends IService<Space> {

    /**
     * 更新空间成员数量
     * @param spaceId 空间ID
     * @param userCount 成员数量
     */
    void updateUserCount(String spaceId, Integer userCount);

}