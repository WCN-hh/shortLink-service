package com.hhfindjob.shortlink.project.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhfindjob.shortlink.project.common.convention.result.Result;
import com.hhfindjob.shortlink.project.common.convention.result.Results;
import com.hhfindjob.shortlink.project.dto.req.PageSelectReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.hhfindjob.shortlink.project.dto.resp.PageSelectRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.hhfindjob.shortlink.project.handler.CustomBlockHandler;
import com.hhfindjob.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/v1")
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService service;

    /**
     * 创建短链接
     * @param dto
     * @return
     */
    @PostMapping("/create")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO dto){
        return Results.success(service.createShortLink(dto));
    }

    /**
     * 批量创建短链接
     * @param dto
     * @return
     */
    @PostMapping("/create/batch")
    public Result<ShortLinkBatchCreateRespDTO> batchCreateShortLink(
            @RequestBody ShortLinkBatchCreateReqDTO dto){
        return Results.success(service.batchCreateShortLink(dto));
    }

    /**
     * 修改短链接
     * @param dto
     * @return
     */
    @PostMapping("/update")
    public Result<Boolean> updateShortLink(@RequestBody ShortLinkUpdateReqDTO dto){
        //TODO 条件不全，后面再开发
        return Results.success(service.updateShortLink(dto));
    }

    /**
     * 分页查询短链接
     * @param
     * @return
     */
    @GetMapping("/page")
    public Result<IPage<PageSelectRespDTO>> pageSelect(PageSelectReqDTO dto){
        //TODO 兼容性问题？，返回的total始终比实际值小1
        return Results.success(service.pageSelect(dto));
    }

    /**
     * 查询短链接数量
     * @param
     * @return
     */
    @GetMapping("/count")
    public Result<List<ShortLinkGroupCountQueryRespDTO>> listGroupShortLinkCount(
            @RequestParam("gids") List<String> gids){
        return Results.success(service.listGroupShortLinkCount(gids));
    }

}
