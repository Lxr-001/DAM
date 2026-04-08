package org.jeecg.modules.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.quartz.entity.LogParam;
import org.jeecg.modules.quartz.entity.QuartzJob;
import org.jeecg.modules.quartz.service.IQuartzJobService;
import org.jeecg.modules.system.entity.SysLog;
import org.jeecg.modules.system.service.ISysLogService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.vo.LogParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 系统日志表 前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@RestController
@RequestMapping("/sys/log")
@Slf4j
public class SysLogController extends JeecgController<SysLog, ISysLogService> {

	@Value("${k0.systemSecurity}")
	private Integer systemSecurity;
	@Value("${jeecg.path.upload}")
	private String upLoadPath;
	@Autowired
	private BaseCommonService baseCommonService;
	@Autowired
	private ISysLogService sysLogService;
	@Autowired
	private ISysUserService sysUserService;
	@Autowired
	IQuartzJobService quartzJobService;

	/**
	 * 根据三员用户角色，返回能查看的日志类型，前端显示对应的Tab页
	 * 只在打开日志管理页面时记录一次日志，页面内条件查询不重复记录
	 */
	@GetMapping("/getLogTypeByUserRole")
	public Result<List<Integer>> getLogTypeByUserRole() {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		baseCommonService.addLog(
				Math.min(systemSecurity, user.getSecurity()),
				sysUserService.getLogTypeByUserName(user.getUsername()),
				1, "日志列表", "查看日志记录", "成功"
		);
		List<Integer> logTypeList = sysLogService.filterLogTypeByUserRole(user);
		return Result.OK(logTypeList);
	}

