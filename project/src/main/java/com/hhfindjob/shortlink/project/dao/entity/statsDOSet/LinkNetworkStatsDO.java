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
 * @description t_link_network_stats
 * @author BEJSON.com
 * @date 2026-08-20
 */
@Data
@Builder
@AllArgsConstructor
@TableName("t_link_network_stats")
public class LinkNetworkStatsDO extends BaseDO {

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
     * 访问网络
     */
    private String network;

    public LinkNetworkStatsDO() {}
}
