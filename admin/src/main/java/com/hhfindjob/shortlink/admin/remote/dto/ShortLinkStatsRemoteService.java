package com.hhfindjob.shortlink.admin.remote.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;

import java.util.HashMap;
import java.util.Map;

public interface ShortLinkStatsRemoteService {
    
    /**
     * 获取单个短链接监控数据
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    default Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam){
        Map<String, Object> map = BeanUtil.beanToMap(requestParam);
        String string = HttpUtil.get(
                "http://localhost:8002/api/short-link/v1/stats"
                , map);
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }

    default Result<Page<ShortLinkStatsAccessRecordRespDTO>> pageSelectAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam){
        //Map<String, Object> map = BeanUtil.beanToMap(dto);
        Map<String,Object> map=new HashMap<>();
        map.put("fullShortUrl",requestParam.getFullShortUrl());
        map.put("gid",requestParam.getGid());
        map.put("startDate",requestParam.getStartDate());
        map.put("endDate",requestParam.getEndDate());
        map.put("current",requestParam.getCurrent());
        map.put("size",requestParam.getSize());
        String string = HttpUtil.get("http://localhost:8002/api/short-link/v1/stats/access-record", map);
        return JSON.parseObject(string, new TypeReference<>() {});
    }

    default Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam){
        Map<String, Object> map = BeanUtil.beanToMap(requestParam);
        String string = HttpUtil.get(
                "http://localhost:8002/api/short-link/v1/stats/group"
                , map);
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }

    default Result<Page<ShortLinkStatsAccessRecordRespDTO>> groupPageStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam){
        Map<String,Object> map=new HashMap<>();
        map.put("gid",requestParam.getGid());
        map.put("startDate",requestParam.getStartDate());
        map.put("endDate",requestParam.getEndDate());
        map.put("current",requestParam.getCurrent());
        map.put("size",requestParam.getSize());
        String string = HttpUtil.get(
                "http://localhost:8002/api/short-link/v1/stats/access-record/group"
                , map);
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }
}
