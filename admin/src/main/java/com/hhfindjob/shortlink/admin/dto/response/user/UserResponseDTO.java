package com.hhfindjob.shortlink.admin.dto.response.user;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hhfindjob.shortlink.admin.common.seriailze.PhoneDesensitizationSerializer;
import com.hhfindjob.shortlink.admin.dao.entity.UserDO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 用户返回参数响应
 */
@Data
public class UserResponseDTO {

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    public static UserResponseDTO copy(UserDO userDO){
        UserResponseDTO dto=new UserResponseDTO();
        BeanUtils.copyProperties(userDO,dto);
        return dto;
    }

}
