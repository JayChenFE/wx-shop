package com.github.jaychenfe.wxshop.service.impl;


import com.github.jaychenfe.wxshop.controller.AuthController;
import com.github.jaychenfe.wxshop.service.TelVerificationService;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class TelVerificationServiceImpl implements TelVerificationService {
    private static final Pattern TEL_PATTERN = Pattern.compile("1\\d{10}");

    /**
     * 验证输入的参数是否合法：
     * tel必须存在且为合法的中国大陆手机号
     *
     * @param param 输入的参数
     * @return true 合法，否则返回false
     */
    @Override
    public boolean verifyTelParameter(AuthController.TelAndCode param) {
        if (param == null || param.getTel() == null) {
            return false;
        }
        return TEL_PATTERN.matcher(param.getTel()).find();
    }
}
