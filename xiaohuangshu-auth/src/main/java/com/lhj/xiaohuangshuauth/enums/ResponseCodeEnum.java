package com.lhj.xiaohuangshuauth.enums;

import com.lhj.framework.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    SYSTEM_ERROR("AUTH-10000", "系统错误，请稍后重试"),
    PARAM_NOT_VALID("AUTH-10001", "参数错误"),

    VERIFICATION_CODE_SEND_FREQUENTLY("AUTH-20000", "请求太频繁，请 3 分钟后再试"),
    VERIFICATION_CODE_CACHE_ERROR("AUTH-20001", "验证码服务暂不可用，请稍后重试"),
    VERIFICATION_CODE_ERROR("AUTH-20002", "验证码错误"),
    LOGIN_TYPE_ERROR("AUTH-20002", "登录类型错误"),
    USER_NOT_FOUND("AUTH-20003", "该用户不存在"),
    PHONE_OR_PASSWORD_ERROR("AUTH-20004", "手机号或密码错误"),
            ;
    //异常码
    private final String errorCode;
    //错误信息
    private final String errorMessage;
}
