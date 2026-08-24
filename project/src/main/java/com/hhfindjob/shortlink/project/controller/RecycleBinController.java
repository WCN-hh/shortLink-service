package com.hhfindjob.shortlink.project.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhfindjob.shortlink.project.dto.req.PageSelectRecycleReqDTO;
import com.hhfindjob.shortlink.project.dto.req.RecycleReqDTO;
import com.hhfindjob.shortlink.project.dto.resp.PageSelectRespDTO;
import com.hhfindjob.shortlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-link/v1/recycle-bin")
@RequiredArgsConstructor
public class RecycleBinController {

    //回收站接口
    private final RecycleBinService service;

    @PostMapping("/save")
    public Boolean saveRecycleBin(@RequestBody RecycleReqDTO dto){
        return service.saveRecycleBin(dto);
    }

    @PostMapping("/recover")
    public Boolean recoverRecycleBin(@RequestBody RecycleReqDTO dto){
        return service.recoverRecycleBin(dto);
    }

    @PostMapping("/remove")
    public Boolean removeRecycleBin(@RequestBody RecycleReqDTO dto){
        return service.removeRecycleBin(dto);
    }

    @GetMapping("/page")
    public IPage<PageSelectRespDTO> pageSelect(PageSelectRecycleReqDTO dto){
        return service.pageSelect(dto);
    }
}
