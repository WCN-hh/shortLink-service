package com.hhfindjob.shortlink.admin.remote.dto;


import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.remote.dto.req.PageSelectRecycleReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.PageSelectReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.RecycleReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkCreateOrUpdateReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.PageSelectRespDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ShortLinkRemoteService {

    /***************ShortLinkController*******************/

    /**
     * 分页查询短链接
     * @param
     * @return
     */
    default Result<Page<PageSelectRespDTO>> pageSelect(PageSelectReqDTO dto){
        Map<String,Object> map=new HashMap<>();
        map.put("gid",dto.getGid());
        map.put("orderTag", dto.getOrderTag());
        map.put("current",dto.getCurrent());
        map.put("size",dto.getSize());
        String string = HttpUtil.get("http://localhost:8002/api/short-link/v1/page", map);
        //JSONObject json = JSON.parseObject(string);
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }

    /**
     * 创建短链接
     * @param dto
     * @return
     */
    default Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateOrUpdateReqDTO dto){
        String responseBody = HttpUtil.post(
                "http://localhost:8002/api/short-link/v1/create",
                JSON.toJSONString(dto));
        return JSON.parseObject(responseBody,new TypeReference<>(){});
    }


    /***************GroupServiceImpl*******************/

    /**
     * 分组查询
     * @param
     * @return
     */
    default Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(List<String> gids){
        Map<String,Object> map=new HashMap<>();
        map.put("gids",gids);
        String string = HttpUtil.get("http://localhost:8002/api/short-link/v1/count", map);
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }


    /***************UrlMetaController*******************/
    /**
     * @param url
     * @return 返回网站标题
     */
    default Result<String> getTitle(String url){
        String string = HttpUtil.get("http://localhost:8002/api/short-link/v1/title?url=" + url);
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }

    /***************RecycleBinController*******************/

    default Boolean saveRecycleBin(RecycleReqDTO dto){
        String string = HttpUtil.post(
                "http://localhost:8002/api/short-link/v1/recycle-bin"+"/save"
                , JSON.toJSONString(dto));
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }

    default Boolean recoverRecycleBin(RecycleReqDTO dto){
        String string = HttpUtil.post(
                "http://localhost:8002/api/short-link/v1/recycle-bin"+"/recover"
                , JSON.toJSONString(dto));
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }

    default Boolean removeRecycleBin(RecycleReqDTO dto){
        String string = HttpUtil.post(
                "http://localhost:8002/api/short-link/v1/recycle-bin"+"/remove"
                , JSON.toJSONString(dto));
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }

    default Page<PageSelectRespDTO> pageSelectRecycle(PageSelectRecycleReqDTO dto){
        Map<String,Object> map=new HashMap<>();
        map.put("gidList",dto.getGidList());
        map.put("current",dto.getCurrent());
        map.put("size",dto.getSize());
        String string = HttpUtil.get(
                "http://localhost:8002/api/short-link/v1/recycle-bin"+"/page", map);
        return JSON.parseObject(string, new TypeReference<>() {
        });
    }
}
