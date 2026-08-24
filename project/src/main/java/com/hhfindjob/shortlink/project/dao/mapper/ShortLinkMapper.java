package com.hhfindjob.shortlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhfindjob.shortlink.project.dao.entity.ShortLinkDO;
import com.hhfindjob.shortlink.project.dto.req.PageSelectReqDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ShortLinkMapper extends BaseMapper<ShortLinkDO> {
    @Update("update t_link " +
            "set total_pv=total_pv + #{total_pv},total_uv=total_uv + #{total_uv},total_uip=total_uip + #{total_uip} " +
            "where gid = #{gid} and full_short_url = #{full_short_url}")
    void increment(
            @Param("total_pv") Integer totalPv,
            @Param("total_uv") Integer totalUv,
            @Param("total_uip") Integer totalUip,
            @Param("gid") String gid,
            @Param("full_short_url") String fullShortUrl);

    /**
     * 分页统计短链接
     */
    IPage<ShortLinkDO> pageLink(PageSelectReqDTO requestParam);
}
