package org.jeecg.common.system.util;

/**
 * 自定义 用户密级低于数据密级 异常
 */
public class UserSecurityLessThanDataSecurityException extends Exception{
    public UserSecurityLessThanDataSecurityException(String msg){
        super(msg);
    }

}
