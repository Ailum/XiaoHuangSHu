package com.lhj.xiaohuangshuauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lhj.xiaohuangshuauth.domain.mapper")
public class XiaohuangshuAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaohuangshuAuthApplication.class, args);
    }

}
