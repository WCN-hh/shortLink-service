package com.hhfindjob.shortlink.project.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkDO;
import com.hhfindjob.shortlink.project.dto.req.PageSelectRecycleReqDTO;
import com.hhfindjob.shortlink.project.dto.req.RecycleReqDTO;
import com.hhfindjob.shortlink.project.dto.resp.PageSelectRespDTO;

public interface RecycleBinService extends IService<ShortLinkDO> {

    Boolean saveRecycleBin(RecycleReqDTO dto);

    Boolean recoverRecycleBin(RecycleReqDTO dto);

    Boolean removeRecycleBin(RecycleReqDTO dto);

    IPage<PageSelectRespDTO> pageSelect(PageSelectRecycleReqDTO dto);
}
