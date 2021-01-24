package com.github.jaychenfe.wxshop.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.github.jaychenfe.wxshop.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
