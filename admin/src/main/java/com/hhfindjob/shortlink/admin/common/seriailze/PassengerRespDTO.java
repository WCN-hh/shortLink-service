package com.hhfindjob.shortlink.admin.common.seriailze;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 乘车人返回参数
 */
@Data
@Accessors(chain = true)
public class PassengerRespDTO {

    /**
     * 证件号码
     */
    @JsonSerialize(using = IdCardDesensitizationSerializer.class)
    private String idCard;

    /**
     * 手机号
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;
}