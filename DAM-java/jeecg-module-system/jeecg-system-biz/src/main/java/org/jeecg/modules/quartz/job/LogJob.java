package org.jeecg.modules.quartz.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.DictAspect;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.quartz.entity.LogParam;
import org.jeecg.modules.quartz.entity.SysLogExport;
import org.jeecg.modules.system.entity.SysLog;
import org.jeecg.modules.system.service.ISysLogService;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class LogJob implements Job {
    @Autowired
    DictAspect dictAspect;

    @Value("${k0.systemSecurity}")
    private Integer systemSecurity;
    @Autowired
    private ISysLogService sysLogService;
    @Autowired
    private BaseCommonService baseCommonService;

    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            LocalDate today = LocalDate.now();
            LogParam logParam = new ObjectMapper().readValue(this.parameter, LogParam.class);

//            this.export(0, logParam.path + "/", "admin日志", today);
            this.export(1, logParam.path + "/业务日志", "业务日志", today);
            this.export(2, logParam.path + "/系统管理日志", "系统管理日志", today);
            this.export(3, logParam.path + "/系统运行告警日志", "系统运行告警日志", today);
            this.export(4, logParam.path + "/安全管理日志", "安全管理日志", today);
            this.export(5, logParam.path + "/安全审计日志", "安全审计日志", today);

            if (logParam.autoDel) {
                LambdaQueryWrapper<SysLog> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.le(SysLog::getCreateTime, Date.from(today.minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                sysLogService.remove(queryWrapper);
            }

            String logContent = logParam.autoDel ? "自动备份 " + today + " 及以前的日志，并删除超过6个月的日志" : "自动备份 " + today + " 及以前的日志";
            baseCommonService.addLog(
                    systemSecurity,
                    3, //系统运行告警日志
                    6, "日志备份", logContent, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            baseCommonService.addLog(
                    systemSecurity,
                    3, //系统运行告警日志
                    6, "日志备份", "自动备份失败：" + e.getMessage(), "失败"
            );
        }
    }

    private void export(int logType, String path, String filename, LocalDate today) throws Exception {
        this.makeSureFolder(path);
        Date date = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        LambdaQueryWrapper<SysLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysLog::getLogType, logType);
        queryWrapper.le(SysLog::getCreateTime, date);
        Page<SysLog> page = new Page<>(1, Long.MAX_VALUE);
        IPage<SysLog> pageList = sysLogService.page(page, queryWrapper);
        Result<IPage<SysLog>> result = Result.OK(pageList);
        dictAspect.parseDictText(result);

        String title = "" + filename + " " + "导出于" + today;
        ExportParams exportParams = new ExportParams(title, "密级:" + this.getSecurityText(systemSecurity), filename);
        HSSFWorkbook workbook = (HSSFWorkbook) ExcelExportUtil.exportExcel(exportParams, SysLogExport.class, pageList.getRecords());
        FileOutputStream fos = new FileOutputStream(path + "/" + filename + " " + today + ".xls");
        workbook.write(fos);
        fos.flush();
        fos.close();
    }

    private String getSecurityText(int systemSecurity) {
        switch (systemSecurity) {
            case 1:
                return "公开";
            case 2:
                return "内部";
            case 3:
                return "秘密";
            case 4:
                return "机密";
            case 5:
                return "绝密";
        }
        return "";
    }

    private void makeSureFolder(String path) throws Exception {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (!success) {
                throw new Exception("创建文件夹 " + path + " 失败！");
            }
        }
    }
}
