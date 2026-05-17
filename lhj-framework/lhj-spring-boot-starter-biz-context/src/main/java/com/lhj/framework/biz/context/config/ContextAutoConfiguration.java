package com.lhj.framework.biz.context.config;

import com.lhj.framework.biz.context.filter.HeaderUserId2ContextFilter;
import com.lhj.framework.biz.context.task.LoginUserTransmitTaskDecorator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.web.filter.OncePerRequestFilter;

@AutoConfiguration
@ConditionalOnClass(OncePerRequestFilter.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ContextAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HeaderUserId2ContextFilter headerUserId2ContextFilter() {
        return new HeaderUserId2ContextFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskDecorator loginUserTransmitTaskDecorator() {
        return new LoginUserTransmitTaskDecorator();
    }
}
