package com.zmy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zmy.pojo.entity.User;
import com.zmy.pojo.form.Update.UpdateUserForm;
import com.zmy.pojo.vo.UserInfoVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface UserMapper extends BaseMapper<User> {
    User findUserByUsername(@Param("username") String username);

    UserInfoVo findUserByEmail(@Param("email") String email);

    UserInfoVo findUserByName(@Param("userName")String userName);

    BigDecimal findCountById(@Param("userId") Integer publisher);

    void updateUserInfoById(UpdateUserForm updateUserForm);

    @Insert("INSERT INTO user (username, password, email,count) VALUES (#{username}, #{password}, #{email}, #{count})")
    @Options(useGeneratedKeys = true, keyProperty = "uId") // 自动生成主键
    int insert(User user);
}