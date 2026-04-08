package org.jeecg.modules.chainproductcatalogtree.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 产品目录树结构表
 * @Author: jeecg-boot
 * @Date: 2024-02-29
 * @Version: V1.0
 */
@Data
@TableName("chain_product_catalog_tree")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "chain_product_catalog_tree对象", description = "产品目录树结构表")
public class ChainProductCatalogTree implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 对应 type字段,前端数据树中的title
     */
    private String title;


    private boolean isLeaf;
    // 以下所有字段均与 ChainProductCatalog 相同

    private String parentId;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    /**
     * @author cmz
     * @description 用于指明点击的左侧树结构叶子节点的父级节点
     * @date 2024/3/4 14:02
     */
    @TableField(exist = false)
    private String parent;

    @TableField(exist = false)
    private List<ChainProductCatalogTree> children = new ArrayList<>();


    public ChainProductCatalogTree() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(boolean isleaf) {
        this.isLeaf = isleaf;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ChainProductCatalogTree> getChildren() {
        return children;
    }

    public void setChildren(List<ChainProductCatalogTree> children) {
        if (children == null) {
            this.isLeaf = true;
        }
        this.children = children;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 重写equals方法
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChainProductCatalogTree model = (ChainProductCatalogTree) o;
        return Objects.equals(id, model.id) &&
                Objects.equals(parentId, model.parentId) &&
                Objects.equals(createBy, model.createBy) &&
                Objects.equals(createTime, model.createTime) &&
                Objects.equals(updateBy, model.updateBy) &&
                Objects.equals(updateTime, model.updateTime) &&
                Objects.equals(children, model.children);
    }

    /**
     * 重写hashCode方法
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, createBy, createTime, updateBy, updateTime,
                children);
    }
}
