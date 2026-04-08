package org.jeecg.common.constant.enums;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * 公共枚举类-对应代码表
 */
public class Enums {


    //开关0-否；1-是
    public enum booleanEnum {
        zero(0, "否"), one(1, "是");

        private booleanEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        Integer value = 0;

        public Integer getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static booleanEnum getByValue(Integer id) {
            return getEnumObject(booleanEnum.values(), id);
        }

        public static booleanEnum getByName(String name) {
            for (booleanEnum item : booleanEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }

    //审核状态
    public enum bpmStatusEnum {
        wfp(0, "未分配"),
        dtj(1, "待提交"),
        shz(2, "审核中"),
        ywc(3, "已完成"),
        yzf(4, "已作废"),
        ygq(5, "已挂起"),
        bbh(9, "被驳回");

        private bpmStatusEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        Integer value = 0;

        public Integer getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static bpmStatusEnum getByValue(Integer id) {
            return getEnumObject(bpmStatusEnum.values(), id);
        }

        public static bpmStatusEnum getByName(String name) {
            for (bpmStatusEnum item : bpmStatusEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }

    //流程节点key
    public enum bpmTaskKeyEnum {
        sh("Task_0c2kq2s", "审核中"),
        dtj("Task_163ixp8", "待提交");

        private bpmTaskKeyEnum(String value, String name) {
            this.value = value;
            this.name = name;
        }

        String value = "";

        public String getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static bpmTaskKeyEnum getByValue(String value) {
            return getEnumObject(bpmTaskKeyEnum.values(), value);
        }

        public static bpmTaskKeyEnum getByName(String name) {
            for (bpmTaskKeyEnum item : bpmTaskKeyEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }

    //所属层级
    public enum quenaireLevelEnum {
        qiy(0, "企业"), chanp(1, "产品"), hangy(2, "行业");

        private quenaireLevelEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        Integer value = 0;

        public Integer getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static quenaireLevelEnum getByValue(Integer id) {
            return getEnumObject(quenaireLevelEnum.values(), id);
        }

        public static quenaireLevelEnum getByName(String name) {
            for (quenaireLevelEnum item : quenaireLevelEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }

    //修订状态
    public enum xdStateEnum {
        kong(0, ""), add(1, "add"), del(2, "del");

        private xdStateEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        Integer value = 0;

        public Integer getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static xdStateEnum getByValue(Integer id) {
            return getEnumObject(xdStateEnum.values(), id);
        }

        public static xdStateEnum getByName(String name) {
            for (xdStateEnum item : xdStateEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }

    //数据密级
    public enum secretLevelEnum {
        gk(1, "公开"), nb(2, "内部"), mm(3, "秘密"), jm(4, "机密"), juem(5, "绝密");

        private secretLevelEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        Integer value = 0;

        public Integer getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static secretLevelEnum getByValue(Integer id) {
            return getEnumObject(secretLevelEnum.values(), id);
        }

        public static secretLevelEnum getByName(String name) {
            for (secretLevelEnum item : secretLevelEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }

    /**
     * 跨平台情况
     */
    public enum kptqkEnum {
        samePlat(1, "同平台"), sameHost(2, "同主机"), crossHost(3, "跨主机");

        private kptqkEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        Integer value = 0;

        public Integer getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static kptqkEnum getByValue(Integer id) {
            return getEnumObject(kptqkEnum.values(), id);
        }

        public static kptqkEnum getByName(String name) {
            for (kptqkEnum item : kptqkEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }

    //标准类型
    public enum standardTypeEnum {
        gb("GB", "国标"),
        gbj("GJB", "国军标"),
        jh("JH", "国军标计划"),
        hybz("HYBZ", "行业标准"),
        qt("OTHER", "其他");

        private standardTypeEnum(String value, String name) {
            this.value = value;
            this.name = name;
        }

        String value = "";

        public String getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static standardTypeEnum getByValue(String value) {
            return getEnumObject(standardTypeEnum.values(), value);
        }

        public static standardTypeEnum getByName(String name) {
            for (standardTypeEnum item : standardTypeEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }

    //所属平台
    public enum ssptEnum {
        one(1, "01"),
        two(2, "02"),
        three(3, "03"),
        four(4, "04"),
        five(5, "05"),
        six(6, "06"),
        seven(7, "统型");

        private ssptEnum(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        Integer value = 0;

        public Integer getValue() {
            return value;
        }

        String name = "";

        public String getName() {
            return name;
        }

        public static ssptEnum getByValue(Integer id) {
            return getEnumObject(ssptEnum.values(), id);
        }

        public static ssptEnum getByName(String name) {
            for (ssptEnum item : ssptEnum.values()) {
                if(StringUtils.equals(item.getName(), name)){
                    return item;
                }
            }
            return null;
        }
    }





    public static <T> T getEnumObject(T[] enumArray, int value) {
        if (enumArray == null || enumArray.length <= 0)
            return null;

        try {
            int tempValue = -1;
            for (T obj : enumArray) {
                Method method = null;
                try {
                    method = obj.getClass().getDeclaredMethod("getId");
                } catch (Exception e) {
                    // Auto-generated catch block
                    method = obj.getClass().getMethod("getValue");
                }
                if (method == null) {
                    continue;
                }
                tempValue = Integer.parseInt(method.invoke(obj).toString());
                if (tempValue == value) {
                    return obj;
                }
            }
        } catch (SecurityException e) {
            // Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        return enumArray[0];
    }

    public static <T> T getEnumObject(T[] enumArray, String value) {
        if (enumArray == null || enumArray.length <= 0)
            return null;

        try {
            String tempValue = "";
            for (T obj : enumArray) {
                Method method = null;
                try {
                    method = obj.getClass().getDeclaredMethod("getId");
                } catch (Exception e) {
                    // Auto-generated catch block
                    method = obj.getClass().getMethod("getValue");
                }
                if (method == null) {
                    continue;
                }
                tempValue = method.invoke(obj).toString();
                if (StringUtils.equals(tempValue, value)) {
                    return obj;
                }
            }
        } catch (SecurityException e) {
            // Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        return enumArray[0];
    }

    public static void main(String[] args) {
        String test1 = bpmTaskKeyEnum.getByName("审核").getValue();
        System.out.println(test1);
    }

}
