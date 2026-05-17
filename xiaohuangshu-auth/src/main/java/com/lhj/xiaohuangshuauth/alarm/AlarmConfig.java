package com.lhj.xiaohuangshuauth.alarm;

import com.lhj.xiaohuangshuauth.alarm.impl.MailAlarmHelper;
import com.lhj.xiaohuangshuauth.alarm.impl.SmsAlarmHelper;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
public class AlarmConfig {

    @Value("${alarm.type}")
    private String alarmType;

    @Bean
    @RefreshScope
    public AlarmInterface alarmHelper() {
        // 鏍规嵁閰嶇疆鏂囦欢涓殑鍛婅绫诲瀷锛屽垵濮嬪寲閫夋嫨涓嶅悓鐨勫憡璀﹀疄鐜扮被
        if (StringUtils.equals("sms", alarmType)) {
            return new SmsAlarmHelper();
        } else if (StringUtils.equals("mail", alarmType)) {
            return new MailAlarmHelper();
        } else {
            throw new IllegalArgumentException("閿欒鐨勫憡璀︾被鍨?..");
        }
    }
}
