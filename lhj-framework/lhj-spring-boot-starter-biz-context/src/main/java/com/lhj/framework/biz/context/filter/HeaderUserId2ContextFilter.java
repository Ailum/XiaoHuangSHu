package com.lhj.framework.biz.context.filter;

import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.constant.GlobalConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class HeaderUserId2ContextFilter extends OncePerRequestFilter {

    private static final Log log = LogFactory.getLog(HeaderUserId2ContextFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userId = request.getHeader(GlobalConstants.USER_ID);
        log.info("## HeaderUserId2ContextFilter, userId: " + userId);

        if (!StringUtils.hasText(userId)) {
            filterChain.doFilter(request, response);
            return;
        }

        LoginUserContextHolder.setUserId(userId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            LoginUserContextHolder.remove();
        }
    }
}
