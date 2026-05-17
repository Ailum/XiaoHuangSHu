package com.lhj.xiaohuangshu.user.biz.model.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserInfoReqVO {


    /**
     * 澶村儚
     */
    private MultipartFile avatar;

    /**
     * 鏄电О
     */
    private String nickname;

    /**
     * 灏忓搱涔?ID
     */
    private String xiaohuangshuId;

    /**
     * 鎬у埆
     */
    private Integer sex;

    /**
     * 鐢熸棩
     */
    private LocalDate birthday;

    /**
     * 涓汉浠嬬粛
     */
    private String introduction;

    /**
     * 鑳屾櫙鍥?
     */
    private MultipartFile backgroundImg;

}
