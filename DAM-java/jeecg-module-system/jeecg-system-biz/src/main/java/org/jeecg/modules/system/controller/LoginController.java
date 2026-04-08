package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.SymbolConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.util.SecurityLevelUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.*;
import org.jeecg.common.util.encryption.EncryptedString;
import org.jeecg.config.JeecgBaseConfig;
import org.jeecg.modules.akSysData.entity.AkSysData;
import org.jeecg.modules.akSysData.service.IAkSysDataService;
import org.jeecg.modules.base.service.BaseCommonService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysRoleIndex;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.model.SysLoginModel;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.service.impl.SysBaseApiImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author scott
 * @since 2018-12-17
 */
@RestController
@RequestMapping("/sys")
@Api(tags = "用户登录")
@Slf4j
public class LoginController {
    @Value("${loginTime.sevenDay}")
    private long sevenDay;
    @Value("${loginTime.fifteen}")
    private long fifteen;
    @Value("${loginTime.month}")
    private long month;
    @Autowired
    private IAkSysDataService akSysDataService;

    @Value("${k0.systemSecurity}")
    private Integer systemSecurity;

    @Value("${k0.sysadminIps}")
    private String sysadminIps;

    @Value("${k0.secadminIps}")
    private String secadminIps;

    @Value("${k0.secauditIps}")
    private String secauditIps;

    @Value("${k0.ipValidationEnabled}")
    private Boolean ipValidationEnabled;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysPermissionService sysPermissionService;
    @Autowired
    private SysBaseApiImpl sysBaseApi;
    @Autowired
    private ISysLogService logService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private ISysTenantService sysTenantService;
    @Autowired
    private ISysDictService sysDictService;
    @Resource
    private BaseCommonService baseCommonService;

    @Autowired
    private JeecgBaseConfig jeecgBaseConfig;

    @Autowired
    public RedisTemplate redisTemplate;
    @Value("${accessFile.location}")
    private String location;
    private final String BASE_CHECK_CODES = "qwertyuiplkjhgfdsazxcvbnmQWERTYUPLKJHGFDSAZXCVBNM1234567890";

    /**
     * 用于显示系统密级标识
     */
    @GetMapping(value = "/getSystemSecurityText")
    public Result<String> getSystemSecurityText() {
        String[] texts = {"未知", "公开", "内部", "秘密", "机密", "绝密"};
        return Result.OK("成功", texts[systemSecurity]);
    }

    @ApiOperation("登录接口")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<JSONObject> login(@RequestBody SysLoginModel sysLoginModel) {
        //先查参数 todo: 这么写的前提是 akSysDataService 表里有只少一条系统参数数据，完全没有健壮性，如果表是空的此处应该从配置文件中读一个缺省值出来
        AkSysData akSysData = akSysDataService.list().get(0);

        Result<JSONObject> result = new Result<>();
        String username = sysLoginModel.getUsername();
        String password = sysLoginModel.getPassword();
        //判断密码位数
        //update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题
        //前端密码加密，后端进行密码解密
        //password = AesEncryptUtil.desEncrypt(sysLoginModel.getPassword().replaceAll("%2B", "\\+")).trim();//密码解密
        //update-begin--Author:scott  Date:20190805 for：暂时注释掉密码加密逻辑，有点问题

        // 验证码功能已移除
        // 仅允许三员账号使用用户名/密码登录
        boolean isAdmin = username.equals("sysadmin") || username.equals("secadmin") || username.equals("secaudit") || username.equals("admin");
//        if (!isAdmin) {
//            result.setSuccess(false);
//            result.setMessage("当前用户不允许使用用户名/密码登录");
//            result.setCode(HttpStatus.FORBIDDEN.value());
//            return result;
//        }
        //1. 校验用户是否有效
        //update-begin-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(queryWrapper);
        //update-end-author:wangshuai date:20200601 for: 登录代码验证用户是否注销bug，if条件永远为false
        result = sysUserService.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }

