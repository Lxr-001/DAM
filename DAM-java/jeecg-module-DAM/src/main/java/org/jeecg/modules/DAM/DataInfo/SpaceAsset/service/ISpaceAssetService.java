package org.jeecg.modules.DAM.DataInfo.SpaceAsset.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.SpaceAsset;
import java.util.List;
import java.util.Map;

/**
 * @Description: 数据资产主表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
public interface ISpaceAssetService extends IService<SpaceAsset> {

    /**
     * 根据空间ID查询资产列表（带筛选条件）
     */
    List<SpaceAsset> selectAssetListBySpaceId(String spaceId, Map<String, Object> params);

    /**
     * 根据资产名称校验是否在当前空间内重复
     */
    boolean checkAssetNameDuplicate(String assetName, String spaceId);

    /**
     * 更新资产状态
     */
    boolean updateAssetStatus(String id, String status);

    /**
     * 提交首次登记申请
     */
    boolean submitInitialRegistration(SpaceAsset asset, String approverId);

    /**
     * 提交转让登记申请
     */
    boolean submitTransferRegistration(String assetId, String newOwnerId, String approverId);

    /**
     * 提交变更登记申请
     */
    boolean submitChangeRegistration(SpaceAsset asset, String approverId);

    /**
     * 提交注销登记申请
     */
    boolean submitCancelRegistration(String assetId, String cancelReason, String approverId);
}
