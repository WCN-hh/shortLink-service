package com.hhfindjob.shortlink.admin.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.remote.dto.ShortLinkStatsRemoteService;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控控制层
 */
@RestController
@RequestMapping("/api/short-link/admin/v1/")
public class ShortLinkStatsController {

    ShortLinkStatsRemoteService shortLinkStatsService=new ShortLinkStatsRemoteService() {
    };

    /**
     * 访问单个短链接指定时间内监控数据
     */
    @GetMapping("/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkStatsService.oneShortLinkStats(requestParam);
    }

    /**
     *  访问单个短链接指定时间内访问纪录监控数据
     */
    @GetMapping("/stats/access-record")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> pageStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO dto){
        return shortLinkStatsService.pageSelectAccessRecord(dto);
    }

    /**
     * 访问分组短链接指定时间内监控数据
     */
    @GetMapping("/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return shortLinkStatsService.groupShortLinkStats(requestParam);
    }

    /**
     *  访问分组短链接指定时间内访问纪录监控数据
     */
    @GetMapping("/stats/access-record/group")
    public Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupPageStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO dto){
        return shortLinkStatsService.groupPageStatsAccessRecord(dto);
    }
}
