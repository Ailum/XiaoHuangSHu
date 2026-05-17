package com.lhj.framework.biz.context.config;

import com.lhj.framework.biz.context.interceptor.FeignRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @description: Feign request interceptor auto configuration
 */
@AutoConfiguration
@ConditionalOnClass(RequestInterceptor.class)
public class FeignContextAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RequestInterceptor.class)
    public RequestInterceptor feignRequestInterceptor() {
        return new FeignRequestInterceptor();
    }
}
