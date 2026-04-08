package org.jeecg.common.system.util;

/**
 * 自定义 树节点密级 低于数据密级 异常
 */
public class BidSecurityLessThanDataSecurityException extends Exception{
    public BidSecurityLessThanDataSecurityException(String msg){
        super(msg);
    }

}
