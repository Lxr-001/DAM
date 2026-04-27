package org.jeecg.modules.DAM.DataInfo.SpaceAsset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.AssetRegistration;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.SpaceAsset;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.mapper.AssetRegistrationMapper;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.service.IAssetRegistrationService;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.service.ISpaceAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * @Description: 资产登记申请表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
@Service
public class AssetRegistrationServiceImpl extends ServiceImpl<AssetRegistrationMapper, AssetRegistration> implements IAssetRegistrationService {

    @Autowired
    private ISpaceAssetService spaceAssetService;

    @Override
    public List<AssetRegistration> getRegistrationsByAssetId(String assetId) {
        return baseMapper.selectByAssetId(assetId);
    }

    @Override
    public List<AssetRegistration> getPendingRegistrations(String spaceId) {
        return baseMapper.selectPendingBySpaceId(spaceId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean approveRegistration(String registrationId) {
        AssetRegistration registration = this.getById(registrationId);
        if (registration == null) {
            return false;
        }

        registration.setStatus("approved");
        registration.setApproveTime(new Date());
        this.updateById(registration);

        if ("transfer".equals(registration.getRegistrationType())) {
            SpaceAsset asset = spaceAssetService.getById(registration.getAssetId());
            if (asset != null) {
                asset.setOwnerId(registration.getNewOwnerId());
                asset.setOwnerName(registration.getNewOwnerName());
                asset.setUpdateTime(new Date());
                spaceAssetService.updateById(asset);
            }
        } else if ("cancel".equals(registration.getRegistrationType())) {
            spaceAssetService.updateAssetStatus(registration.getAssetId(), "cancelled");
        } else if ("change".equals(registration.getRegistrationType())) {
            spaceAssetService.updateAssetStatus(registration.getAssetId(), "approved");
        } else if ("initial".equals(registration.getRegistrationType())) {
            spaceAssetService.updateAssetStatus(registration.getAssetId(), "approved");
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rejectRegistration(String registrationId) {
        AssetRegistration registration = this.getById(registrationId);
        if (registration == null) {
            return false;
        }

        registration.setStatus("rejected");
        registration.setApproveTime(new Date());
        this.updateById(registration);

        if (!"transfer".equals(registration.getRegistrationType())) {
            spaceAssetService.updateAssetStatus(registration.getAssetId(), "rejected");
        }

        return true;
    }
}
