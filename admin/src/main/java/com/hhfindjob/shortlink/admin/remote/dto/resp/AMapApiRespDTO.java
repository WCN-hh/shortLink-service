package com.hhfindjob.shortlink.admin.remote.dto.resp;

import lombok.Data;

@Data
public class AMapApiRespDTO {

    private String status;

    private String info;

    private String province;

    private String city;

    private String adcode;

    private String rectangle;
}
