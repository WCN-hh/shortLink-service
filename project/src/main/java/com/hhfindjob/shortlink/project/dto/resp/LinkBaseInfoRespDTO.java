package com.hhfindjob.shortlink.project.dto.resp;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkBaseInfoRespDTO {

    /**
     * 描述
     */
    private String describe;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
