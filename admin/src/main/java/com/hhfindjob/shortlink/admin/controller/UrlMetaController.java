package com.hhfindjob.shortlink.admin.controller;


import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.remote.dto.ShortLinkRemoteService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/short-link/admin/v1")
public class UrlMetaController {

    ShortLinkRemoteService service=new ShortLinkRemoteService() {
    };

    /**
     * 获取网站标题
     * @param
     * @return
     */
    @SneakyThrows
    @GetMapping("/title")
    public Result<String> getTitle(@RequestParam String url){
        return service.getTitle(url);
    }

    /**
     * 网站图标获取
     */
    @GetMapping()
    public void link1(){

    }
}
