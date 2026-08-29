package com.hhfindjob.shortlink.project.dao.mapper.statsMapperSet;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {
    /**
     * 记录今日统计监控数据
     */
    @Insert("INSERT INTO t_link_stats_today " +
            "(full_short_url, gid, date,  today_uv, today_pv, today_uip, create_time, update_time, del_flag) " +
            "VALUES" +
            "(#{DO.fullShortUrl}, #{DO.gid}, #{DO.date}, #{DO.todayUv}, #{DO.todayPv}, #{DO.todayUip}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE " +
            "today_uv = today_uv +  #{DO.todayUv}, today_pv = today_pv +  #{DO.todayPv}, today_uip = today_uip +  #{DO.todayUip};")
    void shortLinkTodayState(@Param("linkTodayStats") LinkStatsTodayDO DO);
}
