package com.lhj.xiaohuangshuauth.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshuauth.constant.RedisKeyConstants;
import com.lhj.xiaohuangshuauth.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshuauth.model.vo.veriticationcode.SendVerificationCodeReqVO;
import com.lhj.xiaohuangshuauth.service.VerificationCodeService;
import com.lhj.xiaohuangshuauth.sms.AliyunAccessKeyProperties;
import com.lhj.xiaohuangshuauth.sms.AliyunSmsHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
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


    //发送短信验证码


    @Override
    public Response<?> send(SendVerificationCodeReqVO sendVerificationCodeReqVO) {
        //手机号
        String phone = sendVerificationCodeReqVO.getPhone();
        //构建验证码 redis key
        String key = RedisKeyConstants.buildVerificationCodeKey(phone);
        //判断是否已经发送验证码
        boolean ifsent = redisTemplate.hasKey(key);
        if (ifsent) {
            //若之前发送的验证码未过期，则提示发送频繁
            throw new BizException(ResponseCodeEnum.VERIFICATION_CODE_SEND_FREQUENTLY);
        }
        //生成6位数验证码
        String verificationCode = RandomUtil.randomNumbers(6);
        log.info("==> 手机号:{},已生成验证码：【{}】", phone, verificationCode);
        //调用第三方短信发送服务
        threadPoolTaskExecutor.submit(() -> {
            String signName = "速通互联验证码";
            String templateCode = "100001";
            String templateParam = String.format("{\"code\":\"%s\",\"min\":\"3\"}", verificationCode);
            aliyunSmsHelper.sendMessage(signName, templateCode, phone, templateParam);

        });
        //存储验证码到redis,并设置过期时间为3分钟
        redisTemplate.opsForValue().set(key, verificationCode, 3, TimeUnit.MINUTES);

        return Response.success();
    }
}
