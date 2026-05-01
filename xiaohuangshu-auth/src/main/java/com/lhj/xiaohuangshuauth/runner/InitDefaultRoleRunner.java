package com.lhj.xiaohuangshuauth.runner;

import com.lhj.xiaohuangshuauth.constant.RoleConstants;
import com.lhj.xiaohuangshuauth.domain.dataobject.RoleDO;
import com.lhj.xiaohuangshuauth.domain.mapper.RoleDOMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class InitDefaultRoleRunner implements ApplicationRunner {

    @Resource
    private RoleDOMapper roleDOMapper;

    @Override
    public void run(ApplicationArguments args) {
        RoleDO roleDO = roleDOMapper.selectByPrimaryKey(RoleConstants.COMMON_USER_ROLE_ID);
        if (roleDO != null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        RoleDO defaultRole = RoleDO.builder()
                .id(RoleConstants.COMMON_USER_ROLE_ID)
                .roleName("普通用户")
                .roleKey("common_user")
                .status(0)
                .sort(1)
                .remark("系统默认普通用户角色")
                .createTime(now)
                .updateTime(now)
                .isDeleted(false)
                .build();

        roleDOMapper.insertSelective(defaultRole);
        log.info("==> init default role success, roleId: {}", RoleConstants.COMMON_USER_ROLE_ID);
    }
}
