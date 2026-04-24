package com.lhj.xiaohuangshu.gateway.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.gateway.enums.ResponseCodeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        log.error("==> Global exception caught: ", ex);

        Response<?> result;
        if (ex instanceof NotLoginException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            result = Response.fail(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(), ex.getMessage());
        } else if (ex instanceof NotPermissionException || ex instanceof NotRoleException) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            result = Response.fail(ResponseCodeEnum.UNAUTHORIZED.getErrorCode(), ex.getMessage());
        } else {
            result = Response.fail(ResponseCodeEnum.SYSTEM_ERROR);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
            } catch (Exception e) {
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
