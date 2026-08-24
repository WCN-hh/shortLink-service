package com.hhfindjob.shortlink.project.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * 短链接监控接口层
 */
public interface ShortLinkStatsService {

    /**
     * 获取单个短链接监控数据
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    IPage<ShortLinkStatsAccessRecordRespDTO> pageSelectAccessRecord(ShortLinkStatsAccessRecordReqDTO dto);

    ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam);

    IPage<ShortLinkStatsAccessRecordRespDTO> groupPageStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO dto);
}
