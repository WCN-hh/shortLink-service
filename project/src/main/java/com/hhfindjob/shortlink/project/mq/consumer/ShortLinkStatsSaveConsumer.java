package com.hhfindjob.shortlink.project.mq.consumer;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hhfindjob.shortlink.project.common.convention.exception.ServiceException;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.*;
import com.hhfindjob.shortlink.project.dao.mapper.LinkAccessLogsMapper;
import com.hhfindjob.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.hhfindjob.shortlink.project.dao.mapper.ShortLinkMapper;
import com.hhfindjob.shortlink.project.dao.mapper.statsMapperSet.*;
import com.hhfindjob.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.hhfindjob.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.hhfindjob.shortlink.project.mq.producer.DelayShortLinkStatsProducer;
import com.hhfindjob.shortlink.project.util.IPUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.hhfindjob.shortlink.project.common.constant.RedisKeyConstant.LOCK_GID_UPDATE_KEY;

/**
 * 短链接监控状态保存消息队列消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper gotoMapper;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper accessStatsMapper;
    private final LinkLocaleStatsMapper localeStatsMapper;
    private final LinkOsStatsMapper osStatsMapper;
    private final LinkBrowserStatsMapper browserStatsMapper;
    private final LinkAccessLogsMapper accessLogsMapper;
    private final LinkDeviceStatsMapper deviceStatsMapper;
    private final LinkNetworkStatsMapper networkStatsMapper;
    private final LinkStatsTodayMapper todayMapper;
    private final DelayShortLinkStatsProducer delayShortLinkStatsProducer;
    private final StringRedisTemplate stringRedisTemplate;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String stream = message.getStream();
        RecordId id = message.getId();
        if (!messageQueueIdempotentHandler.isMessageProcessed(id.toString())){
            //进一步判断是否消费
            if (messageQueueIdempotentHandler.isCompleted(id.toString())) {
                return;
            }
            throw new ServiceException("消息未完成流程，需要消息队列重试");
        }
        try {
            Map<String, String> producerMap = message.getValue();
            String fullShortUrl = producerMap.get("fullShortUrl");
            if (StrUtil.isNotBlank(fullShortUrl)) {
                String gid = producerMap.get("gid");
                ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(producerMap.get("record"), ShortLinkStatsRecordDTO.class);
                actualSaveShortLinkStats(fullShortUrl, gid, statsRecord);
            }
            stringRedisTemplate.opsForStream().delete(Objects.requireNonNull(stream), id.getValue());
        } catch (Throwable ex){
            messageQueueIdempotentHandler.delMessageProcessed(id.toString());
            log.error("记录短链接监控消费异常",ex);
        }
        messageQueueIdempotentHandler.setComplete(id.toString());
    }

    public void actualSaveShortLinkStats(String fullShortUrl, String gid, ShortLinkStatsRecordDTO record) {
        fullShortUrl = Optional.ofNullable(fullShortUrl).orElse(record.getFullShortUrl());
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        if (!rLock.tryLock()) {
            delayShortLinkStatsProducer.send(record);
            return;
        }
        try {
            // ==================== 变量初始化 ====================
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = gotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }
            String ip = record.getIp();
            String uv = record.getUv();
            int hour = record.getHour();
            int week = record.getWeek();
            String os = record.getOs();
            Date today = record.getToday();
            String device = record.getDevice();
            String network = record.getNetwork();
            String browser = record.getBrowser();
            Boolean uvInSet = record.getUvInSet();
            Boolean ipInSet = record.getUipInSet();
            // -------------------- 初始化完成 --------------------

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
            shortLinkMapper.increment(1,Boolean.TRUE.equals(uvInSet) ? 0:1,ipInSet ? 0:1,gid,fullShortUrl);
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .todayPv(1)
                    .todayUv(uvInSet ? 0 : 1)
                    .todayUip(ipInSet ? 0 : 1)
                    .gid(gid)
                    .fullShortUrl(fullShortUrl)
                    .date(new Date())
                    .build();
            todayMapper.shortLinkTodayState(linkStatsTodayDO);
        } catch (Throwable ex) {
            log.error("短链接访问量统计异常", ex);
        } finally {
            rLock.unlock();
        }
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
}
