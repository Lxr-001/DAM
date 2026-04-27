package org.jeecg.modules.DAM.DataInfo.SpaceAsset.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 数据资产主表
 * @Author: jeecg-boot
 * @Date: 2026-04-21
 * @Version: V1.0
 */
@Data
@TableName("dam_space_asset")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "dam_space_asset对象", description = "数据资产主表")
public class SpaceAsset implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /** 数据空间ID */
    @ApiModelProperty(value = "数据空间ID")
    private String spaceId;

    /** 资产类型：dataset/api/training_set */
    @ApiModelProperty(value = "资产类型")
    private String assetType;

    /** 资产名称 */
    @ApiModelProperty(value = "资产名称")
    private String assetName;

    /** 资产描述 */
    @ApiModelProperty(value = "资产描述")
    private String assetDesc;

    /** 数据密级：公开/内部/秘密/机密/绝密 */
    @ApiModelProperty(value = "数据密级")
    private String dataLevel;

    /** 提供者名称 */
    @ApiModelProperty(value = "提供者名称")
    private String provider;

    /** 提供者ID */
    @ApiModelProperty(value = "提供者ID")
    private String providerId;

    /** 数据Owner用户ID */
    @ApiModelProperty(value = "数据Owner用户ID")
    private String ownerId;

    /** 数据Owner用户名 */
    @ApiModelProperty(value = "数据Owner用户名")
    private String ownerName;

    /** 所属部门 */
    @ApiModelProperty(value = "所属部门")
    private String belongDept;

    /** 所属部门ID */
    @ApiModelProperty(value = "所属部门ID")
    private String belongDeptId;

    /** 状态：draft/submitted/approved/rejected/cancelled */
    @ApiModelProperty(value = "状态")
    private String status;

    /** 数据资产证书图片路径 */
    @ApiModelProperty(value = "证书图片路径")
    private String certPath;

    /** 数据资产证书编号 */
    @ApiModelProperty(value = "证书编号")
    private String certNo;

    /** 创建人 */
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    // ==================== 数据集特有字段 ====================

    /** 数据结构类型：structured/unstructured */
    @ApiModelProperty(value = "数据结构类型")
    private String dataStructure;

    /** 更新频率 */
    @ApiModelProperty(value = "更新频率")
    private String updateFrequency;

    /** 数据覆盖开始时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "数据覆盖开始时间")
    private Date timeRangeStart;

    /** 数据覆盖结束时间 */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "数据覆盖结束时间")
    private Date timeRangeEnd;

    /** 数据规模（条数） */
    @ApiModelProperty(value = "数据规模（条数）")
    private BigInteger dataScale;

    /** 数据体量（如500G） */
    @ApiModelProperty(value = "数据体量")
    private String dataVolume;

    /** 应用场景 */
    @ApiModelProperty(value = "应用场景")
    private String applicationScenario;

    /** 细分场景 */
    @ApiModelProperty(value = "细分场景")
    private String subScenario;

    /** 技术体系 */
    @ApiModelProperty(value = "技术体系")
    private String techSystem;

    // ==================== API特有字段 ====================

    /** 开放程度：open/internal/private */
    @ApiModelProperty(value = "开放程度")
    private String openDegree;

    /** 操作类型：read_only/write_only/read_write */
    @ApiModelProperty(value = "操作类型")
    private String operationType;

    /** 数据粒度：detail/summary/wide_table/theme_domain */
    @ApiModelProperty(value = "数据粒度")
    private String dataGranularity;

    /** API调用地址 */
    @ApiModelProperty(value = "API调用地址")
    private String apiAddress;

    /** 请求方式：GET/POST/PUT/DELETE */
    @ApiModelProperty(value = "请求方式")
    private String apiMethod;

    /** 返回格式：JSON/XML */
    @ApiModelProperty(value = "返回格式")
    private String apiReturnType;

    /** 请求参数（JSON格式） */
    @ApiModelProperty(value = "请求参数")
    private String apiRequestParams;

    /** 返回参数（树形JSON格式） */
    @ApiModelProperty(value = "返回参数")
    private String apiReturnParams;

    /** 请求示例代码 */
    @ApiModelProperty(value = "请求示例")
    private String apiRequestExample;

    /** 成功返回示例代码 */
    @ApiModelProperty(value = "成功返回示例")
    private String apiSuccessExample;

    /** 失败返回示例（JSON格式） */
    @ApiModelProperty(value = "失败返回示例")
    private String apiErrorExample;

    // ==================== 训练集特有字段 ====================

    /** 数据格式：json/csv/text/imagefolder/soundfolder */
    @ApiModelProperty(value = "数据格式")
    private String dataFormat;

    /** 数据模态：text/image/audio/video/multimodal */
    @ApiModelProperty(value = "数据模态")
    private String dataModality;

    /** 标注方式 */
    @ApiModelProperty(value = "标注方式")
    private String annotationType;

    /** 训练阶段 */
    @ApiModelProperty(value = "训练阶段")
    private String trainingStage;

    /** 数据来源 */
    @ApiModelProperty(value = "数据来源")
    private String dataSource;

    // ==================== 扩展字段 ====================

    /** 复杂分类信息（JSON格式） */
    @ApiModelProperty(value = "复杂分类信息")
    private String classificationInfo;
}
