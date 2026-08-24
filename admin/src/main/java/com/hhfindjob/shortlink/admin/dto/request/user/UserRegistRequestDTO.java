package com.hhfindjob.shortlink.admin.dto.request.user;


import lombok.Data;

/**
 * 用户登录请求参数
 */
@Data
public class UserRegistRequestDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
