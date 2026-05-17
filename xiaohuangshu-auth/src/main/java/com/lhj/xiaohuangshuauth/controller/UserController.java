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
@RequestMapping
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    @ApiOperationLog(description = "鐢ㄦ埛鐧诲綍/娉ㄥ唽")
    public Response<String> loginAndRegister(@Validated @RequestBody UserLoginReqVO userLoginReqVO) {
        return userService.loginAndRegister(userLoginReqVO);
    }

    @PostMapping("/logout")
    @ApiOperationLog(description = "璐﹀彿鐧诲嚭")
    public Response<?> logout() {
        //todo 璐﹀彿閫€鍑虹櫥褰曢€昏緫寰呭疄鐜?
      return userService.logout();
    }

    @PostMapping("/password/update")
    @ApiOperationLog(description = "淇敼瀵嗙爜")
    public Response<?> updatePassword(@Validated @RequestBody UpdatePasswordReqVO updatePasswordReqVO) {
        return userService.updatePassword(updatePasswordReqVO);
    }

}
