package com.zmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmy.pojo.entity.User;
import com.zmy.pojo.form.Update.UpdateUserForm;
import com.zmy.pojo.vo.UserInfoVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.springframework.data.repository.query.Param;


public interface UserMapper extends BaseMapper<User> {
    User findUserByUsername(@Param("username") String username);

    UserInfoVo findUserByEmail(@Param("email") String email);

    UserInfoVo findUserByName(@Param("userName")String userName);

    void updateUserInfoById(UpdateUserForm updateUserForm);

    @Insert("INSERT INTO user (username, password, email) VALUES (#{username}, #{password}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "userId") // 自动生成主键
    int insert(User user);
}