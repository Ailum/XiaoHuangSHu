package com.lhj.xiaohuangshuauth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.enums.DeletedEnum;
import com.lhj.framework.common.enums.StatusEnum;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.framework.common.util.JsonUtils;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.RoleDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.UserDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.UserRoleDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.RoleDOMapper;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.UserDOMapper;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.UserRoleDOMapper;
import com.lhj.xiaohuangshuauth.constant.RedisKeyConstants;
import com.lhj.xiaohuangshuauth.constant.RoleConstants;
import com.lhj.xiaohuangshuauth.enums.LoginTypeEnum;
import com.lhj.xiaohuangshuauth.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UpdatePasswordReqVO;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UserLoginReqVO;
import com.lhj.xiaohuangshuauth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private UserRoleDOMapper userRoleDOMapper;

    @Resource
    private RoleDOMapper roleDOMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public Response<?> updatePassword(UpdatePasswordReqVO updatePasswordReqVO) {
        Long userId = LoginUserContextHolder.getUserId();
        if (userId == null) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "未获取到当前用户");
        }

        UserDO userDO = UserDO.builder()
                .id(userId)
                .password(passwordEncoder.encode(updatePasswordReqVO.getNewPassword()))
                .updateTime(LocalDateTime.now())
                .build();
        userDOMapper.updateByPrimaryKeySelective(userDO);
        return Response.success();
    }

    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(userLoginReqVO.getType());
        if (loginTypeEnum == null) {
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

        threadPoolTaskExecutor.submit(() -> {
            Long asyncUserId = LoginUserContextHolder.getUserId();
            log.info("==> async thread userId: {}", asyncUserId);
        });

        StpUtil.logout(userId);
        return Response.success();
    }

    private Long loginByVerificationCode(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        String verificationCode = userLoginReqVO.getCode();
        if (StringUtils.isBlank(verificationCode)) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "验证码不能为空");
        }

        String key = RedisKeyConstants.buildVerificationCodeKey(phone);
        String sentCode = (String) redisTemplate.opsForValue().get(key);
        if (!StringUtils.equals(verificationCode, sentCode)) {
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
        }

        UserDO userDO = userDOMapper.selectByPhone(phone);
        log.info("user query by phone, phone: {}, userDO: {}", phone, JsonUtils.toJsonString(userDO));
        if (userDO == null) {
            return registerUser(phone);
        }

        Long userId = userDO.getId();
        ensureDefaultRole(userId);
        return userId;
    }

    private Long loginByPassword(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        String password = userLoginReqVO.getPassword();
        if (StringUtils.isBlank(password)) {
            throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "密码不能为空");
        }

        UserDO userDO = userDOMapper.selectByPhone(phone);
        if (userDO == null) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(password, userDO.getPassword())) {
            throw new BizException(ResponseCodeEnum.PHONE_OR_PASSWORD_ERROR);
        }

        Long userId = userDO.getId();
        ensureDefaultRole(userId);
        return userId;
    }

    private Long registerUser(String phone) {
        return transactionTemplate.execute(status -> {
            try {
                Long xiaohuangshuId = redisTemplate.opsForValue().increment(RedisKeyConstants.XIAOHUANGSHU_ID_GENERATOR_KEY);
                LocalDateTime now = LocalDateTime.now();

                UserDO userDO = UserDO.builder()
                        .phone(phone)
                        .xiaohuangshuId(String.valueOf(xiaohuangshuId))
                        .nickname("zmjjkk" + xiaohuangshuId)
                        .status(StatusEnum.ENABLE.getValue().byteValue())
                        .createTime(now)
                        .updateTime(now)
                        .isDeleted(DeletedEnum.NO.getValue())
                        .build();
                userDOMapper.insertSelective(userDO);

                Long userId = userDO.getId();
                if (userId == null) {
                    throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "用户注册失败，请稍后重试");
                }

                createDefaultRoleRelation(userId, now);
                cacheUserRoles(userId);
                return userId;
            } catch (RuntimeException e) {
                status.setRollbackOnly();
                throw e;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("register user failed, phone: {}", phone, e);
                throw new BizException(ResponseCodeEnum.SYSTEM_ERROR);
            }
        });
    }

    private void ensureDefaultRole(Long userId) {
        UserRoleDO userRoleDO = userRoleDOMapper.selectByUserIdAndRoleId(userId, RoleConstants.COMMON_USER_ROLE_ID);
        if (userRoleDO == null) {
            createDefaultRoleRelation(userId, LocalDateTime.now());
        }
        cacheUserRoles(userId);
    }

    private void createDefaultRoleRelation(Long userId, LocalDateTime now) {
        RoleDO roleDO = roleDOMapper.selectByPrimaryKey(RoleConstants.COMMON_USER_ROLE_ID);
        if (roleDO == null) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "默认角色不存在，请先初始化 t_role 表");
        }

        UserRoleDO userRoleDO = UserRoleDO.builder()
                .userId(userId)
                .roleId(RoleConstants.COMMON_USER_ROLE_ID)
                .createTime(now)
                .updateTime(now)
                .isDeleted(DeletedEnum.NO.getValue())
                .build();
        userRoleDOMapper.insertSelective(userRoleDO);
    }

    private void cacheUserRoles(Long userId) {
        RoleDO roleDO = roleDOMapper.selectByPrimaryKey(RoleConstants.COMMON_USER_ROLE_ID);
        if (roleDO == null) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "默认角色不存在，请先初始化 t_role 表");
        }

        List<String> roles = new ArrayList<>();
        roles.add(roleDO.getRoleKey());
        redisTemplate.opsForValue().set(RedisKeyConstants.buildUserRoleKey(userId), JsonUtils.toJsonString(roles));
    }
}
