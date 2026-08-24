package com.hhfindjob.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.hhfindjob.shortlink.admin.remote.dto.req.PageSelectReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkCreateOrUpdateReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.PageSelectRespDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class ShortLinkController {

    //private final ShortLinkRemoteService service;
    ShortLinkRemoteService service=new ShortLinkRemoteService() {
    };
    /**
     * 创建短链接
     * @param dto
     * @return
     */
    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateOrUpdateReqDTO dto){
        return service.createShortLink(dto);
    }

    /************上方调试完毕**************/

    /**
     * 分页查询短链接
     * @param
     * @return
     */
    @GetMapping("/page")
    public Result<Page<PageSelectRespDTO>> pageSelect(PageSelectReqDTO dto){
        //TODO 兼容性问题？，返回的total始终比实际值小1
        return service.pageSelect(dto);
    }

}
