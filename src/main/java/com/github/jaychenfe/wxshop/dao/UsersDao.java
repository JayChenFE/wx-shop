package com.github.jaychenfe.wxshop.dao;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.jaychenfe.wxshop.mapper.UsersMapper;
import com.github.jaychenfe.wxshop.pojo.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("ALL")
@Service
public class UsersDao {
    private final UsersMapper usersMapper;

    @Autowired
    public UsersDao(UsersMapper userMapper) {
        this.usersMapper = userMapper;
    }

    public void insertUser(Users user) {
        usersMapper.insert(user);
    }

    public Users getUserByTel(String tel) {
        return new LambdaQueryChainWrapper<>(usersMapper).eq(Users::getTel, tel).one();
    }
}
