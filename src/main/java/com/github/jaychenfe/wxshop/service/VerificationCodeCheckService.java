package com.github.jaychenfe.wxshop.service;

public interface VerificationCodeCheckService {
    void addCode(String tel, String correctCode);

    String getCorrectCode(String tel);
}
