package com.hhfindjob.shortlink.admin.dto.response.user;


import lombok.Data;

@Data
public class UserLoginResponseDTO {
    private String token;

    public UserLoginResponseDTO(String token) {
        this.token=token;
    }
}
