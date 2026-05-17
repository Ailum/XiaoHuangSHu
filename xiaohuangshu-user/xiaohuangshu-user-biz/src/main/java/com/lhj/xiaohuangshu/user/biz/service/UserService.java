package com.lhj.xiaohuangshu.user.biz.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.user.biz.model.vo.UpdateUserInfoReqVO;

public interface UserService {


    /**
     * 更新用户信息
     *
     * @param updateUserInfoReqVO
     * @return
     */
    Response<?> updateUserInfo(UpdateUserInfoReqVO updateUserInfoReqVO);
}
