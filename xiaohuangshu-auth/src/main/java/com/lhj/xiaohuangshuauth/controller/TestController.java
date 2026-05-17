package com.lhj.xiaohuangshuauth.controller;

import com.lhj.xiaohuangshuauth.alarm.AlarmInterface;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RefreshScope
public class TestController {

    @Resource
    private AlarmInterface alarm;

    @Value("${rate-limit.api.limit}")
    private Integer limit;

    @GetMapping("/test")
    public String test() {
        return "current rate limit is: " + limit;
    }

    @GetMapping("/alarm")
    public String sendAlarm() {
        alarm.send("绯荤粺鍑洪敊浜嗭紝浣犲潩椹笂涓婄嚎瑙ｅ喅");
        return "alarm success";
    }
}
