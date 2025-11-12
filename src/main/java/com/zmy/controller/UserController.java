package com.zmy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zmy.common.Result;
import com.zmy.pojo.form.LoginForm;
import com.zmy.pojo.form.RegisterForm;
import com.zmy.pojo.form.Update.UpdateUserForm;
import com.zmy.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户控制器")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginForm loginForm) throws JsonProcessingException {
        return userService.login(loginForm);
    }

    @Operation(summary = "发送验证码")
    @GetMapping("/getEmail_code")
    public Result<?> getEmailCode(@RequestParam("email") String email){
        return userService.getEmailCode(email);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterForm registerForm) {
        return userService.register(registerForm);
    }

    @Operation(summary = "查看个人信息")
    @GetMapping("/getUserInfo")
    public Result<?> getUserInfo(@RequestParam("userName")String userName) {
        return userService.getUserInfo(userName);
    }

    @Operation(summary = "根据用户id修改用户信息")
    @PutMapping("/updateUserInfo")
    public Result<?> updateUserInfo(@RequestBody UpdateUserForm updateUserForm) {
        return userService.updateUserInfo(updateUserForm);
    }

}
