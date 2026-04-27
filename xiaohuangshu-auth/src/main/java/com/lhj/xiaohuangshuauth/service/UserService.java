package com.lhj.xiaohuangshuauth.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UpdatePasswordReqVO;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UserLoginReqVO;

public interface UserService {

    Response<String> loginAndRegister(UserLoginReqVO userLoginVO);

    Response<?> logout();

    /**
     * 修改密码
     * @param updatePasswordReqVO
     * @return
     */

    Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO);
}
