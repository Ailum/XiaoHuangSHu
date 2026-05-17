package com.lhj.xiaohuangshuauth.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum LoginTypeEnum {
    //楠岃瘉鐮?
    VERIFICATION_CODE(1),
    //瀵嗙爜
    PASSWORD(2);

    private final Integer value;

    public static LoginTypeEnum valueOf(Integer code) {
        for(LoginTypeEnum loginTypeEnum : LoginTypeEnum.values()){
            if(Objects.equals(loginTypeEnum.getValue(), code)){
                return loginTypeEnum;
            }
        }

        return null;
    }

}
