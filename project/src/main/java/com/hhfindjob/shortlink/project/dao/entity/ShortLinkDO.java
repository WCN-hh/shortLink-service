package com.hhfindjob.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @description t_link
 * @author BEJSON.com
 * @date 2026-08-06
 */
@Data
@Builder
@AllArgsConstructor
@TableName("t_link")
public class ShortLinkDO extends BaseDO{

    private static final long serialVersionUID = 1L;
    /**
     *  id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 启用标识：0 启用
     */
    private Integer enableStatus;

    /**
     * 创建类型：0 接口创建，1 控制台创建
     */
    private Integer createType;

    /**
     * 有效期类型：0 永久有效，1 自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

    /**
     * 网站标识
     */
    private String favicon;

    /**
     *  历史pv数
     */
    private Integer totalPv;

    /**
     *  历史uv数
     */
    private Integer totalUv;

    /**
     *  历史uip数
     */
    private Integer totalUip;

    /**
     * 今日PV
     */
    @TableField(exist = false)
    private Integer todayPv;

    /**
     * 今日UV
     */
    @TableField(exist = false)
    private Integer todayUv;

    /**
     * 今日UIP
     */
    @TableField(exist = false)
    private Integer todayUip;

    public ShortLinkDO() {
    }
}
