package com.lhj.xiaohuangshu.user.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserByIdRspDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickName;


    /**
     * 头像
     */
    private String avatar;
}
