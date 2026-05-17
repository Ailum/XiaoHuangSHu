package com.lhj.framework.biz.context.interceptor;


import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.constant.GlobalConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @description: Feign 请求拦截器
 */
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取上下文用户ID
        Long userId = LoginUserContextHolder.getUserId();

        //若不为空，则添加到请求头中
        if(Objects.nonNull(userId)){
            requestTemplate.header(GlobalConstants.USER_ID, String.valueOf(userId));
            log.info("########## feign 请求设置请求头 userId: {}", userId);
        }
    }
}
