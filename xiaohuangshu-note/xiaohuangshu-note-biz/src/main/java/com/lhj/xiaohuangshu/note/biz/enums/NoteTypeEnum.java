package com.lhj.xiaohuangshu.note.biz.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum NoteTypeEnum {
    IMAGE_TEXT(0,"图文"),
    VIDEO(1,"视频");

    private final Integer code;
    private final String description;

    /**
     * 类型是否有效
     */

    public static boolean isValid(Integer code) {
        for (NoteTypeEnum noteTypeEnum : NoteTypeEnum.values()) {
            if (Objects.equals(code, noteTypeEnum.getCode())) {
                return true;
            }
        }    return false;
    }

    /**
     * 根据类型code获取对应枚举
     */

    public static NoteTypeEnum valueOf(Integer code) {
        for (NoteTypeEnum noteTypeEnum : NoteTypeEnum.values()) {
            if (Objects.equals(code, noteTypeEnum.getCode())) {
                return noteTypeEnum;
            }
        }
        return null;
    }
}
