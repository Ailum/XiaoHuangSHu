package com.lhj.xiaohuangshu.user.biz.service.impl;

import com.google.common.base.Preconditions;
import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.enums.DeletedEnum;
import com.lhj.framework.common.enums.StatusEnum;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.framework.common.util.JsonUtils;
import com.lhj.framework.common.util.ParamUtils;
import com.lhj.xiaohuangshu.user.biz.constant.RedisKeyConstants;
import com.lhj.xiaohuangshu.user.biz.constant.RoleConstants;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.RoleDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.UserDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.UserRoleDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.RoleDOMapper;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.UserDOMapper;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.UserRoleDOMapper;
import com.lhj.xiaohuangshu.user.biz.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshu.user.biz.enums.SexEnum;
import com.lhj.xiaohuangshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.lhj.xiaohuangshu.user.biz.rpc.OssRpcService;
import com.lhj.xiaohuangshu.user.biz.service.UserService;
import com.lhj.xiaohuangshu.user.dto.req.FindUserByPhoneReqDTO;
import com.lhj.xiaohuangshu.user.dto.req.RegisterUserReqDTO;
import com.lhj.xiaohuangshu.user.dto.req.UpdateUserPasswordReqDTO;
import com.lhj.xiaohuangshu.user.dto.resp.FindUserByPhoneRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private OssRpcService ossRpcService;

    @Resource
    private UserRoleDOMapper userRoleDOMapper;

    @Resource
    private RoleDOMapper roleDOMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Response<?> updateUserInfo(UpdateUserInfoReqVO updateUserInfoReqVO) {
        UserDO userDO = new UserDO();
        userDO.setId(LoginUserContextHolder.getUserId());

        boolean needUpdate = false;

        MultipartFile avatarFile = updateUserInfoReqVO.getAvatar();
        if (Objects.nonNull(avatarFile)) {
            String avatar = ossRpcService.uploadFile(avatarFile);
            log.info("==> upload avatar success, url: {}", avatar);
            if (!StringUtils.hasText(avatar)) {
                throw new BizException(ResponseCodeEnum.UPLOAD_AVATAR_FAIL);
            }
            userDO.setAvatar(avatar);
            needUpdate = true;
        }

        String nickname = updateUserInfoReqVO.getNickname();
        if (StringUtils.hasText(nickname)) {
            Preconditions.checkArgument(
                    ParamUtils.checkNickname(nickname),
                    ResponseCodeEnum.NICK_NAME_VALID_FAIL.getErrorMessage()
            );
            userDO.setNickname(nickname);
            needUpdate = true;
        }

        String xiaohuangshuId = updateUserInfoReqVO.getXiaohuangshuId();
        if (StringUtils.hasText(xiaohuangshuId)) {
            Preconditions.checkArgument(
                    ParamUtils.checkXiaohashuId(xiaohuangshuId),
                    ResponseCodeEnum.XIAOHASHU_ID_VALID_FAIL.getErrorMessage()
            );
            userDO.setXiaohuangshuId(xiaohuangshuId);
            needUpdate = true;
        }

        Integer sex = updateUserInfoReqVO.getSex();
        if (Objects.nonNull(sex)) {
            Preconditions.checkArgument(SexEnum.isValid(sex), ResponseCodeEnum.SEX_VALID_FAIL.getErrorMessage());
            userDO.setSex(sex.byteValue());
            needUpdate = true;
        }

        LocalDate birthday = updateUserInfoReqVO.getBirthday();
        if (Objects.nonNull(birthday)) {
            userDO.setBirthday(Date.from(birthday.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            needUpdate = true;
        }

        String introduction = updateUserInfoReqVO.getIntroduction();
        if (StringUtils.hasText(introduction)) {
            Preconditions.checkArgument(
                    ParamUtils.checkLength(introduction, 100),
                    ResponseCodeEnum.INTRODUCTION_VALID_FAIL.getErrorMessage()
            );
            userDO.setIntroduction(introduction);
            needUpdate = true;
        }

        MultipartFile backgroundImgFile = updateUserInfoReqVO.getBackgroundImg();
        if (Objects.nonNull(backgroundImgFile)) {
            String backgroundImg = ossRpcService.uploadFile(backgroundImgFile);
            log.info("==> upload background image success, url: {}", backgroundImg);
            if (!StringUtils.hasText(backgroundImg)) {
                throw new BizException(ResponseCodeEnum.UPLOAD_BACKGROUND_IMG_FAIL);
            }
            userDO.setBackgroundImg(backgroundImg);
            needUpdate = true;
        }

        if (needUpdate) {
            userDO.setUpdateTime(LocalDateTime.now());
            userDOMapper.updateByPrimaryKeySelective(userDO);
        }

        return Response.success();
    }

    @Override
    public Response<Long> register(RegisterUserReqDTO registerUserReqDTO) {
        String phone = registerUserReqDTO.getPhone();
        UserDO existsUser = userDOMapper.selectByPhone(phone);
        if (existsUser != null) {
            return Response.success(existsUser.getId());
        }

        Long xiaohuangshuId = redisTemplate.opsForValue().increment(RedisKeyConstants.XIAOHUANGSHU_ID_GENERATOR_KEY);
        LocalDateTime now = LocalDateTime.now();

        UserDO userDO = UserDO.builder()
                .phone(phone)
                .xiaohuangshuId(String.valueOf(xiaohuangshuId))
                .nickname("小红薯" + xiaohuangshuId)
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
        log.info("==> register user success, userId: {}, phone: {}", userId, phone);
        return Response.success(userId);
    }

    private void createDefaultRoleRelation(Long userId, LocalDateTime now) {
        RoleDO roleDO = roleDOMapper.selectByPrimaryKey(RoleConstants.COMMON_USER_ROLE_ID);
        if (roleDO == null) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "默认角色不存在，请先初始化 t_role 表");
        }

        UserRoleDO existsRelation = userRoleDOMapper.selectByUserIdAndRoleId(userId, RoleConstants.COMMON_USER_ROLE_ID);
        if (existsRelation != null) {
            return;
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

    @Override
    public Response<FindUserByPhoneRspDTO> findByPhone(FindUserByPhoneReqDTO findUserByPhoneReqDTO) {
        String phone = findUserByPhoneReqDTO.getPhone();
        UserDO userDO = userDOMapper.selectByPhone(phone);

        if (Objects.isNull(userDO)) {
            throw new BizException(ResponseCodeEnum.USER_NOT_FOUND);
        }

        FindUserByPhoneRspDTO findUserByPhoneRspDTO = FindUserByPhoneRspDTO.builder()
                .id(userDO.getId())
                .password(userDO.getPassword())
                .build();

        return Response.success(findUserByPhoneRspDTO);
    }

    @Override
    public Response<?> updatePassword(UpdateUserPasswordReqDTO updateUserPasswordReqDTO) {
        Long userId = LoginUserContextHolder.getUserId();
        if (Objects.isNull(userId)) {
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR.getErrorCode(), "未获取到当前用户");
        }

        UserDO userDO = UserDO.builder()
                .id(userId)
                .password(updateUserPasswordReqDTO.getEncodePassword())
                .updateTime(LocalDateTime.now())
                .build();
        userDOMapper.updateByPrimaryKeySelective(userDO);
        return Response.success();
    }
}
