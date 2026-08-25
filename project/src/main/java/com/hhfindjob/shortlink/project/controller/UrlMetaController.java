package com.hhfindjob.shortlink.project.controller;


import com.hhfindjob.shortlink.project.common.convention.result.Result;
import com.hhfindjob.shortlink.project.common.convention.result.Results;
import com.hhfindjob.shortlink.project.service.UrlMetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/v1")
public class UrlMetaController {

    private final UrlMetaService urlMetaService;

    /**
     * 获取网站标题
     * @param
     * @return
     */
    @GetMapping("/title")
    public Result<String> getTitle(@RequestParam String url){
        return Results.success(urlMetaService.getTitle(url));
    }

    /**
     * 网站图标获取
     */
    @GetMapping("/favicon")
    public Result<String> getFavicon(@RequestParam("url") String url){
        return Results.success(urlMetaService.getFavicon(url));
    }
}
