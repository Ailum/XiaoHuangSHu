package com.lhj.xiaohuangshu.kv.dto.rsp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindNoteContentRspDTO {
    /**
     * 笔记 ID
     */
    private UUID uuid;

    /**
     * 笔记内容
     */
    private String content;

}