        //2. 校验用户名或密码是否正确
        String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
        String syspassword = sysUser.getPassword();
//        if (!syspassword.equals(userpassword)) {
//            int failTime = sysUser.getLoginFailTime();
//            if (failTime < 5) {
//                result.error500("用户名或密码错误，剩余" + (4 - failTime) + "次机会");
//                sysUser.setLoginFailTime(failTime + 1);
//                sysUserService.updateById(sysUser);
//            } else {
//                result.error500("用户名或密码错误超过5次，已禁用，请联系安全保密员！");
//                sysUser.setLoginFailTime(0);
//                sysUser.setStatus(2);
//                sysUserService.updateById(sysUser);
//            }
//            return result;
//        }else {
//            sysUser.setLoginFailTime(0);
//            sysUserService.updateById(sysUser);
//        }
        //密码失败次数读数据库
        if (!syspassword.equals(userpassword)) {
            int failTime = sysUser.getLoginFailTime();
            if (failTime < akSysData.getFailTime()) {
                result.error500("用户名或密码错误，剩余" + (akSysData.getFailTime() - (failTime + 1)) + "次机会");
                sysUser.setLoginFailTime(failTime + 1);
                sysUserService.updateById(sysUser);
            } else {
                result.error500("用户名或密码错误超过" + akSysData.getFailTime() + "次，已禁用，请联系安全保密员！");
                sysUser.setLoginFailTime(0);
                sysUser.setStatus(2);
                sysUserService.updateById(sysUser);
            }
            return result;
        } else {
            sysUser.setLoginFailTime(0);
            sysUserService.updateById(sysUser);
        }

        if (!this.validateIp(username)) {
            result.error500("登录IP不在三员用户授权范围内!");
            return result;
        }
        Collection<String> keys = redisTemplate.keys(CommonConstant.PREFIX_USER_TOKEN + "*");
        for (String key : keys) {
            String token = (String) redisUtil.get(key);
            if (org.apache.commons.lang.StringUtils.isNotEmpty(token)) {
                //SysUserOnlineVO online = new SysUserOnlineVO();
                //online.setToken(token);
                //TODO 改成一次性查询
                LoginUser loginUser = sysBaseApi.getUserByName(JwtUtil.getUsername(token));
                if (loginUser != null && loginUser.getId().equals(sysUser.getId())) {
                    baseCommonService.addLog("强制: " + sysUser.getRealname() + "退出成功！", CommonConstant.LOG_TYPE_1, null, loginUser);
                    log.info(" 强制  " + sysUser.getRealname() + "退出成功！ ");
                    //清空用户登录Token缓存
                    redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
                    //清空用户登录Shiro权限缓存
                    redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
                    //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
                    redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
                    //调用shiro的logout
                    SecurityUtils.getSubject().logout();
//                    break;
                }
            }
        }

        //用户登录信息
        userInfo(sysUser, result);
        Date passwdUpdateTime = sysUser.getPasswdUpdateTime();
        if (passwdUpdateTime == null) {
            if (username.equals("admin")) {
                passwdUpdateTime = sysUser.getCreateTime();
            } else {
                result.setSuccess(true);
                result.setMessage("首次登陆，请修改密码！");
                return result;
            }
        }
        if (PasswordUtil.encrypt(username, "123456", sysUser.getSalt()).equals(sysUser.getPassword())) {
            result.setSuccess(true);
            result.setMessage("首次登陆，请修改密码！");
            return result;
        }
        long updateTime = passwdUpdateTime.getTime();
        long now = new Date().getTime();

        if (isAdmin && ((now - updateTime) > (sevenDay))) {
            result.setSuccess(true);
            result.setMessage("当前密码使用时间已超过7天，请修改密码！");
            return result;
        }
        //普通用户
//        if(!isAdmin){
//            if (systemSecurity == 3 && ((now - updateTime) > (fifteen))) {
//                //mm
//                result.setSuccess(true);
//                result.setMessage("当前密码使用时间已超过15天，请修改密码！");
//                return result;
//            }else if (systemSecurity == 4 && ((now - updateTime) > (month))){
//                //jm
//                result.setSuccess(true);
//                result.setMessage("当前密码使用时间已超过30天，请修改密码！");
//                return result;
//            }
//        }
        if (!isAdmin) {
            if ((now - updateTime) > (akSysData.getChangeDate().longValue() * 24 * 60 * 60 * 1000)) {
                //mm
                result.setSuccess(true);
                result.setMessage("当前密码使用时间已超过" + akSysData.getChangeDate() + "天，请修改密码！");
                return result;
            }
        }

