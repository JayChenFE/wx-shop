package com.github.jaychenfe.wxshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.github.jaychenfe.wxshop.mapper")
@EnableTransactionManagement
public class WxShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxShopApplication.class, args);
    }

}
