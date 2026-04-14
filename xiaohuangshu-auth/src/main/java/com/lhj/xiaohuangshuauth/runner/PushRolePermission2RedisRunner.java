package com.lhj.xiaohuangshuauth.runner;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PushRolePermission2RedisRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        log.info("==> 服务启动，开始同步角色权限数据到 Redis 中...");

        // todo

        log.info("==> 服务启动，成功同步角色权限数据到 Redis 中...");
    }
}
