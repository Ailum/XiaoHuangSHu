package com.lhj.xiaohuangshuauth.alarm.impl;

import com.lhj.xiaohuangshuauth.alarm.AlarmInterface;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsAlarmHelper implements AlarmInterface {

    /**
     * 鍙戦€佸憡璀︿俊鎭?
     *
     * @param message
     * @return
     */
    @Override
    public boolean send(String message) {
        log.info("==> 銆愮煭淇″憡璀︺€戯細{}", message);

        // 涓氬姟閫昏緫...

        return true;
    }
}
