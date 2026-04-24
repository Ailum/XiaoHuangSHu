package com.lhj.xiaohuangshuauth.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.lhj.framework.common.constant.GlobalConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class HeaderUser2ContrextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String userId = request.getHeader(GlobalConstants.USER_ID);
        log.info("## HeaderUserId2ContrextFilter,用户ID:{}", userId);

        if (StringUtils.isBlank(userId)) {
            chain.doFilter(request, response);
            return;
        }

        log.info("==== 设置userId 到 ThreadLocal 中,用户ID:{}", userId);
        LoginUserContextHolder.setUserId(userId);

        try {
            chain.doFilter(request, response);
        } finally {
            LoginUserContextHolder.remove();
            log.info("==== 删除 ThreadLocal, userId: {}", userId);
        }
    }
}
