package org.jeecg.common.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeecg.common.api.dto.LogDTO;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.enums.ModuleType;
import org.jeecg.common.constant.enums.OperateTypeEnum;
import org.jeecg.common.system.query.MatchTypeEnum;
import org.jeecg.common.system.query.QueryCondition;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.SysPermissionDataRuleModel;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.IpUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.oConvertUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 系统日志，切面处理类
 *
 * @Author scott
 * @email jeecgos@163.com
 * @Date 2018年1月14日
 */
@Slf4j
@Aspect
@Component
public class AutoLogAspect {

    private List<String> queryParams = new ArrayList<>();

    @Resource
    private BaseCommonService baseCommonService;

    @Pointcut("@annotation(org.jeecg.common.aspect.annotation.AutoLog)")
    public void logPointCut() {

    }

    //@Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String methodName = signature.getName();

        if (methodName.equalsIgnoreCase("queryById")) {
            //queryById肯定是从页面点进来的，不重复记查询日志
            return point.proceed();
        }

        AutoLog autoLog = method.getAnnotation(AutoLog.class);
        ApiModel apiModel = null;
        Integer security = 0;
        String json = "";
        try {
            // 传参顺序、类型不同
            // 导出：第二个参数为Entity
            // 查、增、改：第一个参数为Entity
            // 删除：第一个参数为id
            // 批量删除：第一个参数为ids
            Object param;
            if (methodName.startsWith("export")) {
                param = point.getArgs()[1];
            } else {
                param = point.getArgs()[0];
            }

            Class<?> clzEntity;
            if (!methodName.startsWith("delete")) {
                clzEntity = param.getClass();
                if (Arrays.stream(clzEntity.getMethods()).anyMatch(s -> s.getName().equals("getSecretLevel"))){
                    Method methodSecurity = clzEntity.getMethod("getSecretLevel");
                    security = (Integer) methodSecurity.invoke(param);
                    apiModel = clzEntity.getAnnotation(ApiModel.class);
                    json = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(param);
                }
            } else {
                //参数只给了id，需要从controller拿到service，到数据库查记录的密级
                Class<?> clzController = method.getDeclaringClass();
                Object controller = clzController.cast(point.getTarget());
                Method getService = clzController.getMethod("get" + clzController.getSimpleName().substring(0, clzController.getSimpleName().length() - 10) + "Service");
                IService<?> service = (IService<?>) getService.invoke(controller);

                if (methodName.equalsIgnoreCase("delete")) {
                    String id = (String) point.getArgs()[0];
                    Object entity = service.getById(id);
                    clzEntity = entity.getClass();
                    apiModel = clzEntity.getAnnotation(ApiModel.class);
                    Method methodSecurity = clzEntity.getMethod("getSecretLevel");
                    security = (Integer) methodSecurity.invoke(entity);
                    json = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(entity);
                } else if (methodName.equalsIgnoreCase("deleteBatch")) {
                    String[] ids = ((String) point.getArgs()[0]).split(",");
                    List<?> entityList = service.listByIds(Arrays.asList(ids));
                    Object entity = entityList.get(0);
                    clzEntity = entity.getClass();
                    apiModel = clzEntity.getAnnotation(ApiModel.class);
                    Method methodSecurity = clzEntity.getMethod("getSecretLevel");
                    security = 0;
                    for (Object e : entityList) {
                        Integer sec = (Integer) methodSecurity.invoke(e);
                        if (sec > security) {
                            security = sec;
                        }
                    }
                    json = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(entityList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return point.proceed();
        }

        //执行方法
        Object resultObj = point.proceed();

//        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Integer logType = SecurityUtils.getSubject().hasRole("admin") ? 0 : 1;
        Integer operateType = 0;
        String objectName = apiModel == null ? "" : apiModel.description();
        String logContent = "";

        String result = "成功";
        if (!methodName.startsWith("export")) {
            // 其他操作判断下，导出就直接成功
            result = ((Result<?>) resultObj).isSuccess() ? "成功" : "失败";
        }

//        if (autoLog != null) {
//            security = autoLog.security();
//            logType = autoLog.logType();
//            operateType = autoLog.operateType();
//            objectName = autoLog.objectName();
//            logContent = autoLog.value();
//            result = autoLog.result();
//        }
        operateType = getOperateType(methodName, operateType);

        Object entity = null;
        Class<?> clzEntity = null;
        this.queryParams.clear();
        switch (operateType) {
            case 1:
                entity = point.getArgs()[0];
                clzEntity = entity.getClass();
                this.initQueryWrapper(clzEntity.cast(entity), ((HttpServletRequest) point.getArgs()[3]).getParameterMap());
                logContent = "查询记录，筛选条件：" + String.join("；", this.queryParams);
                break;
            case 2:
                logContent = "新增记录：" + json;
                break;
            case 3:
                logContent = "修改记录：" + json;
                break;
            case 4:
                logContent = "删除记录：" + json;
                break;
            case 5:
                security= (Integer) point.getArgs()[4];
                logContent = "导入记录";
                break;
            case 6:
                entity = point.getArgs()[1];
                clzEntity = entity.getClass();
                this.initQueryWrapper(clzEntity.cast(entity), ((HttpServletRequest) point.getArgs()[0]).getParameterMap());
                logContent = "导出记录，筛选条件：" + String.join("；", this.queryParams);
                break;
            default:
                break;
        }

        baseCommonService.addLog(security, logType, operateType, objectName, logContent, result);
        return resultObj;

    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, Object obj) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        LogDTO dto = new LogDTO();
        AutoLog syslog = method.getAnnotation(AutoLog.class);
        if (syslog != null) {
            //update-begin-author:taoyan date:
            String content = syslog.value();
            if (syslog.module() == ModuleType.ONLINE) {
                content = getOnlineLogContent(obj, content);
            }
            //注解上的描述,操作日志内容
            dto.setLogType(syslog.logType());
            dto.setLogContent(content);
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        dto.setMethod(className + "." + methodName + "()");


        //设置操作类型
        if (CommonConstant.LOG_TYPE_2 == dto.getLogType()) {
            dto.setOperateType(getOperateType(methodName, syslog.operateType()));
        }

        //获取request
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        //请求的参数
        dto.setRequestParam(getReqestParams(request, joinPoint));
        //设置IP地址
        dto.setIp(IpUtils.getIpAddr(request));
        //获取登录用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser != null) {
            dto.setUserid(sysUser.getUsername());
            dto.setUsername(sysUser.getRealname());

        }
        //耗时
        dto.setCostTime(time);
        dto.setCreateTime(new Date());
        //保存系统日志
        baseCommonService.addLog(dto);
    }


    /**
     * 获取操作类型
     */
    private int getOperateType(String methodName, int operateType) {
        if (operateType > 0) {
            return operateType;
        }
        if (methodName.startsWith("list") || methodName.startsWith("query")) {
            return CommonConstant.OPERATE_TYPE_1;
        }
        if (methodName.startsWith("add")) {
            return CommonConstant.OPERATE_TYPE_2;
        }
        if (methodName.startsWith("edit")) {
            return CommonConstant.OPERATE_TYPE_3;
        }
        if (methodName.startsWith("delete")) {
            return CommonConstant.OPERATE_TYPE_4;
        }
        if (methodName.startsWith("import")) {
            return CommonConstant.OPERATE_TYPE_5;
        }
        if (methodName.startsWith("export")) {
            return CommonConstant.OPERATE_TYPE_6;
        }
        return CommonConstant.OPERATE_TYPE_1;
    }

    /**
     * @param request:   request
     * @param joinPoint: joinPoint
     * @Description: 获取请求参数
     * @author: scott
     * @date: 2020/4/16 0:10
     * @Return: java.lang.String
     */
    private String getReqestParams(HttpServletRequest request, JoinPoint joinPoint) {
        String httpMethod = request.getMethod();
        String params = "";
        if (CommonConstant.HTTP_POST.equals(httpMethod) || CommonConstant.HTTP_PUT.equals(httpMethod) || CommonConstant.HTTP_PATCH.equals(httpMethod)) {
            Object[] paramsArray = joinPoint.getArgs();
            // java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
            //  https://my.oschina.net/mengzhang6/blog/2395893
            Object[] arguments = new Object[paramsArray.length];
            for (int i = 0; i < paramsArray.length; i++) {
                if (paramsArray[i] instanceof BindingResult || paramsArray[i] instanceof ServletRequest || paramsArray[i] instanceof ServletResponse || paramsArray[i] instanceof MultipartFile) {
                    //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                    //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                    continue;
                }
                arguments[i] = paramsArray[i];
            }
            //update-begin-author:taoyan date:20200724 for:日志数据太长的直接过滤掉
            PropertyFilter profilter = new PropertyFilter() {
                @Override
                public boolean apply(Object o, String name, Object value) {
                    int length = 500;
                    if (value != null && value.toString().length() > length) {
                        return false;
                    }
                    return true;
                }
            };
            params = JSONObject.toJSONString(arguments, profilter);
            //update-end-author:taoyan date:20200724 for:日志数据太长的直接过滤掉
        } else {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            // 请求的方法参数值
            Object[] args = joinPoint.getArgs();
            // 请求的方法参数名称
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = u.getParameterNames(method);
            if (args != null && paramNames != null) {
                for (int i = 0; i < args.length; i++) {
                    params += "  " + paramNames[i] + ": " + args[i];
                }
            }
        }
        return params;
    }

    /**
     * online日志内容拼接
     *
     * @param obj
     * @param content
     * @return
     */
    private String getOnlineLogContent(Object obj, String content) {
        if (Result.class.isInstance(obj)) {
            Result res = (Result) obj;
            String msg = res.getMessage();
            String tableName = res.getOnlTable();
            if (oConvertUtils.isNotEmpty(tableName)) {
                content += ",表名:" + tableName;
            }
            if (res.isSuccess()) {
                content += "," + (oConvertUtils.isEmpty(msg) ? "操作成功" : msg);
            } else {
                content += "," + (oConvertUtils.isEmpty(msg) ? "操作失败" : msg);
            }
        }
        return content;
    }




    /*    private void saveSysLog(ProceedingJoinPoint joinPoint, long time, Object obj) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLog sysLog = new SysLog();
        AutoLog syslog = method.getAnnotation(AutoLog.class);
        if(syslog != null){
            //update-begin-author:taoyan date:
            String content = syslog.value();
            if(syslog.module()== ModuleType.ONLINE){
                content = getOnlineLogContent(obj, content);
            }
            //注解上的描述,操作日志内容
            sysLog.setLogContent(content);
            sysLog.setLogType(syslog.logType());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");


        //设置操作类型
        if (sysLog.getLogType() == CommonConstant.LOG_TYPE_2) {
            sysLog.setOperateType(getOperateType(methodName, syslog.operateType()));
        }

        //获取request
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        //请求的参数
        sysLog.setRequestParam(getReqestParams(request,joinPoint));

        //设置IP地址
        sysLog.setIp(IPUtils.getIpAddr(request));

        //获取登录用户信息
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        if(sysUser!=null){
            sysLog.setUserid(sysUser.getUsername());
            sysLog.setUsername(sysUser.getRealname());

        }
        //耗时
        sysLog.setCostTime(time);
        sysLog.setCreateTime(new Date());
        //保存系统日志
        sysLogService.save(sysLog);
    }*/

    public static final String SQL_RULES_COLUMN = "SQL_RULES_COLUMN";
    private static final String BEGIN = "_begin";
    private static final String END = "_end";
    private static final String MULTI = "_MultiString";
    private static final String COMMA = ",";
    private static final String SUPER_QUERY_PARAMS = "superQueryParams";
    private static final String SUPER_QUERY_MATCH_TYPE = "superQueryMatchType";

    private <T> QueryWrapper<T> initQueryWrapper(T searchObj, Map<String, String[]> parameterMap) {
        long start = System.currentTimeMillis();
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        installMplus(queryWrapper, searchObj, parameterMap);
        return queryWrapper;
    }

    private void installMplus(QueryWrapper<?> queryWrapper, Object searchObj, Map<String, String[]> parameterMap) {

		/*
		 * 注意:权限查询由前端配置数据规则 当一个人有多个所属部门时候 可以在规则配置包含条件 orgCode 包含 #{sys_org_code}
		但是不支持在自定义SQL中写orgCode in #{sys_org_code}
		当一个人只有一个部门 就直接配置等于条件: orgCode 等于 #{sys_org_code} 或者配置自定义SQL: orgCode = '#{sys_org_code}'
		*/

        //区间条件组装 模糊查询 高级查询组装 简单排序 权限查询
        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(searchObj);
        Map<String, SysPermissionDataRuleModel> ruleMap = QueryGenerator.getRuleMap();

        //权限规则自定义SQL表达式
        for (String c : ruleMap.keySet()) {
            if (oConvertUtils.isNotEmpty(c) && c.startsWith(SQL_RULES_COLUMN)) {
                queryWrapper.and(i -> i.apply(QueryGenerator.getSqlRuleValue(ruleMap.get(c).getRuleValue())));
            }
        }

        String name, type, column;
        // update-begin--Author:taoyan  Date:20200923 for：issues/1671 如果字段加注解了@TableField(exist = false),不走DB查询-------
        //定义实体字段和数据库字段名称的映射 高级查询中 只能获取实体字段 如果设置TableField注解 那么查询条件会出问题
        Map<String, String> fieldColumnMap = new HashMap<String, String>();
        for (int i = 0; i < origDescriptors.length; i++) {
            //aliasName = origDescriptors[i].getName();  mybatis  不存在实体属性 不用处理别名的情况
            name = origDescriptors[i].getName();
            type = origDescriptors[i].getPropertyType().toString();
            try {
                if (QueryGenerator.judgedIsUselessField(name) || !PropertyUtils.isReadable(searchObj, name)) {
                    continue;
                }

                Object value = PropertyUtils.getSimpleProperty(searchObj, name);
                column = QueryGenerator.getTableFieldName(searchObj.getClass(), name);
                if (column == null) {
                    //column为null只有一种情况 那就是 添加了注解@TableField(exist = false) 后续都不用处理了
                    continue;
                }
                fieldColumnMap.put(name, column);
                //数据权限查询
                if (ruleMap.containsKey(name)) {
                    QueryGenerator.addRuleToQueryWrapper(ruleMap.get(name), column, origDescriptors[i].getPropertyType(), queryWrapper);
                }
                //区间查询
                this.doIntervalQuery(queryWrapper, parameterMap, type, name, column);
                //判断单值  参数带不同标识字符串 走不同的查询
                //TODO 这种前后带逗号的支持分割后模糊查询(多选字段查询生效) 示例：,1,3,
                if (null != value && value.toString().startsWith(COMMA) && value.toString().endsWith(COMMA)) {
                    String multiLikeval = value.toString().replace(",,", COMMA);
                    String[] vals = multiLikeval.substring(1, multiLikeval.length()).split(COMMA);
                    final String field = oConvertUtils.camelToUnderline(column);
                    if (vals.length > 1) {
                        queryWrapper.and(j -> {
                            log.info("---查询过滤器，Query规则---field:{}, rule:{}, value:{}", field, "like", vals[0]);
                            j = j.like(field, vals[0]);
                            for (int k = 1; k < vals.length; k++) {
                                j = j.or().like(field, vals[k]);
                                log.info("---查询过滤器，Query规则 .or()---field:{}, rule:{}, value:{}", field, "like", vals[k]);
                            }
                            //return j;
                        });
                    } else {
                        log.info("---查询过滤器，Query规则---field:{}, rule:{}, value:{}", field, "like", vals[0]);
                        queryWrapper.and(j -> j.like(field, vals[0]));
                    }
                } else if ("security".equals(column) && null != value) { //密级单独处理
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    value = Math.min(sysUser.getSecurity(), Integer.valueOf(value.toString()));
                    addEasyQuery(queryWrapper, column, QueryRuleEnum.LE, value);
                }else if ("secretLevel".equals(column) && null != value) { //限制用户密级
                    LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                    value = Math.min(sysUser.getSecurity(), Integer.valueOf(value.toString()));
                    addEasyQuery(queryWrapper, column, QueryRuleEnum.LE, value);
                } else {
                    //根据参数值带什么关键字符串判断走什么类型的查询
                    QueryRuleEnum rule = QueryGenerator.convert2Rule(value);
                    value = QueryGenerator.replaceValue(rule, value);
                    // add -begin 添加判断为字符串时设为全模糊查询
                    //if( (rule==null || QueryRuleEnum.EQ.equals(rule)) && "class java.lang.String".equals(type)) {
                    // 可以设置左右模糊或全模糊，因人而异
                    //rule = QueryRuleEnum.LIKE;
                    //}
                    // add -end 添加判断为字符串时设为全模糊查询
                    addEasyQuery(queryWrapper, column, rule, value);
                }

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        // 排序逻辑 处理
        QueryGenerator.doMultiFieldsOrder(queryWrapper, parameterMap, fieldColumnMap);

        //高级查询
        this.doSuperQuery(queryWrapper, parameterMap, fieldColumnMap);
        // update-end--Author:taoyan  Date:20200923 for：issues/1671 如果字段加注解了@TableField(exist = false),不走DB查询-------

    }

    private void doIntervalQuery(QueryWrapper<?> queryWrapper, Map<String, String[]> parameterMap, String type, String filedName, String columnName) throws ParseException {
        // 添加 判断是否有区间值
        String endValue = null, beginValue = null;
        if (parameterMap != null && parameterMap.containsKey(filedName + BEGIN)) {
            beginValue = parameterMap.get(filedName + BEGIN)[0].trim();
            addQueryByRule(queryWrapper, columnName, type, beginValue, QueryRuleEnum.GE);

        }
        if (parameterMap != null && parameterMap.containsKey(filedName + END)) {
            endValue = parameterMap.get(filedName + END)[0].trim();
            addQueryByRule(queryWrapper, columnName, type, endValue, QueryRuleEnum.LE);
        }
        //多值查询
        if (parameterMap != null && parameterMap.containsKey(filedName + MULTI)) {
            endValue = parameterMap.get(filedName + MULTI)[0].trim();
            addQueryByRule(queryWrapper, columnName.replace(MULTI, ""), type, endValue, QueryRuleEnum.IN);
        }
    }

    private void addQueryByRule(QueryWrapper<?> queryWrapper, String name, String type, String value, QueryRuleEnum rule) throws ParseException {
        if (oConvertUtils.isNotEmpty(value)) {
            //update-begin--Author:sunjianlei  Date:20220104 for：【JTC-409】修复逗号分割情况下没有转换类型，导致类型严格的数据库查询报错 -------------------
            // 针对数字类型字段，多值查询
            if (value.contains(COMMA)) {
                Object[] temp = Arrays.stream(value.split(COMMA)).map(v -> {
                    try {
                        return QueryGenerator.parseByType(v, type, rule);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return v;
                    }
                }).toArray();
                this.addEasyQuery(queryWrapper, name, rule, temp);
                return;
            }
            Object temp = QueryGenerator.parseByType(value, type, rule);
            addEasyQuery(queryWrapper, name, rule, temp);
            //update-end--Author:sunjianlei  Date:20220104 for：【JTC-409】修复逗号分割情况下没有转换类型，导致类型严格的数据库查询报错 -------------------
        }
    }

    private void doSuperQuery(QueryWrapper<?> queryWrapper, Map<String, String[]> parameterMap, Map<String, String> fieldColumnMap) {
        if (parameterMap != null && parameterMap.containsKey(SUPER_QUERY_PARAMS)) {
            String superQueryParams = parameterMap.get(SUPER_QUERY_PARAMS)[0];
            String superQueryMatchType = parameterMap.get(SUPER_QUERY_MATCH_TYPE) != null ? parameterMap.get(SUPER_QUERY_MATCH_TYPE)[0] : MatchTypeEnum.AND.getValue();
            MatchTypeEnum matchType = MatchTypeEnum.getByValue(superQueryMatchType);
            // update-begin--Author:sunjianlei  Date:20200325 for：高级查询的条件要用括号括起来，防止和用户的其他条件冲突 -------
            try {
                superQueryParams = URLDecoder.decode(superQueryParams, "UTF-8");
                List<QueryCondition> conditions = JSON.parseArray(superQueryParams, QueryCondition.class);
                if (conditions == null || conditions.size() == 0) {
                    return;
                }
                // update-begin-author:sunjianlei date:20220119 for: 【JTC-573】 过滤空条件查询，防止 sql 拼接多余的 and
                List<QueryCondition> filterConditions = conditions.stream().filter(
                        rule -> oConvertUtils.isNotEmpty(rule.getField())
                                && oConvertUtils.isNotEmpty(rule.getRule())
                                && oConvertUtils.isNotEmpty(rule.getVal())
                ).collect(Collectors.toList());
                if (filterConditions.size() == 0) {
                    return;
                }
                // update-end-author:sunjianlei date:20220119 for: 【JTC-573】 过滤空条件查询，防止 sql 拼接多余的 and
                log.info("---高级查询参数-->" + filterConditions);

                queryWrapper.and(andWrapper -> {
                    for (int i = 0; i < filterConditions.size(); i++) {
                        QueryCondition rule = filterConditions.get(i);
                        if (oConvertUtils.isNotEmpty(rule.getField())
                                && oConvertUtils.isNotEmpty(rule.getRule())
                                && oConvertUtils.isNotEmpty(rule.getVal())) {

                            log.debug("SuperQuery ==> " + rule.toString());

                            //update-begin-author:taoyan date:20201228 for: 【高级查询】 oracle 日期等于查询报错
                            Object queryValue = rule.getVal();
                            if ("date".equals(rule.getType())) {
                                queryValue = DateUtils.str2Date(rule.getVal(), DateUtils.date_sdf.get());
                            } else if ("datetime".equals(rule.getType())) {
                                queryValue = DateUtils.str2Date(rule.getVal(), DateUtils.datetimeFormat.get());
                            }
                            // update-begin--author:sunjianlei date:20210702 for：【/issues/I3VR8E】高级查询没有类型转换，查询参数都是字符串类型 ----
                            String dbType = rule.getDbType();
                            if (oConvertUtils.isNotEmpty(dbType)) {
                                try {
                                    String valueStr = String.valueOf(queryValue);
                                    switch (dbType.toLowerCase().trim()) {
                                        case "int":
                                            queryValue = Integer.parseInt(valueStr);
                                            break;
                                        case "bigdecimal":
                                            queryValue = new BigDecimal(valueStr);
                                            break;
                                        case "short":
                                            queryValue = Short.parseShort(valueStr);
                                            break;
                                        case "long":
                                            queryValue = Long.parseLong(valueStr);
                                            break;
                                        case "float":
                                            queryValue = Float.parseFloat(valueStr);
                                            break;
                                        case "double":
                                            queryValue = Double.parseDouble(valueStr);
                                            break;
                                        case "boolean":
                                            queryValue = Boolean.parseBoolean(valueStr);
                                            break;
                                    }
                                } catch (Exception e) {
                                    log.error("高级查询值转换失败：", e);
                                }
                            }
                            // update-begin--author:sunjianlei date:20210702 for：【/issues/I3VR8E】高级查询没有类型转换，查询参数都是字符串类型 ----
                            this.addEasyQuery(andWrapper, fieldColumnMap.get(rule.getField()), QueryRuleEnum.getByValue(rule.getRule()), queryValue);
                            //update-end-author:taoyan date:20201228 for: 【高级查询】 oracle 日期等于查询报错

                            // 如果拼接方式是OR，就拼接OR
                            if (MatchTypeEnum.OR == matchType && i < (filterConditions.size() - 1)) {
                                andWrapper.or();
                            }
                        }
                    }
                    //return andWrapper;
                });
            } catch (UnsupportedEncodingException e) {
                log.error("--高级查询参数转码失败：" + superQueryParams, e);
            } catch (Exception e) {
                log.error("--高级查询拼接失败：" + e.getMessage());
                e.printStackTrace();
            }
            // update-end--Author:sunjianlei  Date:20200325 for：高级查询的条件要用括号括起来，防止和用户的其他条件冲突 -------
        }
        //log.info(" superQuery getCustomSqlSegment: "+ queryWrapper.getCustomSqlSegment());
    }

    private void addEasyQuery(QueryWrapper<?> queryWrapper, String name, QueryRuleEnum rule, Object value) {
        if (value == null || rule == null || oConvertUtils.isEmpty(value)) {
            return;
        }
        name = oConvertUtils.camelToUnderline(name);
        String ruleText = rule.getValue();
        switch (rule) {
            case GT:
                queryWrapper.gt(name, value);
                ruleText = "大于";
                break;
            case GE:
                queryWrapper.ge(name, value);
                ruleText = "大于等于";
                break;
            case LT:
                queryWrapper.lt(name, value);
                ruleText = "小于";
                break;
            case LE:
                queryWrapper.le(name, value);
                ruleText = "小于等于";
                break;
            case EQ:
            case EQ_WITH_ADD:
                queryWrapper.eq(name, value);
                ruleText = "等于";
                break;
            case NE:
                queryWrapper.ne(name, value);
                ruleText = "不等于";
                break;
            case IN:
                if (value instanceof String) {
                    queryWrapper.in(name, (Object[]) value.toString().split(COMMA));
                } else if (value instanceof String[]) {
                    queryWrapper.in(name, (Object[]) value);
                }
                //update-begin-author:taoyan date:20200909 for:【bug】in 类型多值查询 不适配postgresql #1671
                else if (value.getClass().isArray()) {
                    queryWrapper.in(name, (Object[]) value);
                } else {
                    queryWrapper.in(name, value);
                }
                //update-end-author:taoyan date:20200909 for:【bug】in 类型多值查询 不适配postgresql #1671
                ruleText = "在...中";
                break;
            case LIKE:
                queryWrapper.like(name, value);
                ruleText = "包含";
                break;
            case LEFT_LIKE:
                queryWrapper.likeLeft(name, value);
                ruleText = "以...开始";
                break;
            case RIGHT_LIKE:
                queryWrapper.likeRight(name, value);
                ruleText = "以...结尾";
                break;
            default:
                log.info("--查询规则未匹配到---");
                break;
        }
        this.queryParams.add("字段名:{" + name + "}, 规则:{" + ruleText + "}, 数值:{" + value + "}");
    }


}
