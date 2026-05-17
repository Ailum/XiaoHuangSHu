package com.lhj.xiaohuangshu.gateway.auth;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static cn.dev33.satoken.SaManager.log;

@Configuration
public class SaTokenConfigure {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .setAuth(obj -> {
                    log.info("==================> SaReactorFilter, Path: {}", SaHolder.getRequest().getRequestPath());

                    SaRouter.match("/**")
                            .notMatch("/auth/login")
                            .notMatch("/auth/verification/code/send")
                            .check(r -> StpUtil.checkLogin());

                 SaRouter.match("/auth/logout", r -> StpUtil.checkRole("admin"));
            //       SaRouter.match("/auth/logout", r -> StpUtil.checkPermission("app:note:publish"));
//                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
                })
                .setError(e -> {
                    if (e instanceof NotLoginException) {
                        throw new NotLoginException(e.getMessage(), null, null);
                    } else if (e instanceof NotPermissionException) {
                        throw new NotPermissionException(e.getMessage());
                    } else if (e instanceof NotRoleException) {
                        throw new NotRoleException(e.getMessage());
                    } else {
                        throw new RuntimeException(e.getMessage());
                    }
                });
    }
}
