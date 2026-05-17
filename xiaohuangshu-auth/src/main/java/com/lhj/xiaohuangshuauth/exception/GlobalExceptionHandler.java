package com.lhj.xiaohuangshuauth.exception;


import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshuauth.enums.ResponseCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 鎹曡幏鑷畾涔変笟鍔″紓甯?
     * @return
     */
    @ExceptionHandler({ BizException.class })
    @ResponseBody
    public Response<Object> handleBizException(HttpServletRequest request, BizException e) {
        log.warn("{} request fail, errorCode: {}, errorMessage: {}", request.getRequestURI(), e.getErrorCode(), e.getErrorMessage());
        return Response.fail(e);
    }

    /**
     * 鎹曡幏鍙傛暟鏍￠獙寮傚父
     * @return
     */
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    @ResponseBody
    public Response<Object> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        // 鍙傛暟閿欒寮傚父鐮?
        String errorCode = ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode();

        // 鑾峰彇 BindingResult
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder sb = new StringBuilder();

        // 鑾峰彇鏍￠獙涓嶉€氳繃鐨勫瓧娈碉紝骞剁粍鍚堥敊璇俊鎭紝鏍煎紡涓猴細 email 閭鏍煎紡涓嶆纭? 褰撳墠鍊? '123124qq.com';
        Optional.ofNullable(bindingResult.getFieldErrors()).ifPresent(errors -> {
            errors.forEach(error ->
                    sb.append(error.getField())
                            .append(" ")
                            .append(error.getDefaultMessage())
                            .append(", 褰撳墠鍊? '")
                            .append(error.getRejectedValue())
                            .append("'; ")

            );
        });

        // 閿欒淇℃伅
        String errorMessage = sb.toString();

        log.warn("{} request error, errorCode: {}, errorMessage: {}", request.getRequestURI(), errorCode, errorMessage);

        return Response.fail(errorCode, errorMessage);
    }

    /**
     * 鎹曡幏 guava 鍙傛暟鏍￠獙寮傚父
     * @return
     */
@ExceptionHandler({IllegalArgumentException.class})
@ResponseBody
public Response<Object> handleIllegalArgrmentException(HttpServletRequest request, IllegalArgumentException e) {
    //鍙傛暟閿欒寮傚父鐮?
    String errorCode = ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode();
    //閿欒淇℃伅
    String errorMessage = e.getMessage();

    log.warn("{} request error, errorMessage: {}", request.getRequestURI(),errorCode, errorMessage);

    return Response.fail(errorCode, errorMessage);
}


    /**
     * 鍏朵粬绫诲瀷寮傚父
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler({ Exception.class })
    @ResponseBody
    public Response<Object> handleOtherException(HttpServletRequest request, Exception e) {
        log.error("{} request error, ", request.getRequestURI(), e);
        return Response.fail(ResponseCodeEnum.SYSTEM_ERROR);
    }
}
