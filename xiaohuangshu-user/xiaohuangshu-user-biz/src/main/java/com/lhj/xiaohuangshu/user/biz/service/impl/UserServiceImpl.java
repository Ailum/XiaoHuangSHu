package com.lhj.xiaohuangshu.user.biz.service.impl;


import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.framework.common.util.ParamUtils;
import com.lhj.xiaohuangshu.oss.api.FileFeignApi;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.UserDO;
import com.lhj.xiaohuangshu.user.biz.domain.mapper.UserDOMapper;
import com.lhj.xiaohuangshu.user.biz.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshu.user.biz.enums.SexEnum;
import com.lhj.xiaohuangshu.user.biz.model.vo.UpdateUserInfoReqVO;
import com.lhj.xiaohuangshu.user.biz.rpc.OssRpcService;
import com.lhj.xiaohuangshu.user.biz.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private FileFeignApi fileFeignApi;

    @Resource
    private OssRpcService ossRpcService;

    /**
     * 更新用户信息
     *
     * @param updateUserInfoReqVO
     * @return
     */
    @Override
    public Response<?> updateUserInfo(UpdateUserInfoReqVO updateUserInfoReqVO) {
        UserDO userDO = new UserDO();
        // 设置当前需要更新的用户 ID
        userDO.setId(LoginUserContextHolder.getUserId());
        // 标识位：是否需要更新
        boolean needUpdate = false;

        // 头像
        MultipartFile avatarFile = updateUserInfoReqVO.getAvatar();

        if (Objects.nonNull(avatarFile)) {
            String avatar = ossRpcService.uploadFile(avatarFile);
            log.info("==> 调用 oss 服务成功，上传头像，url：{}", avatar);

            // 若上传头像失败，则抛出业务异常
            if (StringUtils.isBlank(avatar)) {
                throw new BizException(ResponseCodeEnum.UPLOAD_AVATAR_FAIL);
            }

            userDO.setAvatar(avatar);
            needUpdate = true;
        }

        // 昵称
        String nickname = updateUserInfoReqVO.getNickname();
        if (StringUtils.isNotBlank(nickname)) {
            Preconditions.checkArgument(ParamUtils.checkNickname(nickname), ResponseCodeEnum.NICK_NAME_VALID_FAIL.getErrorMessage());
            userDO.setNickname(nickname);
            needUpdate = true;
        }

        // 小皇书号
        String xiaohuangshuId = updateUserInfoReqVO.getXiaohuangshuId();
        if (StringUtils.isNotBlank(xiaohuangshuId)) {
            Preconditions.checkArgument(ParamUtils.checkXiaohashuId(xiaohuangshuId), ResponseCodeEnum.XIAOHASHU_ID_VALID_FAIL.getErrorMessage());
            userDO.setXiaohuangshuId(xiaohuangshuId);
            needUpdate = true;
        }

        // 性别
        Integer sex = updateUserInfoReqVO.getSex();
        if (Objects.nonNull(sex)) {
            Preconditions.checkArgument(SexEnum.isValid(sex), ResponseCodeEnum.SEX_VALID_FAIL.getErrorMessage());
            userDO.setSex(sex.byteValue());
            needUpdate = true;
        }

        // 生日
        LocalDate birthday = updateUserInfoReqVO.getBirthday();
        if (Objects.nonNull(birthday)) {
            userDO.setBirthday(Date.from(birthday.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            needUpdate = true;
        }

        // 个人简介
        String introduction = updateUserInfoReqVO.getIntroduction();
        if (StringUtils.isNotBlank(introduction)) {
            Preconditions.checkArgument(ParamUtils.checkLength(introduction, 100), ResponseCodeEnum.INTRODUCTION_VALID_FAIL.getErrorMessage());
            userDO.setIntroduction(introduction);
            needUpdate = true;
        }

        // 背景图
        MultipartFile backgroundImgFile = updateUserInfoReqVO.getBackgroundImg();
        if (Objects.nonNull(backgroundImgFile)) {
            String backgroundImg = ossRpcService.uploadFile(backgroundImgFile);
            log.info("==> 调用 oss 服务成功，上传背景图，url：{}", backgroundImg);

            // 若上传背景图失败，则抛出业务异常
            if (StringUtils.isBlank(backgroundImg)) {
                throw new BizException(ResponseCodeEnum.UPLOAD_BACKGROUND_IMG_FAIL);
            }

            userDO.setBackgroundImg(backgroundImg);
            needUpdate = true;
        }

        if (needUpdate) {
            // 更新用户信息
            userDO.setUpdateTime(new Date());
            userDOMapper.updateByPrimaryKeySelective(userDO);
        }
        return Response.success();
    }
}
