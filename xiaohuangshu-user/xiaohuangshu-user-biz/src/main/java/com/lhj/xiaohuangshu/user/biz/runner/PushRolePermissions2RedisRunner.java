package com.lhj.xiaohuangshu.user.biz.runner;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lhj.framework.common.util.JsonUtils;
import com.lhj.xiaohuangshu.user.biz.constant.RedisKeyConstants;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.PermissionDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.RoleDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.RolePermissionDO;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.PermissionDOMapper;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.RoleDOMapper;
import com.lhj.xiaohuangshu.user.biz.domain.dataobject.mapper.RolePermissionDOMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PushRolePermissions2RedisRunner implements ApplicationRunner {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private RoleDOMapper roleDOMapper;
    @Resource
    private PermissionDOMapper permissionDOMapper;
    @Resource
    private RolePermissionDOMapper rolePermissionDOMapper;

    // 权限同步标记 Key
    private static final String PUSH_PERMISSION_FLAG = "push.permission.flag";

    @Override
    public void run(ApplicationArguments args) {
        log.info("==> 服务启动，开始同步角色权限数据到 Redis 中...");

        try {
            // 原子操作：仅当同步标记不存在时设置标记，并保留 1 天
            boolean canPushed = redisTemplate.opsForValue()
                    .setIfAbsent(PUSH_PERMISSION_FLAG, "1", 1, TimeUnit.DAYS);

            // 已经同步过时，不再重复同步
            if (!canPushed) {
                log.warn("==> 角色权限数据已经同步至 Redis 中，不再重复同步...");
                return;
            }

            // 查询所有角色
            List<RoleDO> roleDOS = roleDOMapper.selectEnabledList();

            if (CollUtil.isNotEmpty(roleDOS)) {
                // 获取所有角色 ID
                List<Long> roleIds = roleDOS.stream().map(RoleDO::getId).toList();

                // 根据角色 ID 批量查询对应的权限
                List<RolePermissionDO> rolePermissionDOS = rolePermissionDOMapper.selectByRoleIds(roleIds);
                // 按角色 ID 分组，每个角色 ID 对应多个权限 ID
                Map<Long, List<Long>> roleIdPermissionIdsMap = rolePermissionDOS.stream().collect(
                        Collectors.groupingBy(RolePermissionDO::getRoleId,
                                Collectors.mapping(RolePermissionDO::getPermissionId, Collectors.toList()))
                );

                // 查询 APP 端所有已启用的权限
                List<PermissionDO> permissionDOS = permissionDOMapper.selectAppEnabledList();
                // 权限 ID 与权限 DO 的映射
                Map<Long, PermissionDO> permissionIdDOMap = permissionDOS.stream().collect(
                        Collectors.toMap(PermissionDO::getId, permissionDO -> permissionDO)
                );

                // 组织角色与权限的对应关系
                Map<String, List<String>> roleIdPermissionDOMap = Maps.newHashMap();

                // 遍历所有角色
                roleDOS.forEach(roleDO -> {
                    Long roleId = roleDO.getId();
                    String roleKey = roleDO.getRoleKey();
                    // 当前角色对应的权限 ID 集合
                    List<Long> permissionIds = roleIdPermissionIdsMap.get(roleId);
                    if (CollUtil.isNotEmpty(permissionIds)) {
                        List<String> perDOS = Lists.newArrayList();
                        permissionIds.forEach(permissionId -> {
                            // 根据权限 ID 获取具体的权限对象
                            PermissionDO permissionDO = permissionIdDOMap.get(permissionId);
                            if (Objects.nonNull(permissionDO)) {
                                perDOS.add(permissionDO.getPermissionKey());
                            }
                        });
                        roleIdPermissionDOMap.put(roleKey, perDOS);
                    }
                });

                // 同步到 Redis，供网关鉴权时查询
                roleIdPermissionDOMap.forEach((roleKey, permissions) -> {
                    String key = RedisKeyConstants.buildRolePermissionsKey(roleKey);
                    redisTemplate.opsForValue().set(key, JsonUtils.toJsonString(permissions));
                });
            }

            log.info("==> 服务启动，成功同步角色权限数据到 Redis 中...");
        } catch (Exception e) {
            log.error("==> 同步角色权限数据到 Redis 中失败", e);
        }
    }
}
