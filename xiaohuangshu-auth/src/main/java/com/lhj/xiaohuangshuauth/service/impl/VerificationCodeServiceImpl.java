package com.lhj.xiaohuangshuauth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshuauth.constant.RedisKeyConstants;
import com.lhj.xiaohuangshuauth.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.SendVerificationCodeReqVO;
import com.lhj.xiaohuangshuauth.service.VerificationCodeService;
import com.lhj.xiaohuangshuauth.sms.AliyunSmsHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private AliyunSmsHelper aliyunSmsHelper;

    @Override
    public Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        String phone = sendVerificationCodeReqVO.getPhone();
        String key = RedisKeyConstants.buildVerificationCodeKey(phone);

        Boolean sent = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(sent)) {
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_SEND_FREQUENTLY);
        }

        String verificationCode = RandomUtil.randomNumbers(6);
        log.info("==> phone: {}, verification code: {}", phone, verificationCode);

        threadPoolTaskExecutor.submit(() -> {
            String signName = "速通互联验证码";
            String templateCode = "100001";
            String templateParam = String.format("{\"code\":\"%s\",\"min\":\"3\"}", verificationCode);
            aliyunSmsHelper.sendMessage(signName, templateCode, phone, templateParam);
        });

        redisTemplate.opsForValue().set(key, verificationCode, 3, TimeUnit.MINUTES);
        return Response.success();
    }
}
