package com.github.jaychenfe.wxshop.service.impl;

import com.github.jaychenfe.wxshop.service.AuthService;
import com.github.jaychenfe.wxshop.service.SmsCodeService;
import com.github.jaychenfe.wxshop.service.UsersService;
import com.github.jaychenfe.wxshop.service.VerificationCodeCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UsersService usersService;

    private final VerificationCodeCheckService verificationCodeCheckService;
    private final SmsCodeService smsCodeService;

    @Autowired
    public AuthServiceImpl(UsersService usersService,
                           VerificationCodeCheckService verificationCodeCheckService,
                           SmsCodeService smsCodeService) {
        this.usersService = usersService;
        this.verificationCodeCheckService = verificationCodeCheckService;
        this.smsCodeService = smsCodeService;
    }

    @Override
    public void sendVerificationCode(String tel) {
        usersService.createUserIfNotExist(tel);
        String correctCode = smsCodeService.sendSmsCode(tel);
        verificationCodeCheckService.addCode(tel, correctCode);
    }
}
