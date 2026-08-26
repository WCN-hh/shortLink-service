package com.hhfindjob.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
