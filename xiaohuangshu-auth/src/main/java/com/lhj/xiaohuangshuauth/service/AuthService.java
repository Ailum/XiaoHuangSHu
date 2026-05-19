package com.lhj.xiaohuangshuauth.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UpdatePasswordReqVO;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UserLoginReqVO;

public interface AuthService {

    /**
     * 用户登录。验证码登录时，如果手机号未注册，则自动创建用户并返回登录 token。
     *
     * @param userLoginVO 登录请求参数
     * @return Sa-Token 登录 token
     */
    Response<String> loginAndRegister(UserLoginReqVO userLoginVO);

    /**
     * 当前用户退出登录。
     *
     * @return 通用响应
     */
    Response<?> logout();

    /**
     * 当前登录用户修改密码。
     *
     * @param updatePasswordReqVO 密码更新请求参数
     * @return 通用响应
     */
    Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO);
}
