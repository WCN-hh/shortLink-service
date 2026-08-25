package com.hhfindjob.shortlink.project.service.Impl;

import com.hhfindjob.shortlink.project.service.UrlMetaService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;


@Service
@RequiredArgsConstructor
public class UrlMetaServiceImpl implements UrlMetaService {

    @SneakyThrows
    @Override
    public String getTitle(String url) {
        URL targetUrl=new URL(url);
        HttpURLConnection connection=(HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode=connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK){
            Document document = Jsoup.connect(url).get();
            return document.title();
        }
        return "Error while fetching title";
    }

    @SneakyThrows
    @Override
    public String getFavicon(String url) {
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
        return faviconLink != null ? faviconLink.attr("abs:href") : null;

    }
}
