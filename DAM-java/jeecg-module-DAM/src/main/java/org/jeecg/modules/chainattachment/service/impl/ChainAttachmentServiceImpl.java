package org.jeecg.modules.chainattachment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.chainattachment.entity.ChainAttachment;
import org.jeecg.modules.chainattachment.mapper.ChainAttachmentMapper;
import org.jeecg.modules.chainattachment.service.IChainAttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @Description: 产业链附件
 * @Author: jeecg-boot
 * @Date: 2024-01-29
 * @Version: V1.0
 */
@Service
public class ChainAttachmentServiceImpl extends ServiceImpl<ChainAttachmentMapper, ChainAttachment> implements IChainAttachmentService {
    @Resource
    private ChainAttachmentMapper attachmentMapper;

    @Value("${accessFile.location}")
    private String location;

    /**
     * 删除附件时通过 location + chain_attachment 表的 file_key 定位到本地的临时文件并进行删除
     *
     * @param id
     */
    @Override
    public void removeTempFileById(String id) {
//        根据 id 查询 chain_attachment 表的 file_key
        QueryWrapper<ChainAttachment> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        List<ChainAttachment> attachmentList = attachmentMapper.selectList(wrapper);
        deleteTempFilesByAttachmentList(attachmentList);
    }

    private void deleteTempFilesByAttachmentList(List<ChainAttachment> attachmentList) {
        for (ChainAttachment attachment : attachmentList) {
            File file = new File(location, attachment.getFileKey());
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param idList
     */
    @Override
    public void removeTempFilesByIdList(List<String> idList) {
        List<ChainAttachment> attachmentList = attachmentMapper.selectBatchIds(idList);
        deleteTempFilesByAttachmentList(attachmentList);
    }

    /**
     * @param pid
     */
    @Override
    public void removeTempFilesByPid(String pid) {
        // 根据 pid 查询 chain_attachment 表的 file_key
        QueryWrapper<ChainAttachment> wrapper = new QueryWrapper<>();
        wrapper.eq("pid", pid);
        List<ChainAttachment> attachmentList = attachmentMapper.selectList(wrapper);
        deleteTempFilesByAttachmentList(attachmentList);
    }

    /**
     * @param pidList
     */
    @Override
    public void removeTempFilesByPidList(List<String> pidList) {
        QueryWrapper<ChainAttachment> wrapper = new QueryWrapper<>();
        wrapper.in("pid", pidList);
        List<ChainAttachment> attachmentList = attachmentMapper.selectList(wrapper);
        deleteTempFilesByAttachmentList(attachmentList);
    }

    /**
     * @param pidList
     */
    @Override
    public void removeByPidList(List<String> pidList) {
        QueryWrapper<ChainAttachment> wrapper = new QueryWrapper<>();
        wrapper.in("pid", pidList);
        attachmentMapper.delete(wrapper);
    }
}
