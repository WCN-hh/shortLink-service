package com.hhfindjob.shortlink.admin.remote.dto.req;

import lombok.Data;

@Data
public class ShortLinkGroupStatsReqDTO {

    /**
     * 分组
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
