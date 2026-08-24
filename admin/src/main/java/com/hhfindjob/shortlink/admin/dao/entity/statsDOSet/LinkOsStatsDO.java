package com.hhfindjob.shortlink.admin.dao.entity.statsDOSet;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hhfindjob.shortlink.admin.dao.entity.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description t_link_os_stats
 * @author BEJSON.com
 * @date 2026-08-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_os_stats")
public class LinkOsStatsDO extends BaseDO {

    private static final long serialVersionUID = 1L;

    /**
     *  id
     */
    @TableId(type = IdType.AUTO)
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
     * 操作系统
     */
    private String os;
}
