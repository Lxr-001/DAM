package org.jeecg.modules.chainattachment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.chainattachment.entity.ChainAttachment;

import java.util.List;

/**
 * @Description: 产业链附件
 * @Author: jeecg-boot
 * @Date: 2024-01-29
 * @Version: V1.0
 */
public interface IChainAttachmentService extends IService<ChainAttachment> {

    void removeTempFileById(String id);

    void removeTempFilesByIdList(List<String> idList);

    void removeTempFilesByPid(String id);

    void removeTempFilesByPidList(List<String> idList);

    void removeByPidList(List<String> idList);
}
