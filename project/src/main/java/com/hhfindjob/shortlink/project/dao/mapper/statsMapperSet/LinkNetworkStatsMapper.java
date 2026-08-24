package com.hhfindjob.shortlink.project.dao.mapper.statsMapperSet;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.LinkNetworkStatsDO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LinkNetworkStatsMapper extends BaseMapper<LinkNetworkStatsDO> {

    /**
     * 记录浏览器访问监控数据
     */
    @Insert("INSERT INTO t_link_network_stats " +
            "(full_short_url, gid, date, cnt, network, create_time, update_time, del_flag) " +
            "VALUES" +
            "(#{DO.fullShortUrl}, #{DO.gid}, #{DO.date}, #{DO.cnt}, #{DO.network}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{DO.cnt};")
    void insertNetworkStats(@Param("DO") LinkNetworkStatsDO DO);
    /**
     * 根据短链接获取指定日期内访问网络监控数据
     */
    @Select("SELECT " +
            "    network, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_network_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, network;")
    List<LinkNetworkStatsDO> listNetworkStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内访问网络监控数据
     */
    @Select("SELECT " +
            "    network, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_network_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, network;")
    List<LinkNetworkStatsDO> listNetworkStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
