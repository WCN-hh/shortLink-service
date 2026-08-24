package com.hhfindjob.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.hhfindjob.shortlink.admin.common.convention.result.Result;
import com.hhfindjob.shortlink.admin.common.convention.result.Results;
import com.hhfindjob.shortlink.admin.dto.request.user.UserLoginRequestDTO;
import com.hhfindjob.shortlink.admin.dto.request.user.UserRegistRequestDTO;
import com.hhfindjob.shortlink.admin.dto.request.user.UserRequestDTO;
import com.hhfindjob.shortlink.admin.dto.response.user.UserActualResponseDTO;
import com.hhfindjob.shortlink.admin.dto.response.user.UserLoginResponseDTO;
import com.hhfindjob.shortlink.admin.dto.response.user.UserResponseDTO;
import com.hhfindjob.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-link/admin/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     *  根据用户名查找用户信息
     */
    @GetMapping("/user/{username}")
    public Result<UserResponseDTO> getUserByName(@PathVariable("username") String username){
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 查询用户无脱敏信息
     */
    @GetMapping("/actual/user/{username}")
    public Result<UserActualResponseDTO> userIssue(@PathVariable("username") String username){
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username),UserActualResponseDTO.class));
    }

    /**
     * 查询用户名是否可以使用
     */
    @GetMapping("/user/has-username")
    public Result<String> nameNotUsed(@RequestParam("username") String username){
        return Results.success(userService.nameNotUsed(username) ? "可以使用": "已经被使用");
    }

    /**
     *  创建用户
     * @param dto 接收用dto
     */
    @PostMapping("/user")
    public Result<Boolean> createUser(@RequestBody UserRegistRequestDTO dto){
        return Results.success(userService.newUser(dto));
    }

    /**
     *  修改用户信息 TODO
     * @param dto 接收用dto
     */
    @PutMapping("/user")
    public Result updateUser(@RequestBody UserRequestDTO dto){
        userService.update(dto);
        return Results.success();
    }

    /**
     * 用户登录
     * @param dto
     * @return true 表示成功
     */
    @PostMapping("/user/login")
    public Result<UserLoginResponseDTO> userLogin(@RequestBody UserLoginRequestDTO dto){
        UserLoginResponseDTO token = userService.login(dto);
        return Results.success(token);
    }

    /**
     * 检查请求用户是否登录
     * @return true 表示已经登录
     */
    @GetMapping("/user/check-login")
    public Result<Boolean> loginCheck(
            @RequestParam("username") String username,
            @RequestParam("token") String token){
        return Results.success(userService.loginCheck(username,token));
    }

    /**
     * 退出登录
     * @return true表示退出成功
     */
    @DeleteMapping("/user/logout")
    public Result<Boolean> leave(@RequestParam("token") String token){
        return Results.success(userService.unlogin(token));
    }

}
