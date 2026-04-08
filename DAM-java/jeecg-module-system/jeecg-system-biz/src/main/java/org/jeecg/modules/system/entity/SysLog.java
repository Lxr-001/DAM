package org.jeecg.modules.system.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * <p>
 * 系统日志表
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysLog implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	@Excel(name = "时间", width = 32, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 密级（1.公开 2.内部 3.秘m 4.机m 5.jm）
	 */
	@Excel(dicCode = "system_security", name = "密级", width = 15)
	@Dict(dicCode = "system_security")
	private Integer security;

	/**
	 * IP
	 */
	@Excel(name = "IP地址", width = 15)
	private String ip;

	/**
	 * 操作人名称
	 */
	@Excel(name = "操作人名称", width = 15)
	private String username;

	/**
	 * 操作人ID
	 */
	@Excel(name = "操作人ID", width = 15)
	private String userid;

	/**
	 * 日志类型（1.系统业务功能 2.系统参数管理 3.系统运行告警 4.安全管理 5.安全审计）
	 */
	@Excel(dicCode = "log_type", name = "日志类型", width = 15)
	@Dict(dicCode = "log_type")
	private Integer logType;

	/**
	 * 操作类型（1.查询 2.添加 3.修改 4.删除 5.导入 6.导出 7.用户登录/退出 8.用户/角色变更 9.系统参数变更）
	 */
	@Excel(dicCode = "operate_type", name = "操作类型", width = 15)
	@Dict(dicCode = "operate_type")
	private Integer operateType;

	/**
	 * 操作对象（数据表名或业务对象名称）
	 */
	@Excel(name = "操作对象", width = 15)
	private String objectName;

	/**
	 * 操作详细内容（自拟，不超过1000字）
	 */
	@Excel(name = "操作内容", width = 64)
	private String logContent;

	/**
	 * 操作结果（成功/失败）
	 */
	@Excel(name = "操作结果", width = 15)
	private String result;


	private String createBy;
	private String updateBy;
	private Date updateTime;
	private Long costTime;
	private String requestParam;
	private String requestType;
	private String requestUrl;
	private String method;

	@Column(name="content_hash",length=64)
	private String contentHash;

	@PrePersist
	@PreUpdate
	public void generateHash(){
		this.contentHash = DigestUtils.sha256Hex(this.logContent);
	}
}
