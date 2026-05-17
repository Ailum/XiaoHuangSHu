package com.lhj.xiaohuangshu.user.biz;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.lhj.xiaohuangshu.user.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.lhj.xiaohuangshu")
public class XiaohuangshuUserBizApplication {

    public static void main(String[] args) {

        SpringApplication.run(XiaohuangshuUserBizApplication.class, args);
    }
}
