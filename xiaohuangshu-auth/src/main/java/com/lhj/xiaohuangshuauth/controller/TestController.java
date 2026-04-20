package com.lhj.xiaohuangshuauth.controller;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @Value("${rate-limit.api.limit}")
    private Integer limit;

    @GetMapping("/test")
    public String test(){
        return "当前限流阈值为："+limit;
    }
}
