package com.lhj.xiaohuangshuauth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 瀵嗙爜鍔犲瘑
 */
@Component
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        //BCrypt 鏄竴绉嶅畨鍏ㄤ笖閫傚悎瀵嗙爜瀛樺偍鐨勫搱甯岀畻娉曪紝浠栧湪杩涜鍝堝笇鏃朵細鑷姩鍔犲叆鈥滅洂鈥濓紝澧炲姞瀵嗙爜瀹夊叏鎬с€?
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("123456qweasd"));
    }
}
