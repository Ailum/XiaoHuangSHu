package com.lhj.xiaohuangshu.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AddUserId2HeaderFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER_ID = "userId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("==================> AddUserId2HeaderFilter");

        Long userId;
        try {
            userId = StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            return chain.filter(exchange);
        }

        log.info("## 当前登录的用户ID: {}", userId);
        ServerWebExchange newExchange = exchange.mutate()
                .request(builder -> builder.headers(headers -> headers.remove(HEADER_USER_ID))
                        .header(HEADER_USER_ID, String.valueOf(userId)))
                .build();

        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
