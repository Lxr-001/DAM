package org.jeecg.common.system.util;

import org.jeecg.common.constant.enums.Enums;

import java.util.List;

/**
 * 跨平台情况
 */
public class KptqkUtil {
    /**
     * 根据跨平台情况编码获取跨平台情况名称
     * @param kptqk
     * @return
     */
    public static String getKptqkNameByKptqk(int kptqk){
        //            通过枚举获得跨平台情况对应文本
        String kptqkName = "";
        try {
            Enums.kptqkEnum kptqkEnum = Enums.kptqkEnum.getByValue(kptqk);
            if (kptqkEnum != null) {
                kptqkName = kptqkEnum.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  kptqkName;
    }
}
