package com.hhfindjob.shortlink.admin.remote.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;


@Data
public class PageSelectReqDTO extends Page {

    /**
     * 分组标识
     */
    String gid;

    String orderTag;

    //OrderTag orderTag;
}
