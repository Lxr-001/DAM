package org.jeecg.common.system.util;

import org.jeecg.common.constant.enums.Enums;

import java.util.List;

public class SecurityLevelUtil {
    /**
     * 计算最大密级
     * @param securityList
     * @return
     */
    public static int calculateMaxSecurity(List<Integer> securityList){
        int maxSecurity = 0;
        for (Integer item : securityList){
            if (item >= maxSecurity){
                maxSecurity = item;
            }
        }
        return maxSecurity;
    }

    /**
     * 根据密级编码获取密级名称
     * @param securityLevel
     * @return
     */
    public static String getSecurityNameByLevel(int securityLevel){
        //            通过枚举获得密级对应文本
        String securityName = "";
        try {
            Enums.secretLevelEnum securityEnum = Enums.secretLevelEnum.getByValue(securityLevel);
            if (securityEnum != null) {
                securityName = securityEnum.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  securityName;
    }
}
