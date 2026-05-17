package com.lhj.xiaohuangshu.user.biz.enums;

import com.lhj.framework.common.exception.BaseExceptionInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum implements BaseExceptionInterface {

    SYSTEM_ERROR("USER-10000", "系统错误，请稍后重试"),
    PARAM_NOT_VALID("USER-10001", "参数校验失败"),

    NICK_NAME_VALID_FAIL("USER-20001", "昵称长度需为 2-24 个字符，且不能包含特殊字符"),
    XIAOHASHU_ID_VALID_FAIL("USER-20002", "小黄书号长度需为 6-15 个字符，且只能包含字母、数字、下划线"),
    SEX_VALID_FAIL("USER-20003", "性别参数不正确"),
    INTRODUCTION_VALID_FAIL("USER-20004", "简介长度不能超过 100 个字符"),
    UPLOAD_AVATAR_FAIL("USER-20005", "上传头像失败"),
    UPLOAD_BACKGROUND_IMG_FAIL("USER-20006", "上传背景图失败"),
    ;

    private final String errorCode;
    private final String errorMessage;
}
