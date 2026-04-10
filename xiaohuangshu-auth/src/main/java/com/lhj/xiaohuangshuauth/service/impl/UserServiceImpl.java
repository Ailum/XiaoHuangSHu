package com.lhj.xiaohuangshuauth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.google.common.collect.Lists;
import com.lhj.framework.common.enums.DeletedEnum;
import com.lhj.framework.common.enums.StatusEnum;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.framework.common.util.JsonUtils;
import com.lhj.xiaohuangshuauth.constant.RedisKeyConstants;
import com.lhj.xiaohuangshuauth.constant.RoleConstants;
import com.lhj.xiaohuangshuauth.domain.dataobject.UserDO;
import com.lhj.xiaohuangshuauth.domain.dataobject.UserRoleDO;
import com.lhj.xiaohuangshuauth.domain.mapper.UserDOMapper;
import com.lhj.xiaohuangshuauth.domain.mapper.UserRoleDOMapper;
import com.lhj.xiaohuangshuauth.enums.LoginTypeEnum;
import com.lhj.xiaohuangshuauth.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.user.UserLoginReqVO;
import com.lhj.xiaohuangshuauth.service.UserService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRoleDOMapper userRoleDOMapper;

    @Override
    public Response<String> loginAndRegister(UserLoginReqVO userLoginReqVO) {
        String phone = userLoginReqVO.getPhone();
        Integer type = userLoginReqVO.getType();

        LoginTypeEnum loginTypeEnum = LoginTypeEnum.valueOf(type);

        if (loginTypeEnum == null) {
            return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "登录类型不正确");
        }

        Long userId = null;


        // 判断登录类型
        switch (loginTypeEnum) {
            case VERIFICATION_CODE:// 验证码登录
                String verificationCode = userLoginReqVO.getCode();

                // 校验入参验证码是否为空
                if (StringUtils.isEmpty(verificationCode)) {
                    return Response.fail(ResponseCodeEnum.PARAM_NOT_VALID.getErrorCode(), "验证码不能为空");
                }
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

                // 通过手机号查询记录
                if (Objects.isNull(userDO)) {
                    userId = registerUser(phone);
                } else {
                    userId = userDO.getId();
                }
                break;
            case PASSWORD:
                // TODO: password login.
                break;
            default:
                break;
        }

        // SaToken 登录用户，入参为用户ID
        StpUtil.login(userId);

        //获取Token令牌
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();

        //返回Token令牌

        return Response.success(tokenInfo.toString());
    }

    @Transactional(rollbackFor = Exception.class)
    public Long registerUser(String phone) {
        // 通过手机号查询记录
        Long xiaohuangshuId = redisTemplate.opsForValue().increment(RedisKeyConstants.XIAOHUANGSHU_ID_GENERATOR_KEY);

        UserDO userDO = UserDO.builder()
                .phone(phone)
                .xiaohuangshuId(String.valueOf(xiaohuangshuId)) // 自动生成小红书号 ID
                .nickname("zmjjkk" + xiaohuangshuId) // 自动生成昵称, 如：小红薯10000
                .status(StatusEnum.ENABLE.getValue()) // 状态为启用
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(DeletedEnum.NO.getValue()) // 逻辑删除
                .build();
        //添加入库
        userDOMapper.insertSelective(userDO);

        // 获取刚刚添加入库的用户 ID
        Long userId = userDO.getId();

        // 给该用户分配一个默认角色
        UserRoleDO userRoleDO = UserRoleDO.builder()
                .userId(userId)
                .roleId(RoleConstants.COMMON_USER_ROLE_ID)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(DeletedEnum.NO.getValue())
                .build();
        userRoleDOMapper.insert(userRoleDO);


        // 将该用户的角色 ID 存入 Redis 中
        List<Long> roles = Lists.newArrayList();
        roles.add(RoleConstants.COMMON_USER_ROLE_ID);
        String userRolesKey = RedisKeyConstants.buildUserRoleKey(phone);
        redisTemplate.opsForValue().set(userRolesKey, JsonUtils.toJsonString(roles));

        return userId;
    }
}
