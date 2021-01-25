package com.github.jaychenfe.wxshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.jaychenfe.wxshop.pojo.Users;

import java.util.Optional;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author jaychenfe
 * @since 2021-01-24
 */
public interface UsersService extends IService<Users> {

    Users createUserIfNotExist(String tel);

    Optional<Users> getUserByTel(String tel);
}
