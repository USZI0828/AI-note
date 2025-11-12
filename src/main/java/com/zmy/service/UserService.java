package com.zmy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zmy.pojo.entity.User;
import com.zmy.pojo.form.LoginForm;
import com.zmy.pojo.form.RegisterForm;
import com.zmy.pojo.form.Update.UpdateUserForm;

public interface UserService extends IService<User> {

    Result<?> login(LoginForm loginForm) throws JsonProcessingException;

    Result<?> getEmailCode(String email);

    Result<?> register(RegisterForm registerForm);

    Result<?> getUserInfo(String userName);

    Result<?> updateUserInfo(UpdateUserForm updateUserForm);
}
