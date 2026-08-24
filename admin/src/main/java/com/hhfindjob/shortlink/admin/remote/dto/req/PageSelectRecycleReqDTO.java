package com.hhfindjob.shortlink.admin.remote.dto.req;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hhfindjob.shortlink.admin.dao.entity.ShortLinkDO;
import lombok.Data;

import java.util.List;


@Data
public class PageSelectRecycleReqDTO extends Page<ShortLinkDO> {

    /**
     * 分组标识
     */
    List<String> gidList;
}