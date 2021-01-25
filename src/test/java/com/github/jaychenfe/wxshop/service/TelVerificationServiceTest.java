package com.github.jaychenfe.wxshop.service;

import com.github.jaychenfe.wxshop.controller.AuthController;
import com.github.jaychenfe.wxshop.service.impl.TelVerificationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TelVerificationServiceTest {
    public static AuthController.TelAndCode VALID_PARAMETER = new AuthController.TelAndCode("13800000000", null);
    public static AuthController.TelAndCode VALID_PARAMETER_CODE = new AuthController.TelAndCode("13800000000", "000000");
    public static AuthController.TelAndCode WRONG_CODE = new AuthController.TelAndCode("13800000000", "123456");
    public static AuthController.TelAndCode EMPTY_TEL = new AuthController.TelAndCode(null, null);

    @Test
     void returnTrueIfValid() {
        Assertions.assertTrue(new TelVerificationServiceImpl().verifyTelParameter(VALID_PARAMETER));
    }

    @Test
     void returnFalseIfNoTel() {
        Assertions.assertFalse(new TelVerificationServiceImpl().verifyTelParameter(EMPTY_TEL));
        Assertions.assertFalse(new TelVerificationServiceImpl().verifyTelParameter(null));
    }
}
