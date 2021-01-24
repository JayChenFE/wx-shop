package com.github.jaychenfe.wxshop.service;

import com.github.jaychenfe.wxshop.controller.AuthController;

public interface TelVerificationService {

    boolean verifyTelParameter(AuthController.TelAndCode param);
}
