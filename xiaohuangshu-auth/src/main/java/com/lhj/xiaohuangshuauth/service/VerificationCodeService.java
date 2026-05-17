package com.lhj.xiaohuangshuauth.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.SendVerificationCodeReqVO;

public interface VerificationCodeService {
    //鍙戦€佺煭淇￠獙璇佺爜
    Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO);

}
