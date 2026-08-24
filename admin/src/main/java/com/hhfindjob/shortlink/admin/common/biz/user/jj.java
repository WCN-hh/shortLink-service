package com.hhfindjob.shortlink.admin.common.biz.user;

public class jj {
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) req;
//        String uri = request.getRequestURI();
//        String method = request.getMethod();
//
//        boolean isRegistry = "/api/short-link/admin/v1/user".equals(uri) && "POST".equals(method);
//        boolean needAuth = !SKIP_PATHS.contains(uri) && !isRegistry;
//
//        if (needAuth) {
//            String username = request.getHeader("username");
//            String token = request.getHeader("token");
//            Object cached = stringRedisTemplate.opsForHash().get("login_" + username, token);
//
//            UserInfoDTO user = (cached != null)
//                    ? JSON.parseObject(cached.toString(), UserInfoDTO.class)
//                    : new UserInfoDTO();
//            UserContext.setUser(user);
//        }
//
//        try {
//            chain.doFilter(req, res);
//        } finally {
//            UserContext.removeUser();
//        }
//    }

}
