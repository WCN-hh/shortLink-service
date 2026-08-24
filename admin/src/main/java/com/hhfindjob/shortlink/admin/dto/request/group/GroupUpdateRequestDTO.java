package com.hhfindjob.shortlink.admin.dto.request.group;

import lombok.Data;

@Data
public class GroupUpdateRequestDTO {
        /**
         * 分组标识
         */
        //@JsonProperty("gId")
        private String gId;

        /**
         * 创建人
         */
        private String gName;

}
