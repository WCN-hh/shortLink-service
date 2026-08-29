package com.hhfindjob.shortlink.project.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hhfindjob.shortlink.project.common.convention.exception.ClientException;
import com.hhfindjob.shortlink.project.common.convention.exception.ServiceException;
import com.hhfindjob.shortlink.project.common.enums.VailDateTypeEnum;
import com.hhfindjob.shortlink.project.config.GotoDomainWhiteListConfiguration;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkDO;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.LinkLocaleStatsDO;
import com.hhfindjob.shortlink.project.dao.mapper.LinkAccessLogsMapper;
import com.hhfindjob.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.hhfindjob.shortlink.project.dao.mapper.ShortLinkMapper;
import com.hhfindjob.shortlink.project.dao.mapper.statsMapperSet.*;
import com.hhfindjob.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.hhfindjob.shortlink.project.dto.req.PageSelectReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.hhfindjob.shortlink.project.dto.resp.*;
import com.hhfindjob.shortlink.project.mq.producer.ShortLinkStatsSaveProducer;
import com.hhfindjob.shortlink.project.service.ShortLinkService;
import com.hhfindjob.shortlink.project.service.UrlMetaService;
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
import java.util.*;

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

    private final UrlMetaService urlMetaService;

    private final ShortLinkStatsSaveProducer shortLinkStatsSaveProducer;

    private final GotoDomainWhiteListConfiguration gotoDomainWhiteListConfiguration;

    private final static String DEFAULT_URL="/page/notfound";

    @Value("${short-link.default.domain}")
    private String defaultDomain;

    @Value("${short-link.default.port}")
    private String defaultPort;

    //private final String defaultProtocol="http";

    @Override
    @Transactional
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO dto) {

        verificationWhitelist(dto.getOriginUrl());
        String shortUri = getShortUri(dto);

        ShortLinkDO DO = ShortLinkDO.builder()
                .domain(defaultDomain+":"+defaultPort)
                .shortUri(shortUri)
                .fullShortUrl(String.format("%s:%s/%s",defaultDomain,defaultPort,shortUri))
                .originUrl(dto.getOriginUrl())
                .createType(dto.getCreateType())
                .validDateType(dto.getValidDateType())
                .validDate(dto.getValidDate())
                .clickNum(0)
                .gid(dto.getGid())
                .enableStatus(0)
                .describe(dto.getDescribe())
                .favicon(urlMetaService.getFavicon(dto.getOriginUrl()))
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
        //"http://"+
        shortUriRegisterCachePenetrationBloomFilter.add(DO.getFullShortUrl());
        stringRedisTemplate.opsForValue().set(
                SHORT_LINK_GOTO_KEY +DO.getFullShortUrl(), DO.getOriginUrl()
        );

        return BeanUtil.toBean(DO,ShortLinkCreateRespDTO.class);
    }

    @Override
    public IPage<PageSelectRespDTO> pageSelect(PageSelectReqDTO dto) {
        IPage<ShortLinkDO> resultPage = baseMapper.pageLink(dto);
        return resultPage.convert(
                e -> BeanUtil.toBean(e, PageSelectRespDTO.class)
        );
    }

    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> gids) {
        //shortLinkMapper.listGroupShortLinkCount(gids);
        if (CollUtil.isEmpty(gids)){
            return new ArrayList<>();
        }
        QueryWrapper<ShortLinkDO> wrapper = Wrappers.query(new ShortLinkDO())
                .select("gid,count(*) as shortLinkCount")
                .in("gid", gids)
                .eq("enable_status", 0)
                .groupBy("gid");
        List<Map<String, Object>> maps = baseMapper.selectMaps(wrapper);
        return BeanUtil.copyToList(maps, ShortLinkGroupCountQueryRespDTO.class);
    }

    @Override
    public Boolean updateShortLink(ShortLinkUpdateReqDTO updateReq) {
        verificationWhitelist(updateReq.getOriginUrl());
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, updateReq.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, updateReq.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO oldShortLink = baseMapper.selectOne(wrapper);
        if (oldShortLink == null){
            throw new ClientException("短链接不存在");
        }
        //允许更改的部分为:完整链接,分组gid,描述,过期时间，过期类型标注
        ShortLinkDO newshortLink = BeanUtil.toBean(oldShortLink, ShortLinkDO.class);
        newshortLink.setOriginUrl(updateReq.getOriginUrl());
        newshortLink.setDescribe(updateReq.getDescribe());
        newshortLink.setGid(updateReq.getGid());
        newshortLink.setValidDate(updateReq.getValidDate());
        newshortLink.setValidDateType(updateReq.getValidDateType());

        if (Objects.equals(newshortLink.getGid(),oldShortLink.getGid())){
            //gid不改变
            LambdaUpdateWrapper<ShortLinkDO> wrapperTrue = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, newshortLink.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, newshortLink.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(updateReq.getValidDateType(), VailDateTypeEnum.FOREVER.getType())
                            , ShortLinkDO::getValidDateType, null);
            baseMapper.update(newshortLink,wrapperTrue);
        } else {
            if (true){
                //应该把老gid传入进来
                return true;
            }
            //TODO 更改gid会同时牵连多张监控表，此处未考虑
            LambdaQueryWrapper<ShortLinkDO> wrapperFalse = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, updateReq.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, newshortLink.getGid())
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .eq(ShortLinkDO::getDelFlag, 0);
            baseMapper.delete(wrapper);
            baseMapper.insert(newshortLink);
        }

        return true;
    }

    private String getShortUri(ShortLinkCreateReqDTO dto){
        int count=0;
        String originUrl = dto.getOriginUrl();
        String uri=null;
        while (true){
            if (count>10){
                throw new ServiceException("频繁撞库，请稍后重试");
            }

            uri=HashUtil.hashToBase62(originUrl + count);
            //localhost:8002/uri
            if (!shortUriRegisterCachePenetrationBloomFilter.contains(
                    defaultDomain+":"+defaultPort+"/"+uri)  ){
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
        //查布隆过滤器 "http://"+
        if (!shortUriRegisterCachePenetrationBloomFilter.contains(fullShortUrl)) {
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

    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO dto) {

        List<String> originUrls = dto.getOriginUrls();
        List<String> describes = dto.getDescribes();
        List<LinkBaseInfoRespDTO> results=new ArrayList<>();
        for (int i = 0; i < Math.min(originUrls.size(),describes.size()); i++) {
            ShortLinkCreateReqDTO bean = BeanUtil.toBean(dto, ShortLinkCreateReqDTO.class);
            bean.setDescribe(describes.get(i));
            bean.setOriginUrl(originUrls.get(i));
            try {
                //TODO 事务不生效
                ShortLinkCreateRespDTO shortLink = createShortLink(bean);
                LinkBaseInfoRespDTO info = LinkBaseInfoRespDTO.builder()
                        .describe(describes.get(i))
                        .originUrl(shortLink.getOriginUrl())
                        .fullShortUrl(shortLink.getFullShortUrl())
                        .build();
                results.add(info);
            } catch (Throwable ex){
                log.error("这个链接的创建出错:{}",originUrls.get(i));
            }
        }

        return ShortLinkBatchCreateRespDTO.builder()
                .total(results.size())
                .linkInfos(results)
                .build();
    }

    private void redirectFullLink(
            String originUrl, String gid,String fullShortUrl,
            ServletResponse response, ServletRequest request) throws IOException {

        if (StrUtil.isBlank(originUrl)){
            ((HttpServletResponse)response).sendRedirect(DEFAULT_URL);
            return;
        }

        ShortLinkStatsRecordDTO record = buildLinkStatsRecordAndSetUser(fullShortUrl, (HttpServletRequest) request, response);
        shortLinkStats(fullShortUrl,gid,record);
        ((HttpServletResponse)response).sendRedirect(originUrl);
    }

    private ShortLinkStatsRecordDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, HttpServletRequest request, ServletResponse response){
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

        Date today=new Date();
        String ip = IPUtil.getActualIp(request);
        Long added = stringRedisTemplate.opsForSet().add("link:stats:cookieSet:uip", ip);
        boolean ipInSet = added != null && added == 0;
        return ShortLinkStatsRecordDTO.builder()
                .ip(ip)
                .uv(uv)
                .today(today)
                .uvInSet(uvInSet)
                .uipInSet(ipInSet)
                .os(IPUtil.getOs(request))
                .fullShortUrl(fullShortUrl)
                .device(IPUtil.getDevice(request))
                .browser(IPUtil.getBrowser(request))
                .network(IPUtil.getNetwork(request))
                .hour(DateUtil.hour(today,true))
                .week(DateUtil.dayOfWeekEnum(today).getIso8601Value())
                .build();
    }


    private ShortLinkGotoDO getGotoDO(String fullShortUrl){
        LambdaQueryWrapper<ShortLinkGotoDO> wrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
        return gotoMapper.selectOne(wrapper);
    }

    private void verificationWhitelist(String originUrl) {
        Boolean enable = gotoDomainWhiteListConfiguration.getEnable();
        if (enable == null || !enable) {
            return;
        }
        String domain = IPUtil.extractDomain(originUrl);
        if (StrUtil.isBlank(domain)) {
            throw new ClientException("跳转链接填写错误");
        }
        List<String> details = gotoDomainWhiteListConfiguration.getDetails();
        if (!details.contains(domain)) {
            throw new ClientException("演示环境为避免恶意攻击，请生成以下网站跳转链接：" + gotoDomainWhiteListConfiguration.getNames());
        }
    }

    private void shortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO record){
        Map<String, String> producerMap = new HashMap<>();
        producerMap.put("fullShortUrl", fullShortUrl);
        producerMap.put("gid", gid);
        producerMap.put("record", JSON.toJSONString(record));
        shortLinkStatsSaveProducer.send(producerMap);
    }

    //弃用
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
}
