package com.lhj.xiaohuangshu.kv.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindnoteContentReqDTO {

    @NotBlank(message = "笔记 UUID 不能为空")
    private String uuid;
}
