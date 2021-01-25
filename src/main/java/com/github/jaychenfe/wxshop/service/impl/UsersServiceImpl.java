package com.github.jaychenfe.wxshop.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.jaychenfe.wxshop.pojo.Users;
import com.github.jaychenfe.wxshop.mapper.UsersMapper;
import com.github.jaychenfe.wxshop.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-24
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    private final UsersMapper usersMapper;

    @Autowired
    public UsersServiceImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }


    @Override
    public Users createUserIfNotExist(String tel) {
        Users user = new Users();
        user.setTel(tel);
        try {
            usersMapper.insert(user);
        } catch (DuplicateKeyException e) {
            return getUserByTel(tel).get();
        }
        return user;
    }

    @Override
    public Optional<Users> getUserByTel(String tel) {
        return Optional.ofNullable(new LambdaQueryChainWrapper<>(usersMapper).eq(Users::getTel, tel).one());
    }
}
