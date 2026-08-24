package com.hhfindjob.shortlink.project.dao.mapper.statsMapperSet;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.LinkDeviceStatsDO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import com.hhfindjob.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LinkDeviceStatsMapper extends BaseMapper<LinkDeviceStatsDO> {

    /**
     * 记录浏览器访问监控数据
     */
    @Insert("INSERT INTO t_link_device_stats " +
            "(full_short_url, gid, date, cnt, device, create_time, update_time, del_flag) " +
            "VALUES" +
            "(#{DO.fullShortUrl}, #{DO.gid}, #{DO.date}, #{DO.cnt}, #{DO.device}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt + #{DO.cnt};")
    void insertDeviceStats(@Param("DO") LinkDeviceStatsDO DO);

    /**
     * 根据短链接获取指定日期内访问设备监控数据
     */
    @Select("SELECT " +
            "    device, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_device_stats " +
            "WHERE " +
            "    full_short_url = #{param.fullShortUrl} " +
            "    AND gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    full_short_url, gid, device;")
    List<LinkDeviceStatsDO> listDeviceStatsByShortLink(@Param("param") ShortLinkStatsReqDTO requestParam);

    /**
     * 根据分组获取指定日期内访问设备监控数据
     */
    @Select("SELECT " +
            "    device, " +
            "    SUM(cnt) AS cnt " +
            "FROM " +
            "    t_link_device_stats " +
            "WHERE " +
            "    gid = #{param.gid} " +
            "    AND date BETWEEN #{param.startDate} and #{param.endDate} " +
            "GROUP BY " +
            "    gid, device;")
    List<LinkDeviceStatsDO> listDeviceStatsByGroup(@Param("param") ShortLinkGroupStatsReqDTO requestParam);
}
