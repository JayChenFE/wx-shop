package com.github.jaychenfe.wxshop.service.impl;

import com.github.jaychenfe.wxshop.service.SmsCodeService;
import org.springframework.stereotype.Service;

@Service
public class MockSmsCodeServiceImpl implements SmsCodeService {
    @Override
    public String sendSmsCode(String tel) {
        return "000000";
    }
}
