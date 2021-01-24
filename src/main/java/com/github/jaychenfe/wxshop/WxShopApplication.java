package com.github.jaychenfe.wxshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.github.jaychenfe.wxshop.mapper")
public class WxShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(WxShopApplication.class, args);
	}

}