        // 验证码功能已移除，无需删除验证码
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(sysUser, loginUser);
        baseCommonService.addLog(
                Math.min(systemSecurity, loginUser.getSecurity()),
                sysUserService.getLogTypeByUserName(loginUser.getUsername()),
                7, "用户", "用户名: " + loginUser.getRealname() + " 登录成功！", "成功",
                loginUser
        );
        //update-end--Author:wangshuai  Date:20200714  for：登录日志没有记录人员
//		result.getResult().getJSONObject("userInfo").get("realname") 可以获取到当前登录用户的真实姓名字段值
        return result;
    }

    /**
     * 校验Key登陆信息，通过了直接跳转主页
     *
     * @return
     */
    //@AutoLog(value = "校验Key登陆信息")
    @PostMapping("/keyLogin")
    public Result<JSONObject> CheckUserKey(HttpServletRequest request) throws UnsupportedEncodingException {
        //获取request
        Result<JSONObject> result = new Result<>();
        String loginid = "";//用户名
        SysUser sysUser = null;
        //ttpServletRequest request = SpringContextUtils.getHttpServletRequest();

        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) cookies = new Cookie[0];
            System.out.println("cookies=======================" + Arrays.toString(cookies));
            // 获取当前的时间戳（Unix时间）
            long timestamp = System.currentTimeMillis();
            // 使用SimpleDateFormat将时间戳格式化为可读的日期和时间字符串（默认本地时区）
            Date dateDefaultZone = new Date(timestamp);
            SimpleDateFormat sdfDefaultZone = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDateDefaultZone = sdfDefaultZone.format(dateDefaultZone);
            formattedDateDefaultZone = formattedDateDefaultZone.replaceAll(" ", "_");
            formattedDateDefaultZone = formattedDateDefaultZone.replace(":", "-");
//            System.out.println("Formatted date (default timezone): " + formattedDateDefaultZone);
            StringLogger logger = new StringLogger(location + formattedDateDefaultZone + ".txt");
//            从 Cookie 中获取 KOAL_CERT_E，KOAL_CERT_G 用来获取 8 位编码
            for (Cookie cookie : cookies) {
//                KOAL_CERT_E 对应的是商网从外网导内网的地址
//                if ("KOAL_CERT_E".equals(cookie.getName())) {
////	        	 userName = ComUtil.replaceNull2Space(new String(URLDecoder.decode(cookie.getValue()).getBytes("ISO-8859-1"), "UTF-8")) ;
//                    String loginid1 = new String(URLDecoder.decode(cookie.getValue()).getBytes("ISO-8859-1"), "UTF-8");
//                    System.out.println("获取登陆的userCode=" + loginid1);
//                    if (loginid1.contains("@")) {
//                        loginid1 = loginid1.substring(0, loginid1.indexOf("@"));
//                        logger.logString(loginid1, "KOAL_CERT_E");
//                    }
//                }
//                if ("KOAL_CERT_G".equals(cookie.getName())) {
////	        	 userName = ComUtil.replaceNull2Space(new String(URLDecoder.decode(cookie.getValue()).getBytes("ISO-8859-1"), "UTF-8")) ;
//                    String loginid1 = new String(URLDecoder.decode(cookie.getValue()).getBytes("ISO-8859-1"), "UTF-8");
//                    System.out.println("获取登陆的userCode=" + loginid1);
//                    if (loginid1.contains("@")) {
//                        loginid1 = loginid1.substring(0, loginid1.indexOf("@"));
//                        logger.logString(loginid1, "KOAL_CERT_G");
//                    }
//                }
//         KOAL_CERT_CN 是 13 位，013.。。???
                if ("KOAL_CERT_G".equals(cookie.getName())) {
//	        	 userName = ComUtil.replaceNull2Space(new String(URLDecoder.decode(cookie.getValue()).getBytes("ISO-8859-1"), "UTF-8")) ;
                    loginid = new String(URLDecoder.decode(cookie.getValue()).getBytes("ISO-8859-1"), "UTF-8");
//                    将此文本信息写到一个文本文件以方便查找
//                    System.out.println("获取登陆的userCode=" + loginid);
//                    logger.logString(loginid, "KOAL_CERT_G");
//                    if (loginid.contains("@")) {
//                        loginid = loginid.substring(0, loginid.indexOf("@"));
//                    }
                    break;
                }
            }
