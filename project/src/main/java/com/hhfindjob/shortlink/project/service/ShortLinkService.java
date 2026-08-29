package com.hhfindjob.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkDO;
import com.hhfindjob.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.hhfindjob.shortlink.project.dto.req.PageSelectReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.hhfindjob.shortlink.project.dto.resp.PageSelectRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO dto);

    IPage<PageSelectRespDTO> pageSelect(PageSelectReqDTO dto);

    List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gids);

    Boolean updateShortLink(ShortLinkUpdateReqDTO dto);

    void restoreShortLink(String uri, ServletRequest request, ServletResponse response);

    ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO dto);

    void shortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO record);
}
