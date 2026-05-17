package com.lhj.xiaohuangshu.user.biz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum SexEnum {

    WOMAN(0),
    MAN(1);

    private final Integer value;

    public static boolean isValid(Integer value) {
        for(SexEnum loginTypenum:SexEnum.values()){
            if(Objects.equals(value, loginTypenum.value)){
                return true;
            }
        }
        return false;
    }
}
