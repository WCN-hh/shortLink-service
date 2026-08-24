package com.hhfindjob.shortlink.admin.dto.response.group;


import lombok.Data;

@Data
public class GroupResponseDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 分组标识
     */
    private String gId;

    /**
     * 分组标识
     */
    private String gName;

    /**
     * 创建人
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 短链接数量
     */
    private Integer shortLinkCount;

}
