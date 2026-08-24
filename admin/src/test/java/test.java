import org.apache.commons.lang3.RandomStringUtils;

public class test {

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
        for (int i = 0; i < 4; i++) {
            System.out.println(String.format(sql,i));
        }
        // 生成 6 位，包含数字(true) 和 字母(true)
        String code = RandomStringUtils.random(6, true, true);
        System.out.println(code);
    }
}
