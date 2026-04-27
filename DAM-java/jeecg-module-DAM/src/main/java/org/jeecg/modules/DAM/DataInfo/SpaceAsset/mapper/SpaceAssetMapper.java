package org.jeecg.modules.DAM.DataInfo.SpaceAsset.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.SpaceAsset;
import java.util.List;
import java.util.Map;

/**
 * @Description: 数据资产主表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
@Mapper
public interface SpaceAssetMapper extends BaseMapper<SpaceAsset> {

    /**
     * 根据空间ID查询资产列表（带筛选条件）
     */
    List<SpaceAsset> selectAssetListBySpaceId(@Param("spaceId") String spaceId, @Param("params") Map<String, Object> params);

    /**
     * 根据资产名称校验是否在当前空间内重复
     */
    int countByAssetNameAndSpaceId(@Param("assetName") String assetName, @Param("spaceId") String spaceId);

    /**
     * 更新资产状态
     */
    int updateAssetStatus(@Param("id") String id, @Param("status") String status);
}
