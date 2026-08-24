package com.hhfindjob.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hhfindjob.shortlink.admin.dao.entity.UserDO;
import com.hhfindjob.shortlink.admin.dto.request.user.UserLoginRequestDTO;
import com.hhfindjob.shortlink.admin.dto.request.user.UserRegistRequestDTO;
import com.hhfindjob.shortlink.admin.dto.request.user.UserRequestDTO;
import com.hhfindjob.shortlink.admin.dto.response.user.UserLoginResponseDTO;
import com.hhfindjob.shortlink.admin.dto.response.user.UserResponseDTO;

public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户返回实体
     */
    UserResponseDTO getUserByUsername(String username);

    /**
     * nameIsUsed
     * @param username 名称
     * @return true表示可以使用
     */
    boolean nameNotUsed(String username);

    /**
     * 用户注册
     * @param dto
     * @return true 表示成功
     */
    Boolean newUser(UserRegistRequestDTO dto);

    /**
     * @param dto 修改信息
     * @return 全部用户信息
     */
    //UserActualResponseDTO userIssue(String username);
    void update(UserRequestDTO dto);


    /**
     * 登录
     * @param dto
     */
    UserLoginResponseDTO login(UserLoginRequestDTO dto);

    /**
     * 登录检测
     * @param token 存储在redis
     * @return true 表示登录
     */
    Boolean loginCheck(String username,String token);

    Boolean unlogin(String token);
}
