package com.hhfindjob.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 短链接分组实体
 * @description saas_group
 * @author BEJSON.com
 * @date 2026-04-13
 */
@Data
@Builder
@TableName("saas_group")
public class GroupDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * 分组标识
     */
    private String gId;

    /**
     * 分组标识
     */
    private String gName;

    /**
     * 创建人
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识 0：未删除
     */
    private Integer delFlag;

}