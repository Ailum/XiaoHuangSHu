package com.lhj.xiaohuangshu.note.biz.constant;

public class RedisKeyConstants {

    /**
     * 笔记详情 KEY 前缀
     */
    public static final String NOTE_DETAIL_KEY= "note:detail:";

    /**
     * 构建完整的笔记详情 KEY
     */
    public static final String buildNoteDetailKey(Long noteId){
        return NOTE_DETAIL_KEY + noteId;
    }
}
