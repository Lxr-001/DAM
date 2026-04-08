package org.jeecg.modules.chainattachment.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.enums.Enums;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DESUtils;
import org.jeecg.modules.chainattachment.entity.ChainAttachment;
import org.jeecg.modules.chainattachment.service.IChainAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Description: 产业链附件
 * @Author: jeecg-boot
 * @Date: 2024-01-29
 * @Version: V1.0
 */
@Api(tags = "产业链附件")
@RestController
@RequestMapping("/chainAttachment")
@Slf4j
public class ChainAttachmentController extends JeecgController<ChainAttachment, IChainAttachmentService> {
    @Autowired
    private IChainAttachmentService chainAttachmentService;

    @Value("${jeecg.path.upload}")
    private String uploadpath;
    @Value("${accessFile.decryptLocation}")
    private String decryptLocation;//密文被解密后存储的临时路径

    /**
     * 分页列表查询
     *
     * @param chainAttachment
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "产业链附件-分页列表查询")
    @ApiOperation(value = "产业链附件-分页列表查询", notes = "产业链附件-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<ChainAttachment>> queryPageList(ChainAttachment chainAttachment,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        HttpServletRequest req) {
        QueryWrapper<ChainAttachment> queryWrapper = QueryGenerator.initQueryWrapper(chainAttachment, req.getParameterMap());
        Page<ChainAttachment> page = new Page<ChainAttachment>(pageNo, pageSize);
        IPage<ChainAttachment> pageList = chainAttachmentService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param chainAttachment
     * @return
     */
    @AutoLog(value = "产业链附件-添加")
    @ApiOperation(value = "产业链附件-添加", notes = "产业链附件-添加")
    //@RequiresPermissions("chainattachment:chain_attachment:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ChainAttachment chainAttachment) {
        chainAttachmentService.save(chainAttachment);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param chainAttachment
     * @return
     */
    @AutoLog(value = "产业链附件-编辑")
    @ApiOperation(value = "产业链附件-编辑", notes = "产业链附件-编辑")
    //@RequiresPermissions("chainattachment:chain_attachment:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody ChainAttachment chainAttachment) {
        chainAttachmentService.updateById(chainAttachment);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "产业链附件-通过id删除")
    @ApiOperation(value = "产业链附件-通过id删除", notes = "产业链附件-通过id删除")
    //@RequiresPermissions("chainattachment:chain_attachment:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        // 删除附件时通过 location + chain_attachment 表的 file_key 定位到本地的临时文件先进行删除然后再删除数据库表中的记录
        chainAttachmentService.removeTempFileById(id);
        chainAttachmentService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "产业链附件-批量删除")
    @ApiOperation(value = "产业链附件-批量删除", notes = "产业链附件-批量删除")
    //@RequiresPermissions("chainattachment:chain_attachment:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        // 删除附件时通过 location + chain_attachment 表的 file_key 定位到本地的临时文件先进行删除然后再删除数据库表中的记录
        chainAttachmentService.removeTempFilesByIdList(idList);
        this.chainAttachmentService.removeByIds(idList);
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "产业链附件-通过id查询")
    @ApiOperation(value = "产业链附件-通过id查询", notes = "产业链附件-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<ChainAttachment> queryById(@RequestParam(name = "id", required = true) String id) {
        ChainAttachment chainAttachment = chainAttachmentService.getById(id);
        if (chainAttachment == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(chainAttachment);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param chainAttachment
     */
    //@RequiresPermissions("chainattachment:chain_attachment:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ChainAttachment chainAttachment) {
        return super.exportXls(request, chainAttachment, ChainAttachment.class, "产业链附件");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresPermissions("chainattachment:chain_attachment:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, ChainAttachment.class);
    }

    /**
     * 通过excel导入数据
     *
     * @return
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public Result<?> importExcel(@RequestParam(value = "files") MultipartFile[] files, String pid, Integer security, String remark) throws IOException {
        for (MultipartFile file : files) {
            // 获取上传文件对象
            String filename = file.getOriginalFilename();
            // 获取密级中文描述并拼接到fileKey开头
            String securityName = "";
            try {
                Enums.secretLevelEnum securityEnum = Enums.secretLevelEnum.getByValue(security);
                if (securityEnum != null) {
                    securityName = securityEnum.getName();
                }
            } catch (Exception e) {
                log.warn("获取密级描述失败，使用默认值", e);
                securityName = "机密";
            }
            String fileKey = securityName + "-" + UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));
            String separator = File.separator;
            String filePath = uploadpath + separator + fileKey;
            //文件加密
            InputStream inputs = file.getInputStream();
            try {
                DESUtils.encodeBase64File(inputs, filePath, null);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
//            file.transferTo(new File(filePath));

            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            String username = loginUser.getUsername();
            ChainAttachment chainAttachment = new ChainAttachment();
            chainAttachment.setRemark(remark);
            chainAttachment.setCreateBy(username);
            chainAttachment.setCreateTime(new Date());
            chainAttachment.setFileKey(fileKey);
            chainAttachment.setFileName(filename);
            chainAttachment.setSecurity(String.valueOf(security));
            chainAttachment.setPid(pid);
            chainAttachmentService.save(chainAttachment);
        }
        return Result.ok("文件上传成功！");
    }

    /**
     * 下载文件（自定义文件名）
     * 下载时文件名显示为：密级-原始文件名
     *
     * @param fileKey  文件key
     * @param response
     */
    @ApiOperation(value = "下载文件", notes = "下载文件，文件名格式为：密级-原始文件名")
    @GetMapping(value = "/download/**")
    public void downloadFile(@RequestParam(name = "fileKey") String fileKey, HttpServletResponse response) {
        try {
            // 根据fileKey查找文件信息
            QueryWrapper<ChainAttachment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("file_key", fileKey);
            ChainAttachment chainAttachment = chainAttachmentService.getOne(queryWrapper);

            if (chainAttachment == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            String filePath = uploadpath + File.separator + fileKey;
            String decryptPath = decryptLocation + File.separator + fileKey;//解密后文件路径
            File dir = new File(decryptLocation);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("无法创建解密临时目录：" + decryptLocation);
                } else {
                    System.out.println("目录创建成功" + decryptLocation);
                }
            } else {
                System.out.println("解密文件临时目录已存在" + decryptLocation);
            }
            boolean res = DESUtils.decodeBase64File(filePath, decryptPath);
            if (res) {
                filePath = decryptPath;
            }
            File file = new File(filePath);

            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            // 构造下载文件名：密级-原始文件名
            String originalFileName = chainAttachment.getFileName();
            String securityName = "";
            try {
                Enums.secretLevelEnum securityEnum = Enums.secretLevelEnum.getByValue(Integer.parseInt(chainAttachment.getSecurity()));
                if (securityEnum != null) {
                    securityName = securityEnum.getName();
                }
            } catch (Exception e) {
                log.warn("获取密级描述失败，使用默认值", e);
                securityName = "机密";
            }

            String downloadFileName = securityName + "-" + originalFileName;
            String encodedFileName = URLEncoder.encode(downloadFileName, "UTF-8");

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
            response.setContentLength((int) file.length());

            // 输出文件流
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

        } catch (Exception e) {
            log.error("文件下载失败", e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("文件下载失败: " + e.getMessage());
            } catch (IOException ioException) {
                log.error("写入错误响应失败", ioException);
            }
        }
    }

}
