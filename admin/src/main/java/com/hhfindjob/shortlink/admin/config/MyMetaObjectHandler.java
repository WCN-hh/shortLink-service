package com.hhfindjob.shortlink.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

// java example
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        Date now = new Date();
        //this.strictInsertFill(metaObject, "createUserId", Long.class, 123456L);
        this.strictInsertFill(metaObject, "createTime", Date.class, now);
        this.strictInsertFill(metaObject, "updateTime", Date.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        //this.strictInsertFill(metaObject, "updateUserId", Long.class, 123456L);
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    }
}