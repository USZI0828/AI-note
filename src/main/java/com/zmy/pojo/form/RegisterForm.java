package com.zmy.pojo.form;

import lombok.Data;

@Data
public class RegisterForm {
    private String username;
    private String password;
    private String email;
    private String code;
    private Integer permId;
}