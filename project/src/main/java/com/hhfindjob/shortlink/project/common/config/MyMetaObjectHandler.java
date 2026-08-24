package com.hhfindjob.shortlink.project.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

// java example
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @PostConstruct
    public void init() {
        log.info("MyMetaObjectHandler 已注册到 Spring 容器");
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        //this.strictInsertFill(metaObject, "createUserId", Long.class, 123456L);
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "delFlag", Integer.class, 0);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        //this.strictInsertFill(metaObject, "updateUserId", Long.class, 123456L);
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }
}