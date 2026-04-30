package com.lhj.xiaohuangshu.oss.biz.factory;

import com.lhj.xiaohuangshu.oss.biz.strategy.FileStrategy;
import com.lhj.xiaohuangshu.oss.biz.strategy.impl.AliyunOSSFileStrategy;
import com.lhj.xiaohuangshu.oss.biz.strategy.impl.MinioFileStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@RefreshScope
public class FileStrategyFactory {

    @Value("${storage.type}")
    private String strategyType;

    @Bean
    @RefreshScope
    public FileStrategy getFileStrategy() {
        if ("minio".equals(strategyType)) {
            return new MinioFileStrategy();
        } else if ("aliyun".equals(strategyType)) {
            return new AliyunOSSFileStrategy();
        }
        throw new IllegalArgumentException("不可用的存储类型");
    }
}
