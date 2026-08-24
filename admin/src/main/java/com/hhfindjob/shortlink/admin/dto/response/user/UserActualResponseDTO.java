package com.hhfindjob.shortlink.admin.dto.response.user;


import lombok.Data;

/**
 * 用户返回参数响应
 */
@Data
public class UserActualResponseDTO {

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
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

//    public static <T extends UserDO> UserActualResponseDTO  copy(T userDO){
//        UserActualResponseDTO dto=new UserActualResponseDTO();
//        BeanUtils.copyProperties(userDO,dto);
//        return dto;
//    }

}
