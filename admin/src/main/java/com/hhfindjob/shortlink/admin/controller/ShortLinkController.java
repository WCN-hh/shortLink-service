package com.hhfindjob.shortlink.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.hhfindjob.shortlink.admin.remote.dto.req.PageSelectReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkBatchCreateReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.LinkBaseInfoRespDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.PageSelectRespDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkBatchCreateRespDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.hhfindjob.shortlink.admin.util.EasyExcelWebUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO dto){
        return service.createShortLink(dto);
    }

    /**
     * 批量创建短链接
     * @param dto
     * @return
     */
    @PostMapping("/create/batch")
    public void batchCreateShortLink(
            @RequestBody ShortLinkBatchCreateReqDTO dto, HttpServletResponse response){
        Result<ShortLinkBatchCreateRespDTO> result = service.batchCreateShortLink(dto);
        if (result.isSuccess()){
            List<LinkBaseInfoRespDTO> linkInfos = result.getData().getLinkInfos();
            EasyExcelWebUtil.write(response,"批量创建成功列表", LinkBaseInfoRespDTO.class,linkInfos);
        }
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
