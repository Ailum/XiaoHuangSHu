package com.lhj.xiaohuangshu.user.biz.controller;

import com.lhj.framework.biz.operationlog.aspect.ApiOperationLog;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.lhj.xiaohuangshu.user.biz.service.UserService;
import com.lhj.xiaohuangshu.user.dto.req.FindUserByIdReqDTO;
import com.lhj.xiaohuangshu.user.dto.req.FindUserByPhoneReqDTO;
import com.lhj.xiaohuangshu.user.dto.req.RegisterUserReqDTO;
import com.lhj.xiaohuangshu.user.dto.req.UpdateUserPasswordReqDTO;
import com.lhj.xiaohuangshu.user.dto.resp.FindUserByIdRspDTO;
import com.lhj.xiaohuangshu.user.dto.resp.FindUserByPhoneRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperationLog(description = "更新用户资料")
    public Response<?> updateUserInfo(@Validated UpdateUserInfoReqVO updateUserInfoReqVO) {
        return userService.updateUserInfo(updateUserInfoReqVO);
    }

    @PostMapping("/register")
    @ApiOperationLog(description = "用户注册")
    public Response<Long> register(@Validated @RequestBody RegisterUserReqDTO registerUserReqDTO) {
        return userService.register(registerUserReqDTO);
    }

    @PostMapping("/findByPhone")
    @ApiOperationLog(description = "手机号查询用户信息")
    public Response<FindUserByPhoneRspDTO> findByPhone(@Validated @RequestBody FindUserByPhoneReqDTO findUserByPhoneReqDTO) {
        return userService.findByPhone(findUserByPhoneReqDTO);
    }

    @PostMapping("/password/update")
    @ApiOperationLog(description = "修改密码")
    public Response<?> updatePassword(@Validated @RequestBody UpdateUserPasswordReqDTO updateUserPasswordReqDTO) {
        return userService.updatePassword(updateUserPasswordReqDTO);
    }

    @PostMapping("/findById")
    @ApiOperationLog(description = "查询用户信息")
    public Response<FindUserByIdRspDTO> findById(@Validated @RequestBody FindUserByIdReqDTO findUserByIdReqDTO) {
        return userService.findById(findUserByIdReqDTO);
    }
}
