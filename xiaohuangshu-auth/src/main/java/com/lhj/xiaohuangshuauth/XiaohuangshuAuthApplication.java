package com.lhj.xiaohuangshuauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.lhj.xiaohuangshu.user.api")
public class XiaohuangshuAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaohuangshuAuthApplication.class, args);
    }
}
