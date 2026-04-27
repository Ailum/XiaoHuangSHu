package com.lhj.xiaohuangshuauth.controller;

import com.lhj.framework.biz.operationlog.aspect.ApiOperationLog;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UpdatePasswordReqVO;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UserLoginReqVO;
import com.lhj.xiaohuangshuauth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    @ApiOperationLog(description = "用户登录/注册")
    public Response<String> loginAndRegister(@Validated @RequestBody UserLoginReqVO userLoginReqVO) {
        return userService.loginAndRegister(userLoginReqVO);
    }

    @PostMapping("/logout")
    @ApiOperationLog(description = "账号登出")
    public Response<?> logout() {
        //todo 账号退出登录逻辑待实现
      return userService.logout();
    }

    @PostMapping("/password/update")
    @ApiOperationLog(description = "修改密码")
    public Response<?> updatePassword(@Validated @RequestBody UpdatePasswordReqVO updatePasswordReqVO) {
        return userService.updatePassword(updatePasswordReqVO);
    }

}
