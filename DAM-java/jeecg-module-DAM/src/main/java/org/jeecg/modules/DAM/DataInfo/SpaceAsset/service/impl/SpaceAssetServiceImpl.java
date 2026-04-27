package org.jeecg.modules.DAM.DataInfo.SpaceAsset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.SpaceAsset;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity.AssetRegistration;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.mapper.SpaceAssetMapper;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.service.ISpaceAssetService;
import org.jeecg.modules.DAM.DataInfo.SpaceAsset.service.IAssetRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 数据资产主表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
@Service
public class SpaceAssetServiceImpl extends ServiceImpl<SpaceAssetMapper, SpaceAsset> implements ISpaceAssetService {

    @Autowired
    @Lazy
    private IAssetRegistrationService assetRegistrationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<SpaceAsset> selectAssetListBySpaceId(String spaceId, Map<String, Object> params) {
        return baseMapper.selectAssetListBySpaceId(spaceId, params);
    }

    @Override
    public boolean checkAssetNameDuplicate(String assetName, String spaceId) {
        int count = baseMapper.countByAssetNameAndSpaceId(assetName, spaceId);
        return count > 0;
    }

    @Override
    public boolean updateAssetStatus(String id, String status) {
        return baseMapper.updateAssetStatus(id, status) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitInitialRegistration(SpaceAsset asset, String approverId) {
        asset.setStatus("submitted");
        asset.setCreateTime(new Date());
        this.save(asset);

        AssetRegistration registration = new AssetRegistration();
        registration.setAssetId(asset.getId());
        registration.setSpaceId(asset.getSpaceId());
        registration.setRegistrationType("initial");
        registration.setApplicantId(asset.getOwnerId());
        registration.setApplicantName(asset.getOwnerName());
        registration.setApproverId(approverId);
        registration.setStatus("pending");
        registration.setNewOwnerId(asset.getOwnerId());
        registration.setNewOwnerName(asset.getOwnerName());
        try {
            registration.setRegistrationData(objectMapper.writeValueAsString(asset));
        } catch (Exception e) {
            registration.setRegistrationData("{}");
        }
        registration.setSubmitTime(new Date());
        return assetRegistrationService.save(registration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitTransferRegistration(String assetId, String newOwnerId, String approverId) {
        SpaceAsset asset = this.getById(assetId);
        if (asset == null) {
            return false;
        }

        AssetRegistration registration = new AssetRegistration();
        registration.setAssetId(assetId);
        registration.setSpaceId(asset.getSpaceId());
        registration.setRegistrationType("transfer");
        registration.setApplicantId(newOwnerId);
        registration.setOldOwnerId(asset.getOwnerId());
        registration.setOldOwnerName(asset.getOwnerName());
        registration.setNewOwnerId(newOwnerId);
        registration.setApproverId(approverId);
        registration.setStatus("pending");
        registration.setSubmitTime(new Date());
        return assetRegistrationService.save(registration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitChangeRegistration(SpaceAsset asset, String approverId) {
        SpaceAsset existingAsset = this.getById(asset.getId());
        if (existingAsset == null) {
            return false;
        }

        AssetRegistration registration = new AssetRegistration();
        registration.setAssetId(asset.getId());
        registration.setSpaceId(existingAsset.getSpaceId());
        registration.setRegistrationType("change");
        registration.setApplicantId(existingAsset.getOwnerId());
        registration.setApplicantName(existingAsset.getOwnerName());
        registration.setApproverId(approverId);
        registration.setStatus("pending");
        try {
            registration.setRegistrationData(objectMapper.writeValueAsString(asset));
        } catch (Exception e) {
            registration.setRegistrationData("{}");
        }
        registration.setSubmitTime(new Date());
        return assetRegistrationService.save(registration);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitCancelRegistration(String assetId, String cancelReason, String approverId) {
        SpaceAsset asset = this.getById(assetId);
        if (asset == null) {
            return false;
        }

        AssetRegistration registration = new AssetRegistration();
        registration.setAssetId(assetId);
        registration.setSpaceId(asset.getSpaceId());
        registration.setRegistrationType("cancel");
        registration.setApplicantId(asset.getOwnerId());
        registration.setApplicantName(asset.getOwnerName());
        registration.setApproverId(approverId);
        registration.setStatus("pending");
        registration.setCancelReason(cancelReason);
        registration.setSubmitTime(new Date());
        return assetRegistrationService.save(registration);
    }
}