//            如果没有找到有效的 loginid, 返回错误
            if (StringUtils.isBlank(loginid)) {
//                logger.logString("loginid 为空", "获取登陆的userCode");
                result.error500("非有效用户");
                return result;
            }
//            根据 loginid 查询用户
            LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysUser::getWorkNo, loginid);
            sysUser = sysUserService.getOne(queryWrapper);
//            验证用户是否有效
            if (sysUser == null) {
                result.error500("非有效用户");
                return result;
            }
//            校验用户是否有效
            result = sysUserService.checkUserIsEffective(sysUser);
            if (!result.isSuccess()) {
                result.error500("非有效用户");
                return result;
            }
//            IP 验证
            if (!this.validateIp(sysUser.getUsername())) {
                result.error500("登录IP不在三员用户授权范围内");
                return result;
            }
//            强制退出其他登录的同一用户
            Collection<String> keys = redisTemplate.keys(CommonConstant.PREFIX_USER_TOKEN + "*");
            for (String key : keys) {
                String token = (String) redisUtil.get(key);
                if (org.apache.commons.lang.StringUtils.isNotEmpty(token)) {
                    LoginUser loginUser = sysBaseApi.getUserByName(JwtUtil.getUsername(token));
                    if (loginUser != null && loginUser.getId().equals(sysUser.getId())) {
                        baseCommonService.addLog("强制：" + sysUser.getRealname() + "退出成功！", CommonConstant.LOG_TYPE_1, null, loginUser);
                        log.info("强制 " + sysUser.getRealname() + "退出成功！");
//                        清空用户登录 Token 缓存
                        redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
//                        清空用户登录 Shiro 权限缓存
                        redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
//                        清空用户的缓存信息（包括部门信息），例如 sys:cache:user::<username>
                        redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
//                        调用 shiro 的 logout
                        SecurityUtils.getSubject().logout();
                    }
                }
            }
//            调用 userInfo 方法生成完整的登录信息
            userInfo(sysUser, result);
//            记录登录日志
            LoginUser loginUser = new LoginUser();
            BeanUtils.copyProperties(sysUser, loginUser);
            baseCommonService.addLog(Math.min(systemSecurity, sysUser.getSecurity()), sysUserService.getLogTypeByUserName(sysUser.getUsername()), 7, "用户", "用户名：" + sysUser.getRealname() + " Key登录成功！", "成功", loginUser);
        } catch (Exception e) {
            log.error("Key登陆异常", e);
            result.error500("非有效用户");
        }
