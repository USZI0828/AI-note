package com.zmy.pojo.vo;

import com.zmy.pojo.entity.Perm;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserInfoVo {
    private Integer uId;
    private String sex;
    private String username;
    private String email;
    private String avatar;
    private String phone;
    private String name;
    private String count;
    private String idNumber;
    private List<Perm> permList = new ArrayList<>();
}