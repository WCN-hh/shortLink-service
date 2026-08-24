package com.hhfindjob.shortlink.project.controller;

import com.hhfindjob.shortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestoreController {
    private final ShortLinkService service;

    //重定向接口
    @GetMapping("/{short-uri}")
    public void restoreShortLink(
            @PathVariable("short-uri") String uri,
            ServletRequest request, ServletResponse response){
        service.restoreShortLink(uri,request,response);
    }
}
