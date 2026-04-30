package com.lhj.xiaohuangshu.oss.biz.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "storage.type", havingValue = "aliyun")
public class AliyunOSSConfig {

    @Resource
    private AliyunOSSProperties aliyunOSSProperties;
    /**
     * 构建 阿里云 OSS 客户端
     *
     * @return
     */

    @Bean
    public OSS aliyunOSSClient() {
        //设置访问凭证
        return new OSSClientBuilder().build(
                aliyunOSSProperties.getEndpoint(),
                aliyunOSSProperties.getAccessKey(),
                aliyunOSSProperties.getSecretKey()
        );
    }
}
