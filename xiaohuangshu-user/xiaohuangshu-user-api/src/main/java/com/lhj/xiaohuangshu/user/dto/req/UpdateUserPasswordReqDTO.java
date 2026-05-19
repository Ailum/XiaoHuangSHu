package com.lhj.xiaohuangshu.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserPasswordReqDTO {

    /**
     * 加密后的密码
     */
    @NotBlank(message = "密码不能为空")
    private String encodePassword;
}
