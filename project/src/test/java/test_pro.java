import com.hhfindjob.shortlink.project.dao.entity.statsDOSet.LinkLocaleStatsDO;
import com.hhfindjob.shortlink.project.util.IPUtil;

import java.util.Date;

public class test_pro {

    static String sql="create table saas_group_%d\n" +
            "(\n" +
            "    id          bigint auto_increment comment 'ID'\n" +
            "        primary key,\n" +
            "    g_id        varchar(32)          null comment '分组标识',\n" +
            "    g_name      varchar(64)          null comment '分组标识',\n" +
            "    username    varchar(256)         null comment '创建人',\n" +
            "    sort_order  int        default 0 null,\n" +
            "    create_time datetime             null comment '创建时间',\n" +
            "    update_time datetime             null comment '更新时间',\n" +
            "    del_flag    tinyint(1) default 0 null comment '删除标识 0：未删除',\n" +
            "    constraint idx_unique_username_gid\n" +
            "        unique (g_id, username)\n" +
            ");";

    public static void main(String[] args) {
        LinkLocaleStatsDO ip = IPUtil.getLocaleStatsDOByIP("114.247.50.2");
        Date createTime = ip.getCreateTime();
        System.out.println(createTime == null);
        System.out.println(ip);
    }
}