//        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(SysUser::getLoginid, "zhangsan");
//        sysUser = sysUserService.getOne(queryWrapper);
        return result;
    }

    private boolean validateIp(String userName) {
//        如果总开关关闭，直接返回 true(不校验IP)
        if (!ipValidationEnabled) {
            return true;
        }
        String ip;
        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //获取IP地址
            ip = IpUtils.getIpAddr(request);
            if (ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
                ip = "127.0.0.1";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            ip = "未知来源";
        }
        switch (userName) {
            case "sysadmin":
                if (StringUtils.isNotBlank(sysadminIps) && !sysadminIps.contains(ip)) {
                    return false;
                }
                break;
            case "secadmin":
                if (StringUtils.isNotBlank(secadminIps) && !secadminIps.contains(ip)) {
                    return false;
                }
                break;
            case "secaudit":
                if (StringUtils.isNotBlank(secauditIps) && !secauditIps.contains(ip)) {
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 【vue3专用】获取用户信息
     */
    @GetMapping("/user/getUserInfo")
    public Result<JSONObject> getUserInfo(HttpServletRequest request) {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = JwtUtil.getUserNameByToken(request);
        if (oConvertUtils.isNotEmpty(username)) {
            // 根据用户名查询用户信息
            SysUser sysUser = sysUserService.getUserByName(username);
            JSONObject obj = new JSONObject();

            //update-begin---author:scott ---date:2022-06-20  for：vue3前端，支持自定义首页-----------
            String version = request.getHeader(CommonConstant.VERSION);
            //update-begin---author:liusq ---date:2022-06-29  for：接口返回值修改，同步修改这里的判断逻辑-----------
            SysRoleIndex roleIndex = sysUserService.getDynamicIndexByUserRole(username, version);
            if (oConvertUtils.isNotEmpty(version) && roleIndex != null && oConvertUtils.isNotEmpty(roleIndex.getUrl())) {
                String homePath = roleIndex.getUrl();
                if (!homePath.startsWith(SymbolConstant.SINGLE_SLASH)) {
                    homePath = SymbolConstant.SINGLE_SLASH + homePath;
                }
                sysUser.setHomePath(homePath);
            }
            //update-begin---author:liusq ---date:2022-06-29  for：接口返回值修改，同步修改这里的判断逻辑-----------
            //update-end---author:scott ---date::2022-06-20  for：vue3前端，支持自定义首页--------------

            obj.put("userInfo", sysUser);
            obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
            result.setResult(obj);
            result.success("");
        }
        return result;

    }

    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/logout")
    public Result<Object> logout(HttpServletRequest request, HttpServletResponse response) {
        //用户退出逻辑
        String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
        if (oConvertUtils.isEmpty(token)) {
            return Result.error("退出登录失败！");
        }
        String username = JwtUtil.getUsername(token);
        LoginUser sysUser = sysBaseApi.getUserByName(username);
        if (sysUser != null) {
            baseCommonService.addLog(Math.min(systemSecurity, sysUser.getSecurity()), sysUserService.getLogTypeByUserName(sysUser.getUsername()), 7, "用户", "用户名: " + sysUser.getRealname() + " 退出登录！", "成功", sysUser);
            log.info(" 用户名:  " + sysUser.getRealname() + ",退出成功！ ");
            //清空用户登录Token缓存
            redisUtil.del(CommonConstant.PREFIX_USER_TOKEN + token);
            //清空用户登录Shiro权限缓存
            redisUtil.del(CommonConstant.PREFIX_USER_SHIRO_CACHE + sysUser.getId());
            //清空用户的缓存信息（包括部门信息），例如sys:cache:user::<username>
            redisUtil.del(String.format("%s::%s", CacheConstant.SYS_USERS_CACHE, sysUser.getUsername()));
            //调用shiro的logout
            SecurityUtils.getSubject().logout();
            return Result.ok("退出登录成功！");
        } else {
            return Result.error("Token无效!");
        }
    }

    /**
     * 获取访问量
     *
     * @return
     */
    @GetMapping("loginfo")
    public Result<JSONObject> loginfo() {
        Result<JSONObject> result = new Result<JSONObject>();
        JSONObject obj = new JSONObject();
        //update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
        // 获取一天的开始和结束时间
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dayStart = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date dayEnd = calendar.getTime();
        // 获取系统访问记录
        Long totalVisitCount = logService.findTotalVisitCount();
        obj.put("totalVisitCount", totalVisitCount);
        Long todayVisitCount = logService.findTodayVisitCount(dayStart, dayEnd);
        obj.put("todayVisitCount", todayVisitCount);
        Long todayIp = logService.findTodayIp(dayStart, dayEnd);
        //update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
        obj.put("todayIp", todayIp);
        result.setResult(obj);
        result.success("登录成功");
        return result;
    }

    /**
     * 获取访问量
     *
     * @return
     */
    @GetMapping("visitInfo")
    public Result<List<Map<String, Object>>> visitInfo() {
        Result<List<Map<String, Object>>> result = new Result<List<Map<String, Object>>>();
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date dayStart = calendar.getTime();
        List<Map<String, Object>> list = logService.findVisitCount(dayStart, dayEnd);
        result.setResult(oConvertUtils.toLowerCasePageList(list));
        return result;
    }


    /**
     * 登陆成功选择用户当前部门
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/selectDepart", method = RequestMethod.PUT)
    public Result<JSONObject> selectDepart(@RequestBody SysUser user) {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = user.getUsername();
        if (oConvertUtils.isEmpty(username)) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            username = sysUser.getUsername();
        }
        String orgCode = user.getOrgCode();
        this.sysUserService.updateUserDepart(username, orgCode);
        SysUser sysUser = sysUserService.getUserByName(username);
        JSONObject obj = new JSONObject();
        obj.put("userInfo", sysUser);
        result.setResult(obj);
        return result;
    }

    // 短信验证码功能已移除


    // 手机号登录功能已移除


    /**
     * 用户信息
     *
     * @param sysUser
     * @param result
     * @return
     */
    private Result<JSONObject> userInfo(SysUser sysUser, Result<JSONObject> result) {
        String username = sysUser.getUsername();
        String syspassword = sysUser.getPassword();
        // 获取用户部门信息
        JSONObject obj = new JSONObject(new LinkedHashMap<>());

        // 生成token
        String token = JwtUtil.sign(username, syspassword);
        // 设置token缓存有效时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);
        obj.put("token", token);

        // update-begin--Author:sunjianlei Date:20210802 for：获取用户租户信息
        String tenantIds = sysUser.getRelTenantIds();
        if (oConvertUtils.isNotEmpty(tenantIds)) {
            List<Integer> tenantIdList = new ArrayList<>();
            for (String id : tenantIds.split(SymbolConstant.COMMA)) {
                tenantIdList.add(Integer.valueOf(id));
            }
            // 该方法仅查询有效的租户，如果返回0个就说明所有的租户均无效。
            List<SysTenant> tenantList = sysTenantService.queryEffectiveTenant(tenantIdList);
            if (tenantList.size() == 0) {
                result.error500("与该用户关联的租户均已被冻结，无法登录！");
                return result;
            } else {
                obj.put("tenantList", tenantList);
            }
        }
        // update-end--Author:sunjianlei Date:20210802 for：获取用户租户信息
// 获取角色信息
//        List<SysRole> roles = sysUser.getRoles();
        List<String> role = sysUserService.getRole(username);
        sysUser.setRoleCode(String.join(",", role));
//        密级
        sysUser.setSecurityTxt(SecurityLevelUtil.getSecurityNameByLevel(sysUser.getSecurity()));
        obj.put("userInfo", sysUser);

        List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
        obj.put("departs", departs);
        if (departs == null || departs.size() == 0) {
            obj.put("multi_depart", 0);
        } else if (departs.size() == 1) {
            sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
            obj.put("multi_depart", 1);
        } else {
            //查询当前是否有登录部门
            // update-begin--Author:wangshuai Date:20200805 for：如果用戶为选择部门，数据库为存在上一次登录部门，则取一条存进去
            SysUser sysUserById = sysUserService.getById(sysUser.getId());
            if (oConvertUtils.isEmpty(sysUserById.getOrgCode())) {
                sysUserService.updateUserDepart(username, departs.get(0).getOrgCode());
            }
            // update-end--Author:wangshuai Date:20200805 for：如果用戶为选择部门，数据库为存在上一次登录部门，则取一条存进去
            obj.put("multi_depart", 2);
        }
        obj.put("sysAllDictItems", sysDictService.queryAllDictItems());
        result.setResult(obj);
        result.success("登录成功");
//		result.getResult().getJSONObject("userInfo").get("realname") 可以获得当前登录用户的真实姓名字段值
        return result;
    }

    /**
     * 获取加密字符串
     *
     * @return
     */
    @GetMapping(value = "/getEncryptedString")
    public Result<Map<String, String>> getEncryptedString() {
        Result<Map<String, String>> result = new Result<Map<String, String>>();
        Map<String, String> map = new HashMap(5);
        map.put("key", EncryptedString.key);
        map.put("iv", EncryptedString.iv);
        result.setResult(map);
        return result;
    }

    // 验证码功能已移除

    /**
     * 切换菜单表为vue3的表
     */
    @GetMapping(value = "/switchVue3Menu")
    public Result<String> switchVue3Menu(HttpServletResponse response) {
        Result<String> res = new Result<String>();
        sysPermissionService.switchVue3Menu();
        return res;
    }

    /**
     * app登录
     *
     * @param sysLoginModel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/mLogin", method = RequestMethod.POST)
    public Result<JSONObject> mLogin(@RequestBody SysLoginModel sysLoginModel) throws Exception {
        Result<JSONObject> result = new Result<JSONObject>();
        String username = sysLoginModel.getUsername();
        String password = sysLoginModel.getPassword();

        //1. 校验用户是否有效
        SysUser sysUser = sysUserService.getUserByName(username);
        result = sysUserService.checkUserIsEffective(sysUser);
        if (!result.isSuccess()) {
            return result;
        }

        //2. 校验用户名或密码是否正确
        String userpassword = PasswordUtil.encrypt(username, password, sysUser.getSalt());
        String syspassword = sysUser.getPassword();
        if (!syspassword.equals(userpassword)) {
            result.error500("用户名或密码错误");
            return result;
        }

        String orgCode = sysUser.getOrgCode();
        if (oConvertUtils.isEmpty(orgCode)) {
            //如果当前用户无选择部门 查看部门关联信息
            List<SysDepart> departs = sysDepartService.queryUserDeparts(sysUser.getId());
            //update-begin-author:taoyan date:20220117 for: JTC-1068【app】新建用户，没有设置部门及角色，点击登录提示暂未归属部，一直在登录页面 使用手机号登录 可正常
            if (departs == null || departs.size() == 0) {
				/*result.error500("用户暂未归属部门,不可登录!");
				return result;*/
            } else {
                orgCode = departs.get(0).getOrgCode();
                sysUser.setOrgCode(orgCode);
                this.sysUserService.updateUserDepart(username, orgCode);
            }
            //update-end-author:taoyan date:20220117 for: JTC-1068【app】新建用户，没有设置部门及角色，点击登录提示暂未归属部，一直在登录页面 使用手机号登录 可正常
        }
        JSONObject obj = new JSONObject();
        //用户登录信息
        obj.put("userInfo", sysUser);

        // 生成token
        String token = JwtUtil.sign(username, syspassword);
        // 设置超时时间
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, JwtUtil.EXPIRE_TIME * 2 / 1000);

        //token 信息
        obj.put("token", token);
        result.setResult(obj);
        result.setSuccess(true);
        result.setCode(200);
        baseCommonService.addLog("用户名: " + username + ",登录成功[移动端]！", CommonConstant.LOG_TYPE_1, null);
        return result;
    }

    // 验证码功能已移除

    /**
     * 登录二维码
     */
    @ApiOperation(value = "登录二维码", notes = "登录二维码")
    @GetMapping("/getLoginQrcode")
    public Result<?> getLoginQrcode() {
        String qrcodeId = CommonConstant.LOGIN_QRCODE_PRE + IdWorker.getIdStr();
        //定义二维码参数
        Map params = new HashMap(5);
        params.put("qrcodeId", qrcodeId);
        //存放二维码唯一标识30秒有效
        redisUtil.set(CommonConstant.LOGIN_QRCODE + qrcodeId, qrcodeId, 30);
        return Result.OK(params);
    }

    /**
     * 扫码二维码
     */
    @ApiOperation(value = "扫码登录二维码", notes = "扫码登录二维码")
    @PostMapping("/scanLoginQrcode")
    public Result<?> scanLoginQrcode(@RequestParam String qrcodeId, @RequestParam String token) {
        Object check = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
        if (oConvertUtils.isNotEmpty(check)) {
            //存放token给前台读取
            redisUtil.set(CommonConstant.LOGIN_QRCODE_TOKEN + qrcodeId, token, 60);
        } else {
            return Result.error("二维码已过期,请刷新后重试");
        }
        return Result.OK("扫码成功");
    }


    /**
     * 获取用户扫码后保存的token
     */
    @ApiOperation(value = "获取用户扫码后保存的token", notes = "获取用户扫码后保存的token")
    @GetMapping("/getQrcodeToken")
    public Result getQrcodeToken(@RequestParam String qrcodeId) {
        Object token = redisUtil.get(CommonConstant.LOGIN_QRCODE_TOKEN + qrcodeId);
        Map result = new HashMap(5);
        Object qrcodeIdExpire = redisUtil.get(CommonConstant.LOGIN_QRCODE + qrcodeId);
        if (oConvertUtils.isEmpty(qrcodeIdExpire)) {
            //二维码过期通知前台刷新
            result.put("token", "-2");
            return Result.OK(result);
        }
        if (oConvertUtils.isNotEmpty(token)) {
            result.put("success", true);
            result.put("token", token);
        } else {
            result.put("token", "-1");
        }
        return Result.OK(result);
    }

}
