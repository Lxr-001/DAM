package org.jeecg.common.aspect.annotation;

import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.ModuleType;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @Author scott
 * @email jeecgos@163.com
 * @Date 2019年1月14日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoLog {
	/**
	 * 密级（1.公开 2.内部 3.秘m 4.机m 5.jm）
	 */
	int security() default 1;

	/**
	 * 日志类型（1.系统业务功能 2.系统参数管理 3.系统运行告警 4.安全管理 5.安全审计）
	 */
	int logType() default 1;

	/**
	 * 操作类型（1.查询 2.添加 3.修改 4.删除 5.导入 6.导出 7.用户登录/退出 8.用户/角色变更 9.系统参数变更）
	 */
	int operateType() default 0;

	/**
	 * 操作对象（数据表名或业务对象名称）
	 */
	String objectName() default "";

	/**
	 * 操作详细内容（自拟，不超过1000字）
	 */
	String value() default "";

	/**
	 * 操作结果（成功/失败）
	 */
	String result() default "成功";

	/**
	 * 模块类型 默认为common
	 * JEECG框架自带，H5未使用
	 */
	ModuleType module() default ModuleType.COMMON;
}
