package com.hhfindjob.shortlink.project.dto.resp;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkStatsAccessRecordRespDTO {

    /**
     * 访客类型
     */
    private String uvType;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     *  ip
     */
    private String ip;

    /**
     * 网络
     */
    private String network;

    /**
     * 地区
     */
    private String device;

    /**
     *  访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date createTime;

    /**
     * 用户
     */
    private String user;
}
