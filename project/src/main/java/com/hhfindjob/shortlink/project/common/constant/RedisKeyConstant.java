package com.hhfindjob.shortlink.project.common.constant;

public class RedisKeyConstant {

    /**
     * 分类不合理,但均使用在
     */

    /**
     * 短链接修改分组 ID 锁前缀 Key
     */
    public static final String LOCK_GID_UPDATE_KEY = "short-link_lock_update-gid_%s";

    /**
     * 短链接延迟队列消费统计 Key
     */
    public static final String DELAY_QUEUE_STATS_KEY = "short-link_delay-queue:stats";

    /**
     * 高德获取地区接口地址
     */
    public static final String AMAP_REMOTE_URL = "https://restapi.amap.com/v3/ip";

}
