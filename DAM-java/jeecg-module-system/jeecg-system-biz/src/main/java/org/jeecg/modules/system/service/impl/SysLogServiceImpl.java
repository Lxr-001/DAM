package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.modules.system.entity.SysLog;
import org.jeecg.modules.system.mapper.SysLogMapper;
import org.jeecg.modules.system.service.ISysLogService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 系统日志表 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements ISysLogService {

	@Resource
	private SysLogMapper sysLogMapper;

	@Autowired
	private ISysUserService sysUserService;

	@Override
	public List<Integer> filterLogTypeByUserRole(LoginUser user) {
		// 三员用户只能具有一个用户角色
		List<String> roleCodeList = sysUserService.getRole(user.getUsername());
		if (roleCodeList.size() != 1) {
			return new ArrayList<>();
		}
		String roleCode = roleCodeList.get(0);

		//日志类型（1.系统业务功能 2.系统参数管理 3.系统运行告警 4.安全管理 5.安全审计）
		Integer[] sysadminLogType = {3};
		Integer[] secadminLogType = {5};
		Integer[] secauditLogType = {1, 2, 3, 4};
		Integer[] adminLogType = {0, 1, 2, 3, 4, 5, 6};
		if ("sysadmin".equalsIgnoreCase(roleCode)) {
			return new ArrayList<>(Arrays.asList(sysadminLogType));
		}
		if ("secadmin".equalsIgnoreCase(roleCode)) {
			return new ArrayList<>(Arrays.asList(secadminLogType));
		}
		if ("secaudit".equalsIgnoreCase(roleCode)) {
			return new ArrayList<>(Arrays.asList(secauditLogType));
		}
		if ("admin".equalsIgnoreCase(roleCode)) {
			return new ArrayList<>(Arrays.asList(adminLogType));
		}
		return new ArrayList<>();
	}


	/**
	 * @功能：清空所有日志记录
	 */
	@Override
	public void removeAll() {
		sysLogMapper.removeAll();
	}

	@Override
	public Long findTotalVisitCount() {
		return sysLogMapper.findTotalVisitCount();
	}

	//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
	@Override
	public Long findTodayVisitCount(Date dayStart, Date dayEnd) {
		return sysLogMapper.findTodayVisitCount(dayStart,dayEnd);
	}

	@Override
	public Long findTodayIp(Date dayStart, Date dayEnd) {
		return sysLogMapper.findTodayIp(dayStart,dayEnd);
	}
	//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数

	@Override
	public List<Map<String,Object>> findVisitCount(Date dayStart, Date dayEnd) {
		DbType dbType = CommonUtils.getDatabaseTypeEnum();
		return sysLogMapper.findVisitCount(dayStart, dayEnd,dbType.getDb());
	}
}
