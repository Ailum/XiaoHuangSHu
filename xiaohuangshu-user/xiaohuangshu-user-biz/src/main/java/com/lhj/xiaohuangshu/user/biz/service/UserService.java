package com.lhj.xiaohuangshu.user.biz.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.lhj.xiaohuangshu.user.dto.req.FindUserByPhoneReqDTO;
import com.lhj.xiaohuangshu.user.dto.req.RegisterUserReqDTO;
import com.lhj.xiaohuangshu.user.dto.req.UpdateUserPasswordReqDTO;
import com.lhj.xiaohuangshu.user.dto.resp.FindUserByIdRspDTO;
import com.lhj.xiaohuangshu.user.dto.req.FindUserByIdReqDTO;
import com.lhj.xiaohuangshu.user.dto.resp.FindUserByPhoneRspDTO;

public interface UserService {

    /**
     * 更新当前登录用户资料。
     *
     * @param updateUserInfoReqVO 用户资料更新参数
     * @return 更新结果
     */
    Response<?> updateUserInfo(UpdateUserInfoReqVO updateUserInfoReqVO);

    /**
     * 注册用户，并返回新用户主键 ID。
     *
     * @param registerUserReqDTO 用户注册参数
     * @return 新用户 ID
     */
    Response<Long> register(RegisterUserReqDTO registerUserReqDTO);

    /**
     * 根据手机号查询用户信息。
     *
     * @param findUserByPhoneReqDTO 查询参数
     * @return 用户信息
     */
    Response<FindUserByPhoneRspDTO> findByPhone(FindUserByPhoneReqDTO findUserByPhoneReqDTO);

    /**
     * 更新当前登录用户密码。
     *
     * @param updateUserPasswordReqDTO 密码更新参数
     * @return 更新结果
     */
    Response<?> updatePassword(UpdateUserPasswordReqDTO updateUserPasswordReqDTO);

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param findUserByIdReqDTO
     * @return
     */
    Response<FindUserByIdRspDTO> findById(FindUserByIdReqDTO findUserByIdReqDTO);
}