	/**
	 * @功能：查询日志记录
	 * @param syslog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysLog>> queryPageList(SysLog syslog,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
											   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysLog>> result = new Result<IPage<SysLog>>();
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int querySecurity = syslog.getSecurity() == null ? Math.min(systemSecurity, user.getSecurity()) : Math.min(syslog.getSecurity(), user.getSecurity());
		syslog.setSecurity(querySecurity);
		QueryWrapper<SysLog> queryWrapper = QueryGenerator.initQueryWrapper(syslog, req.getParameterMap());
		Page<SysLog> page = new Page<SysLog>(pageNo, pageSize);
		//日志关键词
		String keyWord = req.getParameter("keyWord");
		if(oConvertUtils.isNotEmpty(keyWord)) {
			queryWrapper.like("log_content",keyWord);
		}
		//TODO 过滤逻辑处理
		//TODO begin、end逻辑处理
		//TODO 一个强大的功能，前端传一个字段字符串，后台只返回这些字符串对应的字段
		//创建时间/创建人的赋值
		IPage<SysLog> pageList = sysLogService.page(page, queryWrapper);
		log.info("查询当前页：" + pageList.getCurrent());
		log.info("查询当前页数量：" + pageList.getSize());
		log.info("查询结果数量：" + pageList.getRecords().size());
		log.info("数据总数：" + pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

//	@RequestMapping(value = "/exportXls")
//	public ModelAndView exportXls(SysLog syslog, HttpServletRequest request) {
//		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//		int querySecurity = syslog.getSecurity() == null ? Math.min(systemSecurity, user.getSecurity()) : Math.min(syslog.getSecurity(), user.getSecurity());
//		syslog.setSecurity(querySecurity);
//		baseCommonService.addLog(
//				querySecurity,
//				sysUserService.getLogTypeByUserName(user.getUsername()),
//				6, "日志列表", "导出日志", "成功"
//		);
//
//		QueryWrapper<SysLog> queryWrapper = QueryGenerator.initQueryWrapper(syslog, request.getParameterMap());
//		List<SysLog> pageList = sysLogService.list(queryWrapper);
//		List<SysLog> exportList = pageList;
//
////        // 过滤选中数据
////        String selections = request.getParameter("selections");
////        if (oConvertUtils.isNotEmpty(selections)) {
////            List<String> selectionList = Arrays.asList(selections.split(","));
////            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
////        } else {
////            exportList = pageList;
////        }
//
//		String title = "日志";
//		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
//		mv.addObject(NormalExcelConstants.FILE_NAME, title);
//		mv.addObject(NormalExcelConstants.CLASS, SysLog.class);
//		ExportParams exportParams = new ExportParams(title, "密级:" + getSecurityText(querySecurity), title);
//		exportParams.setImageBasePath(upLoadPath);
//		mv.addObject(NormalExcelConstants.PARAMS, exportParams);
//		mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
//		mv.addObject(NormalExcelConstants.EXPORT_FIELDS, request.getParameterMap().get("field")[0]);
//		return mv;
//	}

	@RequestMapping(value = "/exportXls")
	@AutoLog()
	public ModelAndView exportXls(HttpServletRequest request, SysLog syslog) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		int querySecurity = syslog.getSecurity() == null ? Math.min(systemSecurity, user.getSecurity()) : Math.min(syslog.getSecurity(), user.getSecurity());
		syslog.setSecurity(querySecurity);
		baseCommonService.addLog(
				querySecurity,
				sysUserService.getLogTypeByUserName(user.getUsername()),
				6, "日志列表", "导出日志", "成功"
		);
		return super.exportXls(request, syslog, SysLog.class, "日志列表");
	}

	private String getSecurityText(int systemSecurity) {
		String[] texts = {"未知", "公开", "内部", "秘密", "机密", "绝密"};
		return texts[systemSecurity];
	}

	@GetMapping(value = "/queryLogParam")
	public Result<LogParamVO> queryLogParam() {
		try {
			LambdaQueryWrapper<QuartzJob> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(QuartzJob::getJobClassName, "org.jeecg.modules.quartz.job.LogJob");
			QuartzJob quartzJob = quartzJobService.list(queryWrapper).get(0);

			String cron = quartzJob.getCronExpression();
			int index = cron.indexOf("/");
			String interval = cron.substring(index + 1, index + 3).trim();
			LogParam logParam = new ObjectMapper().readValue(quartzJob.getParameter(), LogParam.class);

			LogParamVO logParamVO = new LogParamVO();
			logParamVO.setAutoBackup(quartzJob.getStatus() >= 0);
			logParamVO.setAutoDelete(logParam.autoDel);
			logParamVO.setInterval(Integer.parseInt(interval));
			logParamVO.setPath(logParam.path);
			return Result.OK(logParamVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result.error("", new LogParamVO());
	}

	@PostMapping(value = "/saveLogParam")
	public Result<String> saveLogParam(@RequestBody LogParamVO logParamVO) {
		try {
			while (logParamVO.path.endsWith("/") || logParamVO.path.endsWith("\\")) {
				logParamVO.path = logParamVO.path.substring(0, logParamVO.path.length() - 1);
			}
			LogParam logParam = new LogParam(logParamVO.autoDelete, logParamVO.path);
			String paramStr = new ObjectMapper().writeValueAsString(logParam);

			LambdaUpdateWrapper<QuartzJob> updateWrapper = new LambdaUpdateWrapper<>();
			updateWrapper.eq(QuartzJob::getJobClassName, "org.jeecg.modules.quartz.job.LogJob");
			updateWrapper.set(QuartzJob::getStatus, logParamVO.autoBackup ? 0 : -1);
			updateWrapper.set(QuartzJob::getCronExpression, "0 0 6 1/" + logParamVO.interval + " * ? *");
			updateWrapper.set(QuartzJob::getParameter, paramStr);
			quartzJobService.update(updateWrapper);

			String logContent = "修改日志备份设置，";
			if (logParamVO.autoBackup) {
				logContent += "自动备份：开启；";
				logContent += "自动删除超过6个月的日志：" + (logParamVO.autoDelete ? "开启；" : "关闭；");
				logContent += "备份间隔：" + logParamVO.interval + "天；";
				logContent += "保存路径：" + logParamVO.path;
			} else {
				logContent += "自动备份：关闭";
			}

			LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			baseCommonService.addLog(
					Math.min(systemSecurity, loginUser.getSecurity()),
					sysUserService.getLogTypeByUserName(loginUser.getUsername()),
					9,  //系统参数变更
					"日志备份设置", logContent, "成功");
			return Result.OK("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			baseCommonService.addLog(
					Math.min(systemSecurity, loginUser.getSecurity()),
					sysUserService.getLogTypeByUserName(loginUser.getUsername()),
					9,  //系统参数变更
					"日志备份设置", "修改日志备份设置失败：" + e.getMessage(), "失败");
			return Result.OK("保存失败：" + e.getMessage());
		}
	}



//	/**
//	 * @功能：删除单个日志记录
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
//	public Result<SysLog> delete(@RequestParam(name="id",required=true) String id) {
//		Result<SysLog> result = new Result<SysLog>();
//		SysLog sysLog = sysLogService.getById(id);
//		if(sysLog==null) {
//			result.error500("未找到对应实体");
//		}else {
//			boolean ok = sysLogService.removeById(id);
//			if(ok) {
//				result.success("删除成功!");
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * @功能：批量，全部清空日志记录
//	 * @param ids
//	 * @return
//	 */
//	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
//	public Result<SysRole> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		Result<SysRole> result = new Result<SysRole>();
//		if(ids==null || "".equals(ids.trim())) {
//			result.error500("参数不识别！");
//		}else {
//			if("allclear".equals(ids)) {
//				this.sysLogService.removeAll();
//				result.success("清除成功!");
//			}
//			this.sysLogService.removeByIds(Arrays.asList(ids.split(",")));
//			result.success("删除成功!");
//		}
//		return result;
//	}


}
