package org.jeecg.modules.chaindatapackage.enums;

/**
 * @author lhx
 * @description:导入的数据包文件名称的枚举
 * @date 2021/4/12
 */
public enum DataPackageFileTypeEnum {
    PROJECT_DATA("ProjectData.dat", "项目数据",0),
    SPECIFICATION_DATA("SpecificationData.dat", "规格数据",1),
    BATCH_DATA("BatchData.dat", "批次数据",1),
    TEST_PROPERTY_DATA("TestPropertyData.dat", "试验属性数据",1),
    TEST_ITEM("TestItem.dat", "试验项数据",1),
    FACT_DATA("FactData.dat", "实测值数据",1),
    MONTH_REPORT_DATA("MonthReport.dat", "月报数据",0),
    ATTACHMENT_DATA("AttachmentData.dat", "附件数据",1),
    TABLE_SPACE_DATA("TableSpace.dat", "填表数据",1),

    QR_CODE("qrs", "二维码",0),
    ATTACHMENT("attachments", "附件",0);

    DataPackageFileTypeEnum(String name, String value, int flag) {
        this.name = name;
        this.value = value;
        this.flag = flag;
    }

    private String name;
    private String value;

    private int flag; //暂不解析

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static String getValueByName(String name) {
        for (DataPackageFileTypeEnum fileNameEnum : DataPackageFileTypeEnum.values()) {
            if (fileNameEnum.getName().equals(name)) {
                return fileNameEnum.getValue();
            }
        }

        return "";
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
