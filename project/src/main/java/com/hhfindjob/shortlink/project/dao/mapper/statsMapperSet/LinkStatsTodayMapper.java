package com.hhfindjob.shortlink.project.dao.mapper.statsMapperSet;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.LinkStatsTodayDO;
import org.apache.ibatis.annotations.Insert;

public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {
    @Insert("insert into t_link_stats_today " +
            "(gid, full_short_url, date, today_pv, today_uv, today_uip, create_time, update_time, del_flag) " +
            "VALUES " +
            "()"
    )
    void countLog();
}
