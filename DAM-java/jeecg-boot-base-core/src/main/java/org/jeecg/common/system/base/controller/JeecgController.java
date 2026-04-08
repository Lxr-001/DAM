package org.jeecg.common.system.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xkcoding.http.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.BidSecurityLessThanDataSecurityException;
import org.jeecg.common.system.util.SecurityLevelUtil;
import org.jeecg.common.system.util.UserSecurityLessThanDataSecurityException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: Controller基类
 * @Author: dangzhenghui@163.com
 * @Date: 2019-4-21 8:13
 * @Version: 1.0
 */
@Slf4j
public class JeecgController<T, S extends IService<T>> {
    /**
     * issues/2933 JeecgController注入service时改用protected修饰，能避免重复引用service
     */
    @Autowired
    protected S service;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @Value("${k0.systemSecurity}")
    private Integer systemSecurity;
    @Autowired
    private BaseCommonService baseCommonService;

    protected void addLog(String logContent, String rt,Integer logType, Integer operateType, String objectName) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        baseCommonService.addLog(Math.min(systemSecurity, loginUser.getSecurity()), logType, operateType,
                objectName, logContent, rt);
    }
    public static int getFileSecurity(int fileMj, String fj) {
        if (!StringUtil.isEmpty(fj)) {
            if (fj.contains("机密★")) {
                fileMj = 4;
            } else if (fj.contains("秘密★")) {
                fileMj = 3;
            } else if (fj.contains("内部★")) {
                fileMj = 2;
            }
        }
        return fileMj;
    }
    /**
     * 导出excel
     *
     * @param request
     */
    protected ModelAndView exportXls(HttpServletRequest request, T object, Class<T> clazz, String title) {
        // Step.1 组装查询条件
        QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        // Step.2 获取导出数据
        List<T> exportList = service.list(queryWrapper);

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, clazz);
        //update-begin--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置--------------------
        ExportParams exportParams = new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title);
        exportParams.setImageBasePath(upLoadPath);
        //update-end--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置----------------------
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);

        return mv;
    }

    /**
     * 带密级导出excel
     * 导出人替换为密级
     *
     * @param request
     */
    protected ModelAndView exportXlsWithSecurity(HttpServletRequest request, T object, Class<T> clazz, String title) {
        // Step.1 组装查询条件
        QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 过滤选中数据
        String selections = request.getParameter("selections");
        //计算得出的文件密级
        String securityLevelByCalculate = request.getParameter("securityLevelByCalculate");
        String securityText = SecurityLevelUtil.getSecurityNameByLevel(Integer.parseInt(securityLevelByCalculate));

        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        // Step.2 获取导出数据
        List<T> exportList = service.list(queryWrapper);

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, clazz);
        //update-begin--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置--------------------
        ExportParams exportParams = new ExportParams(title + "报表", "密级:" + securityText, title);
        exportParams.setImageBasePath(upLoadPath);
        //update-end--Author:liusq  Date:20210126 for：图片导出报错，ImageBasePath未设置----------------------
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        baseCommonService.addLog(Integer.parseInt(securityLevelByCalculate), 1, 6, title, "导出" + title + "数据信息" + exportList.size() + "条，文件密级：" + securityText, "成功");

        return mv;
    }

    /**
     * 根据每页sheet数量导出多sheet
     *
     * @param request
     * @param object       实体类
     * @param clazz        实体类class
     * @param title        标题
     * @param exportFields 导出字段自定义
     * @param pageNum      每个sheet的数据条数
     * @param request
     */
    protected ModelAndView exportXlsSheet(HttpServletRequest request, T object, Class<T> clazz, String title, String exportFields, Integer pageNum) {
        // Step.1 组装查询条件
        QueryWrapper<T> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // Step.2 计算分页sheet数据
        double total = service.count();
        int count = (int) Math.ceil(total / pageNum);
        //update-begin-author:liusq---date:20220629--for: 多sheet导出根据选择导出写法调整 ---
        // Step.3  过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        //update-end-author:liusq---date:20220629--for: 多sheet导出根据选择导出写法调整 ---
        // Step.4 多sheet处理
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Page<T> page = new Page<T>(i, pageNum);
            IPage<T> pageList = service.page(page, queryWrapper);
            List<T> exportList = pageList.getRecords();
            Map<String, Object> map = new HashMap<>(5);
            ExportParams exportParams = new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title + i, upLoadPath);
            exportParams.setType(ExcelType.XSSF);
            //map.put("title",exportParams);
            //表格Title
            map.put(NormalExcelConstants.PARAMS, exportParams);
            //表格对应实体
            map.put(NormalExcelConstants.CLASS, clazz);
            //数据集合
            map.put(NormalExcelConstants.DATA_LIST, exportList);
            listMap.add(map);
        }
        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.MAP_LIST, listMap);
        return mv;
    }


    /**
     * 根据权限导出excel，传入导出字段参数
     *
     * @param request
     */
    protected ModelAndView exportXls(HttpServletRequest request, T object, Class<T> clazz, String title, String exportFields) {
        ModelAndView mv = this.exportXls(request, object, clazz, title);
        mv.addObject(NormalExcelConstants.EXPORT_FIELDS, exportFields);
        return mv;
    }

    /**
     * 获取对象ID
     *
     * @return
     */
    private String getId(T item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    protected Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<T> clazz) {
        try {
            // 检查必要参数
            if (clazz == null) {
                return Result.error("文件导入失败:导入类不能为空！");
            }

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            if (multipartRequest == null) {
                return Result.error("文件导入失败:请求类型不正确！");
            }

            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            if (fileMap.isEmpty()) {
                return Result.error("文件导入失败:未找到上传文件！");
            }

            for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
                // 获取上传文件对象
                MultipartFile file = entity.getValue();
                if (file == null || file.isEmpty()) {
                    continue; // 跳过空文件
                }

                ImportParams params = new ImportParams();
                params.setTitleRows(2);
                params.setHeadRows(1);
                params.setNeedSave(true);

                // 保存文件流引用
                InputStream inputStream = null;
                try {
                    inputStream = file.getInputStream();
                    List<T> list = ExcelImportUtil.importExcel(inputStream, clazz, params);
                    if (list == null || list.isEmpty()) {
                        return Result.error("文件导入失败:导入数据为空！");
                    }

                    //update-begin-author:taoyan date:20190528 for:批量插入数据
                    long start = System.currentTimeMillis();
                    service.saveBatch(list);
                    //400条 saveBatch消耗时间1592毫秒  循环插入消耗时间1947毫秒
                    //1200条  saveBatch消耗时间3687毫秒 循环插入消耗时间5212毫秒
                    log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                    //update-end-author:taoyan date:20190528 for:批量插入数据
                    return Result.ok("文件导入成功！数据行数：" + list.size());
                } catch (Exception e) {
                    //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
                    String msg = e.getMessage();
                    log.error(msg, e);
                    if (msg != null && msg.contains("Duplicate entry")) {
                        return Result.error("文件导入失败:有重复数据！");
                    } else {
                        return Result.error("文件导入失败:" + (msg != null ? msg : "未知错误"));
                    }
                    //update-end-author:taoyan date:20211124 for: 导入数据重复增加提示
                } finally {
                    // 安全关闭文件流
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            log.error("关闭文件流失败", e);
                        }
                    }
                }
            }

            return Result.error("文件导入失败:未找到有效的上传文件！");
        } catch (Exception e) {
            log.error("文件导入异常", e);
            return Result.error("文件导入失败:" + e.getMessage());
        }
    }

    /**
     * 通过excel导入数据
     * 校验用户上传数据密级，并添加日志
     *
     * @param request
     * @param response
     * @return
     */
    protected Result<?> importExcelCheckSecurity(HttpServletRequest request, HttpServletResponse response, Class<T> clazz, String bid, String title, int bidSecurity) {
        try {
            // 检查必要参数
            if (clazz == null) {
                return Result.error("文件导入失败:导入类不能为空！");
            }

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            if (multipartRequest == null) {
                return Result.error("文件导入失败:请求类型不正确！");
            }

            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            if (fileMap.isEmpty()) {
                return Result.error("文件导入失败:未找到上传文件！");
            }

            for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
                // 获取上传文件对象
                MultipartFile file = entity.getValue();
                if (file == null || file.isEmpty()) {
                    continue; // 跳过空文件
                }

                ImportParams params = new ImportParams();
                params.setTitleRows(2);
                params.setHeadRows(1);
                params.setNeedSave(true);

                // 保存文件流引用
                InputStream inputStream = null;
                try {
                    inputStream = file.getInputStream();
                    List<T> list = ExcelImportUtil.importExcel(inputStream, clazz, params);
                    if (list == null || list.isEmpty()) {
                        return Result.error("文件导入失败:导入数据为空！");
                    }

                    // 添加文件密级超过用户自身密级校验
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    if (sysUser == null) {
                        return Result.error("文件导入失败:用户未登录！");
                    }

                    Integer userSecurity = sysUser.getSecurity();
                    if (userSecurity == null) {
                        return Result.error("文件导入失败:用户密级未设置！");
                    }

                    List<Integer> secrets = list.stream().map(obj -> {
                        try {
                            Method getSecretLevelMethod = obj.getClass().getMethod("getSecretLevel");
                            return (Integer) getSecretLevelMethod.invoke(obj);
                        } catch (Exception e) {
                            throw new RuntimeException(obj.getClass().getName() + "无法获取密级secretLevel属性", e);
                        }
                    }).collect(Collectors.toList());

                    // 过滤掉null值，避免空指针错误
                    List<Integer> validSecrets = secrets.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    if (validSecrets.isEmpty()) {
                        return Result.error("文件导入失败:数据密级全部为空！");
                    }

                    //上传文件的最大密级 securityLevel
                    int securityLevel = SecurityLevelUtil.calculateMaxSecurity(validSecrets);

                    String dataSecurityText = SecurityLevelUtil.getSecurityNameByLevel(securityLevel);
                    String userSecurityText = SecurityLevelUtil.getSecurityNameByLevel(userSecurity);

                    if (!StringUtils.isEmpty(bid) && bidSecurity < securityLevel) {
                        String bidSecurityText = SecurityLevelUtil.getSecurityNameByLevel(bidSecurity);
                        throw new BidSecurityLessThanDataSecurityException(bidSecurityText + "密级的根节点不能上传" + dataSecurityText + "密级的文件");
                    }
                    if (userSecurity < securityLevel) {
                        throw new UserSecurityLessThanDataSecurityException(userSecurityText + "密级用户不能上传" + dataSecurityText + "密级的文件");
                    }

                    if (!StringUtils.isEmpty(bid)) {
                        try {
                            Method methodBid = clazz.getMethod("setBid", String.class);
                            for (T t : list) {
                                if (t != null) {
                                    methodBid.invoke(t, bid);
                                }
                            }
                        } catch (NoSuchMethodException e) {
                            // 如果没有setBid方法，忽略此步骤
                            log.warn("类{}" + "没有setBid方法，跳过设置bid操作", clazz.getName());
                        }
                    }

                    long start = System.currentTimeMillis();
                    service.saveBatch(list);
                    //400条 saveBatch消耗时间1592毫秒  循环插入消耗时间1947毫秒
                    //1200条  saveBatch消耗时间3687毫秒 循环插入消耗时间5212毫秒
                    log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");

                    // 添加导入日志
                    String logTitle = (title != null ? title : "未知");
                    String logContent = "导入" + logTitle + "数据，共计" + list.size() + "条，文件密级：" + dataSecurityText;

                    // 确保baseCommonService不为null
                    if (baseCommonService != null) {
                        baseCommonService.addLog(securityLevel, 1, 5, logTitle, logContent, "成功");
                    } else {
                        log.warn("baseCommonService未注入，无法添加导入日志");
                    }

                    return Result.ok("文件导入成功！" + logContent);
                } catch (BidSecurityLessThanDataSecurityException ue) {
                    return Result.error("文件导入失败:" + ue.getMessage());
                } catch (UserSecurityLessThanDataSecurityException ue) {
                    return Result.error("文件导入失败:" + ue.getMessage());
                } catch (Exception e) {
                    //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
                    String msg = e.getMessage();
                    log.error(msg, e);
                    if (msg != null && msg.indexOf("Duplicate entry") >= 0) {
                        return Result.error("文件导入失败:有重复数据！");
                    } else {
                        return Result.error("文件导入失败:" + (msg != null ? msg : "未知错误"));
                    }
                    //update-end-author:taoyan date:20211124 for: 导入数据重复增加提示
                } finally {
                    // 安全关闭文件流
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            log.error("关闭文件流失败", e);
                        }
                    }
                }
            }

            return Result.error("文件导入失败:未找到有效的上传文件！");
        } catch (Exception e) {
            log.error("文件导入异常", e);
            return Result.error("文件导入失败:" + e.getMessage());
        }
    }

    /**
     * 公用excel导入方法，校验密级，设置bid
     */
    protected Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<T> clazz, String bid, Integer excelSecretLevel) {
        try {
            // 检查必要参数
            if (clazz == null) {
                return Result.error("文件导入失败:导入类不能为空！");
            }

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            if (multipartRequest == null) {
                return Result.error("文件导入失败:请求类型不正确！");
            }

            Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
            if (fileMap.isEmpty()) {
                return Result.error("文件导入失败:未找到上传文件！");
            }

            for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
                // 获取上传文件对象
                MultipartFile file = entity.getValue();
                if (file == null || file.isEmpty()) {
                    continue; // 跳过空文件
                }

                ImportParams params = new ImportParams();
                params.setTitleRows(2);
                params.setHeadRows(1);
                params.setNeedSave(true);

                // 保存文件流引用
                InputStream inputStream = null;
                try {
                    inputStream = file.getInputStream();
                    List<T> list = ExcelImportUtil.importExcel(inputStream, clazz, params);

                    // TODO：在此处对数据进行去重操作

                    if (excelSecretLevel == null) {
                        return Result.error("导入失败：请选择导入数据的最高密级！");
                    }
                    if (excelSecretLevel > systemSecurity) {
                        return Result.error("导入失败：数据密级高于系统密级！");
                    }
                    if (list == null || list.isEmpty()) {
                        return Result.error("导入失败：数据为空！");
                    }

                    // 检查必要方法是否存在
                    boolean hasGetBid = Arrays.stream(clazz.getMethods()).anyMatch(s -> s != null && s.getName().equals("getBid"));
                    boolean hasIsValid = Arrays.stream(clazz.getMethods()).anyMatch(s -> s != null && s.getName().equals("isValid"));
                    boolean hasGetSecretLevel = Arrays.stream(clazz.getMethods()).anyMatch(s -> s != null && s.getName().equals("getSecretLevel"));

                    if (!hasGetBid) {
                        return Result.error("导入失败：数据缺少父级ID字段！");
                    }
                    if (!hasIsValid) {
                        return Result.error("导入失败：数据未配置必填项校验！");
                    }
                    if (!hasGetSecretLevel) {
                        return Result.error("导入失败：数据缺少密级字段！");
                    }

                    // 获取必要方法
                    Method methodBid = clazz.getMethod("setBid", String.class);
                    Method methodValid = clazz.getMethod("isValid", clazz);
                    Method methodSecurity = clazz.getMethod("getSecretLevel");

                    int dataSecretLevel = 0;
                    for (int i = 0; i < list.size(); i++) {
                        T t = list.get(i);
                        if (t == null) {
                            continue; // 跳过空对象
                        }

                        try {
                            if (!(Boolean) methodValid.invoke(t, t)) {
                                return Result.error("导入失败：第 " + (i + 1) + " 条数据不满足必填项校验条件");
                            }
                            methodBid.invoke(t, bid);
                            Integer secretLevel = (Integer) methodSecurity.invoke(t);
                            if (secretLevel != null) {
                                dataSecretLevel = Math.max(secretLevel, dataSecretLevel);
                            }
                        } catch (Exception e) {
                            return Result.error("导入失败：第 " + (i + 1) + " 条数据处理错误：" + e.getMessage());
                        }
                    }

                    if (dataSecretLevel > excelSecretLevel) {
                        return Result.error("导入失败：数据密级高于所选文件密级！");
                    }

                    // 过滤掉空对象后保存
                    List<T> validList = list.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    if (!validList.isEmpty()) {
                        service.saveBatch(validList);
                    }

                    return Result.ok("导入成功！数据行数：" + validList.size());
                } catch (NoSuchMethodException e) {
                    return Result.error("导入失败：找不到必要的方法：" + e.getMessage());
                } catch (Exception e) {
                    //update-begin-author:taoyan date:20211124 for: 导入数据重复增加提示
                    String msg = e.getMessage();
                    log.error(msg, e);
                    if (msg != null && msg.contains("Duplicate entry")) {
                        return Result.error("导入失败:有重复数据！");
                    } else {
                        return Result.error("导入失败:" + (msg != null ? msg : "未知错误"));
                    }
                    //update-end-author:taoyan date:20211124 for: 导入数据重复增加提示
                } finally {
                    // 安全关闭文件流
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            log.error("关闭文件流失败", e);
                        }
                    }
                }
            }

            return Result.error("文件导入失败:未找到有效的上传文件！");
        } catch (Exception e) {
            log.error("文件导入异常", e);
            return Result.error("文件导入失败:" + e.getMessage());
        }
    }

}
