package com.hhfindjob.shortlink.project.dao.mapper.statsMapperSet;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.LinkLocaleStatsDO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    @Insert("insert into t_link_locale_stats " +
            "(full_short_url, gid, date, cnt, province, city, adcode, country, create_time, update_time, del_flag) " +
            "values " +
            "(#{DO.fullShortUrl},#{DO.gid},#{DO.date},#{DO.cnt},#{DO.province}," +
            "#{DO.city},#{DO.adcode},#{DO.country},now(),now(),0)" +
            "on duplicate key update cnt=cnt+#{DO.cnt}")
    void insertLocaleStats(@Param("DO") LinkLocaleStatsDO DO);

    /**
     * 根据短链接获取指定日期内基础监控数据
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_locale_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, province;")
    List<LinkLocaleStatsDO> listLocaleByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内地区监控数据
     */
    @Select("SELECT " +
            "    province, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_locale_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, province;")
    List<LinkLocaleStatsDO> listLocaleByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
