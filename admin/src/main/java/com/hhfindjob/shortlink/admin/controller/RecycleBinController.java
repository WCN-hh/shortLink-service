package com.hhfindjob.shortlink.admin.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.common.convention.result.Results;
import com.hhfindjob.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.hhfindjob.shortlink.admin.remote.dto.req.PageSelectRecycleReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.req.RecycleReqDTO;
import com.hhfindjob.shortlink.admin.remote.dto.resp.PageSelectRespDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-link/admin/v1/recycle-bin")
public class RecycleBinController {

    //回收站接口
    ShortLinkRemoteService service=new ShortLinkRemoteService() {
    };

    @PostMapping("/save")
    public Result<Boolean> saveRecycleBin(@RequestBody RecycleReqDTO dto){
        return Results.success(service.saveRecycleBin(dto));
    }

    @PostMapping("/recover")
    public Result<Boolean> recoverRecycleBin(@RequestBody RecycleReqDTO dto){
        return Results.success(service.recoverRecycleBin(dto));
    }

    @PostMapping("/remove")
    public Result<Boolean> removeRecycleBin(@RequestBody RecycleReqDTO dto){
        return Results.success(service.removeRecycleBin(dto));
    }

    @GetMapping("/page")
    public Result<IPage<PageSelectRespDTO>> pageSelect(PageSelectRecycleReqDTO dto){
        return Results.success(service.pageSelectRecycle(dto));
    }
}
