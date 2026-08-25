package com.hhfindjob.shortlink.project.dto.resp;

import lombok.Builder;

import java.util.List;


@Builder
public class ShortLinkBatchCreateRespDTO {

    /**
     * 总数
     */
    private Integer total;

    /**
     * 短链接集合
     */
    private List<LinkBaseInfoRespDTO> linkInfos;


}
