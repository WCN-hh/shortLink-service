package com.hhfindjob.shortlink.admin.remote.dto.resp;


import lombok.Data;

@Data
public class ShortLinkGroupCountQueryRespDTO {

    private String gid;

    private Integer shortLinkCount;
}
