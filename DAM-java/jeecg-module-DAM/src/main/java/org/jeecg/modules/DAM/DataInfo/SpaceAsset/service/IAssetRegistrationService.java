package org.jeecg.modules.DAM.DataInfo.SpaceAsset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.AssetRegistration;
import java.util.List;

/**
 * @Description: 资产登记申请表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
public interface IAssetRegistrationService extends IService<AssetRegistration> {

    /**
     * 根据资产ID查询登记申请记录
     */
    List<AssetRegistration> getRegistrationsByAssetId(String assetId);

    /**
     * 根据空间ID查询待审批的登记申请
     */
    List<AssetRegistration> getPendingRegistrations(String spaceId);

    /**
     * 审批登记申请
     */
    boolean approveRegistration(String registrationId);

    /**
     * 驳回登记申请
     */
    boolean rejectRegistration(String registrationId);
}
