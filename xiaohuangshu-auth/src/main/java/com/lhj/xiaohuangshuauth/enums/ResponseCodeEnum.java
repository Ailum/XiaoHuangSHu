package com.lhj.xiaohuangshuauth.enums;

import com.lhj.framework.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    SYSTEM_ERROR("AUTH-10000", "系统错误，请稍后重试"),
    PARAM_NOT_VALID("AUTH-10001", "参数校验失败"),

    VERIFICATION_CODE_SEND_FREQUENTLY("AUTH-20000", "验证码发送太频繁，请稍后重试"),
    VERIFICATION_CODE_CACHE_ERROR("AUTH-20001", "验证码缓存失败，请稍后重试"),
    VERIFICATION_CODE_ERROR("AUTH-20002", "验证码错误"),
    LOGIN_TYPE_ERROR("AUTH-20003", "登录类型错误"),
    USER_NOT_FOUND("AUTH-20004", "用户不存在"),
    PHONE_OR_PASSWORD_ERROR("AUTH-20005", "手机号或密码错误"),
    LOGIN_FAIL("AUTH-20006", "登录失败");

    /**
     * 异常码。
     */
    private final String errorCode;

    /**
     * 错误信息。
     */
    private final String errorMessage;
}
