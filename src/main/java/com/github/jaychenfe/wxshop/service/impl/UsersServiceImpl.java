package com.github.jaychenfe.wxshop.service.impl;

import com.github.jaychenfe.wxshop.pojo.Users;
import com.github.jaychenfe.wxshop.mapper.UsersMapper;
import com.github.jaychenfe.wxshop.service.UsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
