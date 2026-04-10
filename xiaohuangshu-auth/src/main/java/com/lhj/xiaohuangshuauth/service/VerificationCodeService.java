package com.lhj.xiaohuangshuauth.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.SendVerificationCodeReqVO;

public interface VerificationCodeService {
    //发送短信验证码
    Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO);

}
