package com.lhj.xiaohuangshu.oss.config;


import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.constant.GlobalConstants;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignFormConfig {
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}
