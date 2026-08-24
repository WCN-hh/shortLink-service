package com.hhfindjob.shortlink.project.dao.entity.statsDOSet;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hhfindjob.shortlink.project.dao.entity.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @description t_link_access_logs
 * @author BEJSON.com
 * @date 2026-08-20
 */
@Data
@Builder
@AllArgsConstructor
@TableName("t_link_access_logs")
public class LinkAccessLogsDO extends BaseDO {

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
     * 用户信息
     */
    private String user;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * IP
     */
    private String ip;

    /**
     * 访问网络
     */
    private String network;

    /**
     * 访问设备
     */
    private String device;

    /**
     * 访问地区
     */
    private String locale;

    public LinkAccessLogsDO() {}
}
