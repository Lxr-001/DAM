package org.jeecg.modules.DAM.DataInfo.Space.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.DAM.DataInfo.Space.entity.Space;

/**
 * @Description: 数据空间
 * @Author: jeecg-boot
 * @Date:   2026-03-23
 * @Version: V1.0
 */
public interface SpaceMapper extends BaseMapper<Space> {

    /**
     * 更新空间的成员数量
     * @param spaceId 空间ID
     * @param userCount 成员数量
     */
    void updateUserCount(@Param("spaceId") String spaceId, @Param("userCount") Integer userCount);

}
