package com.hhfindjob.shortlink.project.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkDO;
import lombok.Data;


@Data
public class PageSelectReqDTO extends Page<ShortLinkDO> {

    /**
     * 分组标识
     */
    String gid;

    /**
     * 排序字段
     */
    String orderTag;

    //OrderTag orderTag;
}
