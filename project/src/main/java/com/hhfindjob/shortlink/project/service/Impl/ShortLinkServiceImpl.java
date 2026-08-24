package com.hhfindjob.shortlink.project.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhfindjob.shortlink.project.common.convention.exception.ServiceException;
import com.hhfindjob.shortlink.project.common.enums.VailDateTypeEnum;
import com.hhfindjob.shortlink.project.dao.entity.*;
import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.*;
import com.hhfindjob.shortlink.project.dao.mapper.*;
import com.hhfindjob.shortlink.project.dao.mapper.statsMapperSet.*;
import com.hhfindjob.shortlink.project.dto.req.PageSelectReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkCreateOrUpdateReqDTO;
import com.hhfindjob.shortlink.project.dto.resp.PageSelectRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.hhfindjob.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.hhfindjob.shortlink.project.service.ShortLinkService;
import com.hhfindjob.shortlink.project.util.HashUtil;
import com.hhfindjob.shortlink.project.util.IPUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.hhfindjob.shortlink.project.common.constant.RedisCacheConstant.LOCK_SHORT_LINK_GOTO;
import static com.hhfindjob.shortlink.project.common.constant.RedisCacheConstant.SHORT_LINK_GOTO_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriRegisterCachePenetrationBloomFilter;

    private final ShortLinkGotoMapper gotoMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient redissonClient;

    private final LinkAccessStatsMapper accessStatsMapper;

    private final LinkLocaleStatsMapper localeStatsMapper;

    private final LinkOsStatsMapper osStatsMapper;

    private final LinkBrowserStatsMapper browserStatsMapper;

    private final LinkAccessLogsMapper accessLogsMapper;

    private final LinkDeviceStatsMapper deviceStatsMapper;

    private final LinkNetworkStatsMapper networkStatsMapper;

    private final static String DEFAULT_URL="/page/notfound";

    @Value("${short-link.domain.default}")
    private String defaultDomain;

    private final String defaultPort="8002";

    private final String defaultProtocol="http";

    @Override
    @Transactional
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateOrUpdateReqDTO dto) {
        String shortUri = getShortLink(dto);

        ShortLinkDO DO = ShortLinkDO.builder()
                .domain(defaultDomain+":"+defaultPort)
                .shortUri(shortUri)
                .fullShortUrl(String.format("%s:%s/%s",defaultDomain,defaultPort,shortUri))
                .originUrl(dto.getOriginUrl())
                .clickNum(0)
                .gid(dto.getGid())
                .enableStatus(0)
                .describe(dto.getDescribe())
                .favicon(dto.getFavicon())
                .totalPv(0)
                .totalUv(0)
                .totalUip(0)
                .todayPv(0)
                .todayUv(0)
                .todayUip(0)
                .build();

        ShortLinkGotoDO gotoDO = ShortLinkGotoDO.builder()
                .gid(DO.getGid())
                .fullShortUrl(DO.getFullShortUrl())
                .build();

        try {
            baseMapper.insert(DO);
            gotoMapper.insert(gotoDO);
        } catch (DuplicateKeyException ex) {
            log.info("短链接:{}重复入库",dto.getDomain()+"/"+ shortUri);
            throw new ServiceException("短链接生成重复");
        }
        shortUriRegisterCachePenetrationBloomFilter.add("http://"+DO.getFullShortUrl());
        stringRedisTemplate.opsForValue().set(
                SHORT_LINK_GOTO_KEY +DO.getFullShortUrl(), DO.getOriginUrl()
        );

        return BeanUtil.toBean(DO,ShortLinkCreateRespDTO.class);
    }

    @Override
    public IPage<PageSelectRespDTO> pageSelect(PageSelectReqDTO dto) {
//        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
//                .eq(ShortLinkDO::getGid, dto.getGid())
//                .eq(ShortLinkDO::getDelFlag, 0)
//                .eq(ShortLinkDO::getEnableStatus, 0)
//                .orderByDesc(ShortLinkDO::getCreateTime);
//        IPage<ShortLinkDO> resultPage = baseMapper.selectPage(dto, wrapper);
        IPage<ShortLinkDO> resultPage = baseMapper.pageLink(dto);
        IPage<PageSelectRespDTO> convert = resultPage.convert(
                e -> BeanUtil.toBean(e, PageSelectRespDTO.class)
        );
        return convert;
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gids) {
        //shortLinkMapper.listGroupShortLinkCount(gids);
        QueryWrapper<ShortLinkDO> wrapper = Wrappers.query(new ShortLinkDO())
                .select("gid,count(*) as shortLinkCount")
                .in("gid", gids)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(wrapper);
        return BeanUtil.copyToList(maps, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Override
    public Boolean updateShortLink(ShortLinkCreateOrUpdateReqDTO dto) {
        if (true){
            return true;
        }
        LambdaUpdateWrapper<ShortLinkDO> wrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, dto.getGid())
                .eq(ShortLinkDO::getFullShortUrl, dto.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0)
                .set(Objects.equals(
                        dto.getValidDateType(), VailDateTypeEnum.NOTFOREVER.getType()),
                        ShortLinkDO::getValidDate, null);

        int update = baseMapper.update(
                BeanUtil.toBean(dto, ShortLinkDO.class), wrapper);
        return update == 1;
    }

    private String getShortLink(ShortLinkCreateOrUpdateReqDTO dto){
        int count=0;
        String originUrl = dto.getOriginUrl();
        String uri=null;
        while (true){
            if (count>10){
                throw new ServiceException("频繁撞库，请稍后重试");
            }

            uri=HashUtil.hashToBase62(originUrl + count);

            if (!shortUriRegisterCachePenetrationBloomFilter.contains(dto.getDomain()+"/"+uri)){
                break;
            }

            count++;
        }
        return uri;
    }

    @Override
    @SneakyThrows
    public void restoreShortLink(String uri, ServletRequest request, ServletResponse response) {

        String serverName = request.getServerName();
        //"http://"+
        String fullShortUrl = serverName +":"+defaultPort+ "/" + uri;
        String gid=null;
        //查布隆过滤器
        if (!shortUriRegisterCachePenetrationBloomFilter.contains("http://"+fullShortUrl)) {
            redirectFullLink(null, gid,fullShortUrl,response,request);
            return;
        }

        //查缓存
        String originUrl=stringRedisTemplate.opsForValue().get(SHORT_LINK_GOTO_KEY + fullShortUrl);
        if (StrUtil.isNotBlank(originUrl)){
            redirectFullLink(originUrl, gid,fullShortUrl,response,request);
            return;
        }

        RLock lock=redissonClient.getLock(LOCK_SHORT_LINK_GOTO + fullShortUrl);
        lock.lock();
        try {
            originUrl=stringRedisTemplate.opsForValue().get(SHORT_LINK_GOTO_KEY + uri);
            if (StrUtil.isNotBlank(originUrl)){
                redirectFullLink(originUrl, gid,fullShortUrl,response,request);
                return;
            }

            ShortLinkGotoDO gotoDO = getGotoDO(fullShortUrl);
            if (gotoDO == null){
                //应该抛个异常
                redirectFullLink(originUrl, gid,fullShortUrl,response,request);
                return;
            }
            gid = gotoDO.getGid();
            LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, gid)
                    .eq(ShortLinkDO::getShortUri, uri)
                    .eq(ShortLinkDO::getEnableStatus,0)
                    .eq(ShortLinkDO::getDelFlag,0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(wrapper);

            if (shortLinkDO == null){
                //缓存有数据 但 数据库无
                redirectFullLink(originUrl, gid,fullShortUrl,response,request);
                return;
            }

            //返回重定向结果
            originUrl = shortLinkDO.getOriginUrl();
            stringRedisTemplate.opsForValue().set(
                    SHORT_LINK_GOTO_KEY + shortLinkDO.getFullShortUrl(),originUrl);
            redirectFullLink(originUrl, gid,fullShortUrl,response,request);
        } finally {
            lock.unlock();
        }
    }

    private void redirectFullLink(
            String originUrl, String gid,String fullShortUrl,
            ServletResponse response, ServletRequest request) throws IOException {

        if (StrUtil.isBlank(originUrl)){
            ((HttpServletResponse)response).sendRedirect(DEFAULT_URL);
            return;
        }
        counter(gid,fullShortUrl,(HttpServletRequest)request,response);
        ((HttpServletResponse)response).sendRedirect(originUrl);
    }

    private void counter(String gid,String fullShortUrl, HttpServletRequest request,ServletResponse response){
        //cookie检查uv，没有则塞入，同步缓存
        Cookie[] cookies = request.getCookies();
        String uv=null;
        Boolean uvInSet = false;
        boolean hasUvCookie = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("uv".equals(cookie.getName())) {
                    hasUvCookie = true;
                    uvInSet = stringRedisTemplate.opsForSet().isMember(
                            "link:stats:cookieSet:uv", cookie.getValue()
                    );
                    uv=cookie.getValue();
                    break;
                }
            }
        }

        if (!hasUvCookie) {
            // 不存在则塞入 cookie
            uv = RandomUtil.randomString(6);
            Cookie cookie = new Cookie("uv", uv);
            cookie.setMaxAge(60 * 60 * 24 * 30);
            ((HttpServletResponse) response).addCookie(cookie);
            stringRedisTemplate.opsForSet().add("link:stats:cookieSet:uv",uv);
        }

        //TODO 到这里就可以返回了，后面的表更新应该丢进其他线程,但是上方还存在一些公用变量，不好拆分

        // ========== 1. 收集基础信息 ==========
        Date today = new Date();

        String os = IPUtil.getOs(request);
        String ip = IPUtil.getActualIp(request);
        String device = IPUtil.getDevice(request);
        String browser = IPUtil.getBrowser(request);
        String network = IPUtil.getNetwork(request);

        int hour= DateUtil.hour(today,true);
        int week = DateUtil.dayOfWeekEnum(today).getValue();
        // ====================================

        Long added = stringRedisTemplate.opsForSet().add("link:stats:cookieSet:uip", ip);
        boolean ipInSet = added != null && added == 0;

        if (StrUtil.isBlank(gid)){
            //TODO 应该考虑多个环节的值为空的问题
            gid = getGotoDO(fullShortUrl).getGid();
        }

        //基础访问量表数据更新
        //uv，uip做去重
        LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                .pv(1)
                .gid(gid)
                .hour(hour)
                .date(today)
                .weekday(week)
                .uip(ipInSet ? 0:1)
                .fullShortUrl(fullShortUrl)
                .uv(Boolean.TRUE.equals(uvInSet) ? 0:1)
                .build();
        accessStatsMapper.insertAccessStats(linkAccessStatsDO);

        //地区表数据更新
        LinkLocaleStatsDO localeDO = insertLocaleStats(gid, fullShortUrl, ip, today);

        //操作系统记录表 拿os
        LinkOsStatsDO osStatsDO = LinkOsStatsDO.builder()
                .cnt(1)
                .os(os)
                .gid(gid)
                .date(today)
                .fullShortUrl(fullShortUrl)
                .build();
        osStatsMapper.insertOsStats(osStatsDO);

        //浏览器记录表
        LinkBrowserStatsDO browserStatsDO = LinkBrowserStatsDO.builder()
                .cnt(1)
                .gid(gid)
                .date(today)
                .browser(browser)
                .fullShortUrl(fullShortUrl)
                .build();
        browserStatsMapper.insertBrowserStats(browserStatsDO);

        //设备记录表
        LinkDeviceStatsDO deviceStatsDO = LinkDeviceStatsDO.builder()
                .cnt(1)
                .gid(gid)
                .date(today)
                .device(device)
                .fullShortUrl(fullShortUrl)
                .build();
        deviceStatsMapper.insertDeviceStats(deviceStatsDO);

        //网络记录表
        LinkNetworkStatsDO networkStatsDO = LinkNetworkStatsDO.builder()
                .cnt(1)
                .gid(gid)
                .date(today)
                .network(network)
                .fullShortUrl(fullShortUrl)
                .build();
        networkStatsMapper.insertNetworkStats(networkStatsDO);

        //访问日志表 最后记录
        LinkAccessLogsDO accessLogsDO = LinkAccessLogsDO.builder()
                .os(os)
                .ip(ip)
                .user(uv)
                .gid(gid)
                .device(device)
                .browser(browser)
                .network(network)
                .fullShortUrl(fullShortUrl)
                .locale(StrUtil.join("-","中国",localeDO.getProvince(),localeDO.getCity()))
                .build();
        accessLogsMapper.insert(accessLogsDO);
        baseMapper.increment(1,Boolean.TRUE.equals(uvInSet) ? 0:1,ipInSet ? 0:1,gid,fullShortUrl);
    }

    private LinkLocaleStatsDO insertLocaleStats(String gid, String fullShortUrl, String IP, Date today) {
        LinkLocaleStatsDO localeDO = IPUtil.getLocaleStatsDOByIP(IP);
        if (localeDO.getProvince() != null){
            localeDO.setCnt(1);
            localeDO.setGid(gid);
            localeDO.setDate(today);
            localeDO.setCountry("中国");
            localeDO.setFullShortUrl(fullShortUrl);
            localeStatsMapper.insertLocaleStats(localeDO);
        }
        return localeDO;
    }

    private ShortLinkGotoDO getGotoDO(String fullShortUrl){
        LambdaQueryWrapper<ShortLinkGotoDO> wrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
        return gotoMapper.selectOne(wrapper);
    }
}
