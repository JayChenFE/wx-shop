package com.github.jaychenfe.wxshop.service.impl;

import com.github.jaychenfe.wxshop.service.VerificationCodeCheckService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeCheckServiceImpl implements VerificationCodeCheckService {
    private Map<String, String> telNumberToCorrectCode = new ConcurrentHashMap<>();

    @Override
    public void addCode(String tel, String correctCode) {
        telNumberToCorrectCode.put(tel, correctCode);
    }
    @Override
    public String getCorrectCode(String tel) {
        return telNumberToCorrectCode.get(tel);
    }
}
