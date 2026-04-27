package com.lhj.xiaohuangshuauth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.base.Preconditions;
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
        //新密码
        String newPassword = updatePasswordReqVO.getNewPassword();
        //密码加密
        String encodePassword = passwordEncoder.encode(newPassword);

        //获取当前请求对应的用户ID
        Long userId = LoginUserContextHolder.getUserId();

        UserDO userDO = UserDO.builder()
                .id(userId)
                .password(encodePassword)
                .updateTime(LocalDateTime.now())
                .build();
        //更新密码
        userDOMapper.updateByPrimaryKeySelective(userDO);

        return Response.success();
    }

    /**
     * 登录与注册
     *
     * @param userLoginReqVO
     * @return
     */

    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(userLoginReqVO.getType());

        // 未知的登录类型
        if (Objects.isNull(loginTypeEnum)) {
            throw new BizException(ResponseCodeEnum.LOGIN_TYPE_ERROR);
        }

        //登录类型错误
        if (loginTypeEnum == null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "登录类型不正确");
        }

        Long userId;
        //判断登录类型
        switch (loginTypeEnum) {
            case VERIFICATION_CODE://验证码登录
                String verificationCode = userLoginReqVO.getCode();

                // 校验入参验证码是否为空
                Preconditions.checkArgument(StringUtils.isNotBlank(verificationCode), "验证码不能为空");

                // 构建验证码 Redis Key
                String key = RedisKeyConstants.buildVerificationCodeKey(phone);
                // 查询存储在 Redis 中该用户的登录验证码
                String sentCode = (String) redisTemplate.opsForValue().get(key);

                // 判断用户提交的验证码，与 Redis 中的验证码是否一致
                if (!StringUtils.equals(verificationCode, sentCode)) {
                    throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_ERROR);
                }

                // 通过手机号查询记录
                UserDO userDO = userDOMapper.selectByPhone(phone);

                log.info("==> 用户是否注册, phone: {}, userDO: {}", phone, JsonUtils.toJsonString(userDO));

                // 判断是否注册
                if (Objects.isNull(userDO)) {
                    // 若此用户还没有注册，系统自动注册该用户
                    userId = registerUser(phone);
                } else {
                    // 已注册，则获取其用户 ID
                    userId = userDO.getId();
                }
                break;
            case PASSWORD://密码登录
                String password = userLoginReqVO.getPassword();
                //根据手机号查询
                UserDO UserDO1 = userDOMapper.selectByPhone(phone);

                // 判断该手机号是否注册
                if(Objects.isNull(UserDO1)){
                    throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
                }

                //拿到密文密码
                String encodePassword = UserDO1.getPassword();

                //匹配密码 是否一致
                boolean isPasswordCorrect = passwordEncoder.matches(password, encodePassword);

                //如果不正确，则抛出业务异常，提示用户名或者密码不正确
                if(!isPasswordCorrect){
                    throw new BizException(ResponseCodeEnum.PHONE_OR_PASSWORD_ERROR);
                }
                userId = UserDO1.getId();
                break;
            default:
                throw new BizException(ResponseCodeEnum.LOGIN_TYPE_ERROR);

        }
       //返回Token令牌
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
