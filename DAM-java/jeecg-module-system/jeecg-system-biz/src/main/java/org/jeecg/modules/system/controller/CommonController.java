package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.constant.enums.Enums;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.chainattachment.entity.ChainAttachment;
import org.jeecg.modules.chainattachment.service.IChainAttachmentService;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.FileEncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/common")
public class CommonController {
    @Autowired
    private IChainAttachmentService chainAttachmentService;
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;
    @Value("${accessFile.decryptLocation}")
    private String decryptLocation;//密文被解密后存储的临时路径
    @Value(value = "${jeecg.path.uploadJszt}")
    private String uploadpathJszt;
    @Value(value = "${jeecg.path.root}")
    private String rootPath;
    /**
     * 本地：local minio：minio 阿里：alioss
     */
    @Value(value = "${jeecg.uploadType}")
    private String uploadType;
    @Autowired
    private ISysDictService sysDictService;
    @Autowired
    private BaseCommonService baseCommonService;

    /**
     * @return
     * @Author 政辉
     */
    @GetMapping("/403")
    public Result<?> noauth() {
        return Result.error("没有权限，请联系管理员授权");
    }

    /**
     * 文件上传统一方法
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/upload")
    public Result<?> upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Result<?> result = new Result<>();
        String savePath = "";
        String bizPath = request.getParameter("biz");
        String fileSecretLevel = request.getParameter("fileSecretLevel");
        String secretName = "公开";   //默认公开
        if (StringUtils.isNotBlank(fileSecretLevel)) {
            secretName = Enums.secretLevelEnum.getByValue(Integer.parseInt(fileSecretLevel)).getName();
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");// 获取上传文件对象

        //校验不让上传压缩包
        String originalFilename = file.getOriginalFilename();
        String[] invalidTypes = {".zip", ".rar"};
        for (String invalidType : invalidTypes) {
            if (originalFilename.endsWith(invalidType)) {
                result.setMessage("上传失败！禁止上传压缩包");
                result.setSuccess(false);
                return result;
            }
        }


        if (oConvertUtils.isEmpty(bizPath)) {
            if (CommonConstant.UPLOAD_TYPE_OSS.equals(uploadType)) {
                //未指定目录，则用阿里云默认目录 upload
                bizPath = "upload";
                //result.setMessage("使用阿里云文件上传时，必须添加目录！");
                //result.setSuccess(false);
                //return result;
            } else {
                bizPath = "";
            }
        }
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
            //update-begin-author:lvdandan date:20200928 for:修改JEditor编辑器本地上传
            savePath = this.uploadLocal(file, bizPath, secretName);
            //update-begin-author:lvdandan date:20200928 for:修改JEditor编辑器本地上传
            /**  富文本编辑器及markdown本地上传时，采用返回链接方式
             //针对jeditor编辑器如何使 lcaol模式，采用 base64格式存储
             String jeditor = request.getParameter("jeditor");
             if(oConvertUtils.isNotEmpty(jeditor)){
             result.setMessage(CommonConstant.UPLOAD_TYPE_LOCAL);
             result.setSuccess(true);
             return result;
             }else{
             savePath = this.uploadLocal(file,bizPath);
             }
             */
        } else {
            //update-begin-author:taoyan date:20200814 for:文件上传改造
            savePath = CommonUtils.upload(file, bizPath, uploadType);
            //update-end-author:taoyan date:20200814 for:文件上传改造
        }
        if (oConvertUtils.isNotEmpty(savePath)) {
            result.setMessage(savePath);
            result.setSuccess(true);
        } else {
            result.setMessage("上传失败！");
            result.setSuccess(false);
        }
        return result;
    }

    @PostMapping(value = "/uploadJszt")
    public Result<?> uploadJszt(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Result<?> result = new Result<>();
        String savePath = "";
        String bizPath = request.getParameter("biz");
        String fileSecretLevel = request.getParameter("fileSecretLevel");
        String secretName = "公开";   //默认公开
        if (StringUtils.isNotBlank(fileSecretLevel)) {
            secretName = Enums.secretLevelEnum.getByValue(Integer.parseInt(fileSecretLevel)).getName();
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");// 获取上传文件对象

        //校验不让上传压缩包
        String originalFilename = file.getOriginalFilename();
        String[] invalidTypes = {".zip", ".rar"};
        for (String invalidType : invalidTypes) {
            if (originalFilename.endsWith(invalidType)) {
                result.setMessage("上传失败！禁止上传压缩包");
                result.setSuccess(false);
                return result;
            }
        }


        if (oConvertUtils.isEmpty(bizPath)) {
            if (CommonConstant.UPLOAD_TYPE_OSS.equals(uploadType)) {
                //未指定目录，则用阿里云默认目录 upload
                bizPath = "upload";
                //result.setMessage("使用阿里云文件上传时，必须添加目录！");
                //result.setSuccess(false);
                //return result;
            } else {
                bizPath = "";
            }
        }
        if (CommonConstant.UPLOAD_TYPE_LOCAL.equals(uploadType)) {
            //update-begin-author:lvdandan date:20200928 for:修改JEditor编辑器本地上传
            savePath = this.uploadLocalJszt(file, bizPath, secretName);
            //update-begin-author:lvdandan date:20200928 for:修改JEditor编辑器本地上传
            /**  富文本编辑器及markdown本地上传时，采用返回链接方式
             //针对jeditor编辑器如何使 lcaol模式，采用 base64格式存储
             String jeditor = request.getParameter("jeditor");
             if(oConvertUtils.isNotEmpty(jeditor)){
             result.setMessage(CommonConstant.UPLOAD_TYPE_LOCAL);
             result.setSuccess(true);
             return result;
             }else{
             savePath = this.uploadLocal(file,bizPath);
             }
             */
        } else {
            //update-begin-author:taoyan date:20200814 for:文件上传改造
            savePath = CommonUtils.upload(file, bizPath, uploadType);
            //update-end-author:taoyan date:20200814 for:文件上传改造
        }
        if (oConvertUtils.isNotEmpty(savePath)) {
            result.setMessage(savePath);
            result.setSuccess(true);
        } else {
            result.setMessage("上传失败！");
            result.setSuccess(false);
        }
        return result;
    }

    /**
     * 本地文件上传
     *
     * @param mf         文件
     * @param bizPath    自定义路径
     * @param secretName
     * @return
     */
    private String uploadLocal(MultipartFile mf, String bizPath, String secretName) {
        try {
            String ctxPath = uploadpath;
            String fileName = null;
            File file = new File(ctxPath + File.separator + bizPath + File.separator);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            String filename = mf.getOriginalFilename();// 获取文件名
            filename = CommonUtils.getFileName(filename);
            if (filename.contains(".")) {
                fileName = System.currentTimeMillis() + "_" + filename.substring(0, filename.lastIndexOf(".")) + filename.substring(filename.lastIndexOf("."));
            } else {
                fileName = filename + "_" + System.currentTimeMillis();
            }
            //附件增加密级标识
            fileName = secretName + "★" + fileName;
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
            FileCopyUtils.copy(mf.getBytes(), savefile);
            String dbpath = null;
            if (oConvertUtils.isNotEmpty(bizPath)) {
                dbpath = bizPath + File.separator + fileName;
            } else {
                dbpath = fileName;
            }
            if (dbpath.contains("\\")) {
                dbpath = dbpath.replace("\\", "/");
            }
            return dbpath;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    private String uploadLocalJszt(MultipartFile mf, String bizPath, String secretName) {
        try {
            String ctxPath = uploadpathJszt;
            String fileName = null;
            File file = new File(ctxPath + File.separator + bizPath + File.separator);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            String filename = mf.getOriginalFilename();// 获取文件名
            filename = CommonUtils.getFileName(filename);
            if (filename.contains(".")) {
                //fileName = System.currentTimeMillis() + "_" + filename.substring(0, filename.lastIndexOf(".")) + filename.substring(filename.lastIndexOf("."));
                fileName = filename.substring(0, filename.lastIndexOf(".")) + filename.substring(filename.lastIndexOf("."));
            } else {
                //fileName = filename + "_" + System.currentTimeMillis();
            }
            //附件增加密级标识
            fileName = secretName + "★" + fileName;
            //fileName = fileName + "-" + secretName;
            String savePath = file.getPath() + File.separator + fileName;
            File savefile = new File(savePath);
//            保存之前先加密
            //设置签名秘钥
            JeecgBaseConfig jeecgBaseConfig = SpringContextUtils.getBean(JeecgBaseConfig.class);
            MultipartFile encryptFile = FileEncryptionUtil.encryptFile(mf, jeecgBaseConfig.getSecretKey());
            FileCopyUtils.copy(encryptFile.getBytes(), savefile);
            String dbpath = null;
            if (oConvertUtils.isNotEmpty(bizPath)) {
                dbpath = bizPath + File.separator + fileName;
            } else {
                dbpath = fileName;
            }
            if (dbpath.contains("\\")) {
                dbpath = dbpath.replace("\\", "/");
            }
            return dbpath;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }

//	@PostMapping(value = "/upload2")
//	public Result<?> upload2(HttpServletRequest request, HttpServletResponse response) {
//		Result<?> result = new Result<>();
//		try {
//			String ctxPath = uploadpath;
//			String fileName = null;
//			String bizPath = "files";
//			String tempBizPath = request.getParameter("biz");
//			if(oConvertUtils.isNotEmpty(tempBizPath)){
//				bizPath = tempBizPath;
//			}
//			String nowday = new SimpleDateFormat("yyyyMMdd").format(new Date());
//			File file = new File(ctxPath + File.separator + bizPath + File.separator + nowday);
//			if (!file.exists()) {
//				file.mkdirs();// 创建文件根目录
//			}
//			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//			MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
//			String orgName = mf.getOriginalFilename();// 获取文件名
//			fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
//			String savePath = file.getPath() + File.separator + fileName;
//			File savefile = new File(savePath);
//			FileCopyUtils.copy(mf.getBytes(), savefile);
//			String dbpath = bizPath + File.separator + nowday + File.separator + fileName;
//			if (dbpath.contains("\\")) {
//				dbpath = dbpath.replace("\\", "/");
//			}
//			result.setMessage(dbpath);
//			result.setSuccess(true);
//		} catch (IOException e) {
//			result.setSuccess(false);
//			result.setMessage(e.getMessage());
//			log.error(e.getMessage(), e);
//		}
//		return result;
//	}

    /**
     * 预览图片&下载文件
     * 请求地址：http://localhost:8080/common/static/{user/20190119/e1fe9925bc315c60addea1b98eb1cb1349547719_1547866868179.jpg}
     *
     * @param request
     * @param response
     */
    @GetMapping(value = "/static/**")
    public void view(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException, UnsupportedEncodingException {
        // ISO-8859-1 ==> UTF-8 进行编码转换
        String imgPath = extractPathFromPattern(request);
        String isEncryption = request.getParameter("isEncryption");//下载文件是否是加密文件 如果是密文 需要先解密后下载到浏览器
        if (oConvertUtils.isEmpty(imgPath) || imgPath == "null") {
            return;
        }
        // 其余处理略
        InputStream inputStream = null;
        OutputStream outputStream = null;
        String filePath = "";//最终发给前端的文件路径
        String orgFilePath = uploadpath + File.separator + imgPath;//原始文件路径
        String decryptPath = decryptLocation + File.separator + imgPath;//解密后文件路径
        try {
            imgPath = imgPath.replace("..", "").replace("../", "");
            if (imgPath.endsWith(",")) {
                imgPath = imgPath.substring(0, imgPath.length() - 1);
            }
            if ("true".equals(isEncryption)) {//需要解密
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
                boolean res = DESUtils.decodeBase64File(orgFilePath, decryptPath);
                if (res) {
                    filePath = decryptPath;
                }
            } else {//直接下载不需解密
                filePath = orgFilePath;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                response.setStatus(404);
                throw new RuntimeException("文件[" + imgPath + "]不存在..");
            }
            QueryWrapper<ChainAttachment> chainAttachmentQueryWrapper = new QueryWrapper<>();
            chainAttachmentQueryWrapper.eq("file_key", imgPath);
            List<ChainAttachment> list = chainAttachmentService.list(chainAttachmentQueryWrapper);
            byte[] fileName = file.getName().getBytes(StandardCharsets.UTF_8);
            Integer fileSecurity = 0;
            String fileNameContent = new String(fileName, StandardCharsets.UTF_8);
            String security = "内部";
            List<DictModel> dictItems = sysDictService.getDictItems("security");
            if (list != null) {
                fileNameContent = list.get(0).getFileName();
                fileSecurity = list.get(0).getFileSecurity();
                if (fileSecurity != null) {
                    for (DictModel sysDict : dictItems) {
                        if (sysDict.getValue().equals(fileSecurity.toString())) {
                            security = sysDict.getText();
                        }
                    }
                    fileName = (security + "-" + fileNameContent).getBytes(StandardCharsets.UTF_8);
                } else {
                    fileName = fileNameContent.getBytes(StandardCharsets.UTF_8);
                }
            }
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName, StandardCharsets.ISO_8859_1));
            inputStream = new BufferedInputStream(new FileInputStream(filePath));
            outputStream = response.getOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            response.flushBuffer();
            //增加附件下载日志
            String realname = request.getParameter("userid");
            String userName = request.getParameter("user");
            LoginUser loginUser = new LoginUser();
            loginUser.setUsername(userName);
            loginUser.setRealname(realname.substring(0, realname.lastIndexOf("（")));
            baseCommonService.addLog(fileSecurity, 1, 10, "附件", fileNameContent + "-下载", "成功", loginUser);
        } catch (IOException e) {
            log.error("预览文件失败" + e.getMessage());
            response.setStatus(404);
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            //在前边输入输出流关闭后 文件不被占用后 删除解密文件
            if ("true".equals(isEncryption)) {//最后删除解密的文件（bm测评要求服务器不存明文 解密后的不允许存在）
                File fileDecryptDelete = new File(decryptPath);
                boolean isDel = fileDecryptDelete.delete();
                if (isDel) {
                    System.out.println("解密后明文删除成功");
                }
            }
        }
    }

    @GetMapping(value = "/staticJszt/**")
    public void view1(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException, UnsupportedEncodingException {
        String imgPath = extractPathFromPattern(request);
        if (oConvertUtils.isEmpty(imgPath) || CommonConstant.STRING_NULL.equals(imgPath)) {
            return;
        }
        imgPath = imgPath.replace("..", "").replace("../", "");
        if (imgPath.endsWith(SymbolConstant.COMMA)) {
            imgPath = imgPath.substring(0, imgPath.length() - 1);
        }
        String decodePath = PathDecoder.decodePath(imgPath);
        String downloadPath = uploadpathJszt + File.separator + decodePath;
        try {
            CommonUtils.writeFileContent(downloadPath, response);
        } catch (IOException e) {
            log.error("预览文件失败" + e.getMessage());
            response.setStatus(404);
            e.printStackTrace();
        }
        // ISO-8859-1 ==> UTF-8 进行编码转换
       /* String imgPath = extractPathFromPattern(request);
        if (oConvertUtils.isEmpty(imgPath) || imgPath == "null") {
            return;
        }
        // 其余处理略
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            imgPath = imgPath.replace("..", "").replace("../", "");
            if (imgPath.endsWith(",")) {
                imgPath = imgPath.substring(0, imgPath.length() - 1);
            }
            imgPath = PathDecoder.decodePath(imgPath);
            String separator = File.separator;
//            String filePath = uploadpath + File.separator + imgPath;
//    todo: 打包需要注意变更        linux 下为 \      windows 下为 /
            String filePath = uploadpath + separator + imgPath;

            File file = new File(filePath);
            if (!file.exists()) {
                filePath = rootPath + File.separator + imgPath; //验证根路径是否存在
                file = new File(filePath);
                if (!file.exists()) {
                    response.setStatus(404);
                    throw new RuntimeException("文件[" + imgPath + "]不存在..");
                }
            }
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes("UTF-8"), "iso-8859-1"));
            inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
            outputStream = response.getOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            response.flushBuffer();
        } catch (IOException e) {
            log.error("预览文件失败" + e.getMessage());
            response.setStatus(404);
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }*/
    }


    /**
     * @param modelAndView
     * @return
     * @功能：pdf预览Iframe
     */
    @RequestMapping("/pdf/pdfPreviewIframe")
    public ModelAndView pdfPreviewIframe(ModelAndView modelAndView) {
        modelAndView.setViewName("pdfPreviewIframe");
        return modelAndView;
    }

    /**
     * 把指定URL后的字符串全部截断当成参数
     * 这么做是为了防止URL中包含中文或者特殊字符（/等）时，匹配不了的问题
     *
     * @param request
     * @return
     */
    private static String extractPathFromPattern(final HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);
    }

}
