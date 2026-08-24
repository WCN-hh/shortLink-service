package com.hhfindjob.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hhfindjob.shortlink.admin.common.convention.exception.ClientException;
import com.hhfindjob.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * 用户信息传输过滤器
 *
 * @公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    private static final List<String> SKIP_PATHS = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username",
            "/api/short-link/admin/v1/title");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String uri = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();

        boolean isRegistry = "/api/short-link/admin/v1/user".equals(uri) && "POST".equals(method);
        boolean needAuth = !SKIP_PATHS.contains(uri) && !isRegistry;

        if (needAuth){
            String userName = httpServletRequest.getHeader("username");
            String token =httpServletRequest.getHeader("token");
            // 此处等价于 userName == null || token == null
            if (!StrUtil.isAllNotBlank(userName,token)) {
                returnJson(
                        (HttpServletResponse) servletResponse,
                        JSON.toJSONString(Results.failure(new ClientException("用户验证失败")))
                );
                return;
            }

            Object userInfoJsonStr = stringRedisTemplate.opsForHash().get("login_"+userName,token);

            if (userInfoJsonStr != null){
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(),UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            } else {
                //没有的情况下传入空对象
                //UserContext.setUser(new UserInfoDTO());
                returnJson(
                        (HttpServletResponse) servletResponse,
                        JSON.toJSONString(Results.failure(new ClientException("用户验证失败")))
                );
                return;
            }
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }

    private void returnJson(HttpServletResponse response,String json){
        PrintWriter writer =null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset-utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);
        } catch (IOException io) {
        } finally {
            if (writer != null){
                writer.close();
            }
        }
    }
}