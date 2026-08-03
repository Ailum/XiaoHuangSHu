package com.lhj.xiaohuangshu.note.biz;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.lhj.xiaohuangshu.note.biz.domain.mapper")
@EnableFeignClients(basePackages = "com.lhj.xiaohuangshu")
public class XiaohuangshuNoteBizApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaohuangshuNoteBizApplication.class, args);
    }
}
