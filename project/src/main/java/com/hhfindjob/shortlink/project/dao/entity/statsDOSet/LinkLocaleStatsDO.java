package com.hhfindjob.shortlink.project.dao.entity.statsDOSet;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hhfindjob.shortlink.project.dao.entity.BaseDO;
import lombok.Data;

import java.util.Date;

/**
 * 地区统计访问实体
 * @description t_link_locale_stats
 * @author BEJSON.com
 * @date 2026-08-19
 */
@Data
@TableName("t_link_locale_stats")
public class LinkLocaleStatsDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * ID
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 城市编码
     */
    private String adcode;

    /**
     * 国家
     */
    private String country;
}
