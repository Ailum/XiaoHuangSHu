package com.lhj.xiaohuangshu.user.dto.req;

import com.lhj.framework.common.validator.PhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserByPhoneReqDTO {

    /**
     * Phone number.
     */
    @NotBlank(message = "手机号不能为空")
    @PhoneNumber
    private String phone;
}
