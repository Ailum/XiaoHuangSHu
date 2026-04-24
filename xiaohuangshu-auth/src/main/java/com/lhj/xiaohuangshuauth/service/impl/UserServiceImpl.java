package com.lhj.xiaohuangshuauth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.collect.Lists;
import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.enums.DeletedEnum;
import com.lhj.framework.common.enums.StatusEnum;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.framework.common.util.JsonUtils;
import com.lhj.xiaohuangshuauth.constant.RedisKeyConstants;
import com.lhj.xiaohuangshuauth.constant.RoleConstants;
import com.lhj.xiaohuangshuauth.domain.dataobject.RoleDO;
import com.lhj.xiaohuangshuauth.domain.dataobject.UserDO;
import com.lhj.xiaohuangshuauth.domain.dataobject.UserRoleDO;
import com.lhj.xiaohuangshuauth.domain.mapper.RoleDOMapper;
import com.lhj.xiaohuangshuauth.domain.mapper.UserDOMapper;
import com.lhj.xiaohuangshuauth.domain.mapper.UserRoleDOMapper;
import com.lhj.xiaohuangshuauth.enums.LoginTypeEnum;
import com.lhj.xiaohuangshuauth.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UserLoginReqVO;
import com.lhj.xiaohuangshuauth.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(userLoginReqVO.getType());
        if (loginTypeEnum == null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "登录类型不正确");
        }

        Long userId;
        switch (loginTypeEnum) {
            case VERIFICATION_CODE:
                userId = loginByVerificationCode(userLoginReqVO);
                break;
            case PASSWORD:
                return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "暂不支持密码登录");
            default:
                return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "登录类型不正确");
        }

        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return Response.success(tokenInfo.tokenValue);
    }

    private Long loginByVerificationCode(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        String verificationCode = userLoginReqVO.getCode();
        if (StringUtils.isBlank(verificationCode)) {
            return failParam("验证码不能为空");
        }

        String key = RedisKeyConstants.buildVerificationCodeKey(phone);
        String sentCode = (String) redisTemplate.opsForValue().get(key);
        if (!StringUtils.equals(verificationCode, sentCode)) {
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
        }

        UserDO userDO = userDOMapper.selectByPhone(phone);
        log.info("user query by phone, phone: {}, userDO: {}", phone, JsonUtils.toJsonString(userDO));

        if (Objects.isNull(userDO)) {
            return registerUser(phone);
        }

        Long userId = userDO.getId();
        ensureDefaultRole(userId, phone);
        return userId;
    }

    /**
     * 退出登录
     *
     * @return
     */
    @Override
    public Response<?> logout() {
        Long userId = LoginUserContextHolder.getUserId();

        log.info("==> 用户退出登录，userId:{}", userId);

        threadPoolTaskExecutor.submit(() -> {
           Long userId2 = LoginUserContextHolder.getUserId();
           log.info("==> 异步线程中获取 userId2:{}", userId2);
        });
        // 退出登录 (指定用户 ID)
        StpUtil.logout(userId);

        return Response.success();
    }

    private Long failParam(String message) {
        throw new BizException(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), message);
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
                        .status(StatusEnum.ENABLE.getValue())
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
                RoleDO roleDO = roleDOMapper.selectByPrimaryKey(RoleConstants.COMMON_USER_ROLE_ID);
                // 将该用户的角色 ID 存入 Redis 中，指定初始容量为 1，这样可以减少在扩容时的性能开销
                List<String> roles = new ArrayList<>();
                roles.add(roleDO.getRoleKey());

                String userRolesKey = RedisKeyConstants.buildUserRoleKey(userId);
                redisTemplate.opsForValue().set(userRolesKey, JsonUtils.toJsonString(roles));
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

    private void ensureDefaultRole(Long userId, String phone) {
        UserRoleDO userRoleDO = userRoleDOMapper.selectByUserIdAndRoleId(userId, RoleConstants.COMMON_USER_ROLE_ID);
        if (userRoleDO == null) {
            createDefaultRoleRelation(userId, LocalDateTime.now());
        }
        RoleDO roleDO = roleDOMapper.selectByPrimaryKey(RoleConstants.COMMON_USER_ROLE_ID);
        if (roleDO == null) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "默认角色不存在，请先初始化 t_role 表");
        }

        List<String> roles = new ArrayList<>();
        roles.add(roleDO.getRoleKey());

        String userRolesKey = RedisKeyConstants.buildUserRoleKey(userId);
        redisTemplate.opsForValue().set(userRolesKey, JsonUtils.toJsonString(roles));
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


}
