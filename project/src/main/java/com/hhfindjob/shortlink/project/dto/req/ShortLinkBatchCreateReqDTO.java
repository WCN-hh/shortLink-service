package com.hhfindjob.shortlink.project.dto.req;

import java.util.Date;
import java.util.List;

public class ShortLinkBatchCreateReqDTO {

    /**
     * 域名
     */
    private String domain;

    /**
     * 原始链接集合
     */
    private List<String> originUrls;

    /**
     * 分组标识
     */
    private String gid;

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
    private List<String> describes;
}
