package com.hhfindjob.shortlink.project.mq.idempotent;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MessageQueueIdempotentHandler {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String IDEMpotent_key_prefix="short-link:idempotent";

    /**
     * 用于判断当前消息是否被消费
     */
    public Boolean isMessageProcessed(String messageId){
        String key=IDEMpotent_key_prefix + messageId;
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue().setIfAbsent(key, "0", 10, TimeUnit.MINUTES)
        );
    }

    /**
     * 如果消息处理遇到异常情况，删除幂等标识
     * @param messageId
     */
    public void delMessageProcessed(String messageId){
        String key=IDEMpotent_key_prefix + messageId;
        stringRedisTemplate.delete(key);
    }

    /**
     *
     * @param messageId
     * @return
     */
    public Boolean isCompleted(String messageId){
        String key=IDEMpotent_key_prefix + messageId;
        return Objects.equals(stringRedisTemplate.opsForValue().get(key),"1");
    }

    /**
     *  消息唯一标识
     * @param messageId
     */
    public void setComplete(String messageId){
        String key=IDEMpotent_key_prefix + messageId;
        stringRedisTemplate.opsForValue().set(key,"1",10,TimeUnit.MINUTES);
    }
}
