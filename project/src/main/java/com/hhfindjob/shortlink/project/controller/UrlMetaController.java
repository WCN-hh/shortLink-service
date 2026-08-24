package com.hhfindjob.shortlink.project.controller;


import com.hhfindjob.shortlink.project.common.convention.result.Result;
import com.hhfindjob.shortlink.project.common.convention.result.Results;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.HttpURLConnection;
import java.net.URL;


@RestController
@RequestMapping("/api/short-link/v1")
public class UrlMetaController {

    /**
     * 获取网站标题
     * @param
     * @return
     */
    @SneakyThrows
    @GetMapping("/title")
    public Result<String> getTitle(@RequestParam String url){
        URL targetUrl=new URL(url);
        HttpURLConnection connection=(HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode=connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            Document document = Jsoup.connect(url).get();
            return Results.success(document.title());
        }
        return Results.success("Error while fetching title");
    }

    /**
     * 网站图标获取
     */
    @SneakyThrows
    @GetMapping("/favicon")
    public Result<String> getFavicon(@RequestParam("url") String url){
        URL targetUrl=new URL(url);
        HttpURLConnection connection=(HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode=connection.getResponseCode();
        Element faviconLink = null;
        if (responseCode == HttpURLConnection.HTTP_OK){
            Document document = Jsoup.connect(url).get();
            faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
        }

        return Results.success(
                faviconLink != null ? faviconLink.attr("abs:href") : null
        );
    }
}
