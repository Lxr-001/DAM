package org.jeecg.modules.DAM.DataInfo.SpaceAsset.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.AssetRegistration;
import java.util.List;

/**
 * @Description: 资产登记申请表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
@Mapper
public interface AssetRegistrationMapper extends BaseMapper<AssetRegistration> {

    /**
     * 根据资产ID查询登记申请记录
     */
    List<AssetRegistration> selectByAssetId(@Param("assetId") String assetId);

    /**
     * 根据空间ID查询待审批的登记申请
     */
    List<AssetRegistration> selectPendingBySpaceId(@Param("spaceId") String spaceId);

    /**
     * 更新审批状态
     */
    int updateStatus(@Param("id") String id, @Param("status") String status);
}
