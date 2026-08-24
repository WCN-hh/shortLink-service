package com.hhfindjob.shortlink.admin.dto.request.user;


import lombok.Data;

@Data
public class UserLoginRequestDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
