package com.lhj.xiaohuangshuauth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.base.Preconditions;
import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.user.dto.resp.FindUserByPhoneRspDTO;
import com.lhj.xiaohuangshuauth.constant.RedisKeyConstants;
import com.lhj.xiaohuangshuauth.enums.LoginTypeEnum;
import com.lhj.xiaohuangshuauth.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UpdatePasswordReqVO;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UserLoginReqVO;
import com.lhj.xiaohuangshuauth.rpc.UserRpcService;
import com.lhj.xiaohuangshuauth.service.AuthService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class UserServiceImpl implements AuthService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "taskExecutor")
    private Executor taskExecutor;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private UserRpcService userRpcService;

    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(userLoginReqVO.getType());
        if (Objects.isNull(loginTypeEnum)) {
            throw new BizException(ResponseCodeEnum.LOGIN_TYPE_ERROR);
        }

        Long userId = switch (loginTypeEnum) {
            case VERIFICATION_CODE -> loginByVerificationCode(userLoginReqVO);
            case PASSWORD -> loginByPassword(userLoginReqVO);
        };

        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Response.success(tokenInfo.tokenValue);
    }

    @Override
    public Response<?> logout() {
        Long userId = LoginUserContextHolder.getUserId();
        log.info("==> user logout, userId: {}", userId);

        taskExecutor.execute(() -> {
            Long asyncUserId = LoginUserContextHolder.getUserId();
            log.info("==> async thread userId: {}", asyncUserId);
        });

        StpUtil.logout(userId);
        return Response.success();
    }

    @Override
    public Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO) {
        // 新密码
        String newPassword = updatePasswordReqVO.getNewPassword();
        // 密码加密
        String encodePassword = passwordEncoder.encode(newPassword);

        // RPC: 调用用户服务，更新密码
        userRpcService.updatePassword(encodePassword);

        return Response.success();
    }

    private Long loginByVerificationCode(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        String verificationCode = userLoginReqVO.getCode();
        Preconditions.checkArgument(StringUtils.isNotBlank(verificationCode), "验证码不能为空");

        String key = RedisKeyConstants.buildVerificationCodeKey(phone);
        String sentCode = (String) redisTemplate.opsForValue().get(key);
        if (!StringUtils.equals(verificationCode, sentCode)) {
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
        }

        Long userId = userRpcService.registerUser(phone);
        if (Objects.isNull(userId)) {
            throw new BizException(ResponseCodeEnum.LOGIN_FAIL);
        }
        return userId;
    }

    private Long loginByPassword(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        String password = userLoginReqVO.getPassword();
        Preconditions.checkArgument(StringUtils.isNotBlank(password), "密码不能为空");

        FindUserByPhoneRspDTO user = userRpcService.findUserByPhone(phone);
        if (Objects.isNull(user)) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException(ResponseCodeEnum.PHONE_OR_PASSWORD_ERROR);
        }

        return user.getId();
    }
}
