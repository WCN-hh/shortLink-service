package com.hhfindjob.shortlink.project.dao.entity.statsDOSet;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hhfindjob.shortlink.project.dao.entity.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @description t_link_stats_today
 * @author BEJSON.com
 * @date 2026-08-22
 */
@Data
@Builder
@AllArgsConstructor
@TableName("t_link_stats_today")
public class LinkStatsTodayDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 今日PV
     */
    private Integer todayPv;

    /**
     * 今日UV
     */
    private Integer todayUv;

    /**
     * 今日IP数
     */
    private Integer todayUip;

    public LinkStatsTodayDO() {}
}
