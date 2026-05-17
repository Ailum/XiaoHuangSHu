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

    //鏉冮檺鍚屾鏍囪Key
    private static final String PUSH_PERMISSION_FLAG = "push.permission.flag";

    @Override
    public void run(ApplicationArguments args) {
        log.info("==> 鏈嶅姟鍚姩锛屽紑濮嬪悓姝ヨ鑹叉潈闄愭暟鎹埌 Redis 涓?..");

        try {
            // 鏄惁鑳藉鍚屾鏁版嵁: 鍘熷瓙鎿嶄綔锛屽彧鏈夊湪閿?PUSH_PERMISSION_FLAG 涓嶅瓨鍦ㄦ椂锛屾墠浼氳缃閿殑鍊间负 "1"锛屽苟璁剧疆杩囨湡鏃堕棿涓?1 澶?
            boolean canPushed = redisTemplate.opsForValue().setIfAbsent(PUSH_PERMISSION_FLAG, "1", 1, TimeUnit.DAYS);

            // 濡傛灉鏃犳硶鍚屾鏉冮檺鏁版嵁
            if (!canPushed) {
                log.warn("==> 瑙掕壊鏉冮檺鏁版嵁宸茬粡鍚屾鑷?Redis 涓紝涓嶅啀鍚屾...");
                return;
            }

            // 鏌ヨ鍑烘墍鏈夎鑹?
            List<RoleDO> roleDOS = roleDOMapper.selectEnabledList();

            if (CollUtil.isNotEmpty(roleDOS)) {
                // 鎷垮埌鎵€鏈夎鑹茬殑 ID
                List<Long> roleIds = roleDOS.stream().map(RoleDO::getId).toList();

                // 鏍规嵁瑙掕壊 ID, 鎵归噺鏌ヨ鍑烘墍鏈夎鑹插搴旂殑鏉冮檺
                List<RolePermissionDO> rolePermissionDOS = rolePermissionDOMapper.selectByRoleIds(roleIds);
                // 鎸夎鑹?ID 鍒嗙粍, 姣忎釜瑙掕壊 ID 瀵瑰簲澶氫釜鏉冮檺 ID
                Map<Long, List<Long>> roleIdPermissionIdsMap = rolePermissionDOS.stream().collect(
                        Collectors.groupingBy(RolePermissionDO::getRoleId,
                                Collectors.mapping(RolePermissionDO::getPermissionId, Collectors.toList()))
                );

                // 鏌ヨ APP 绔墍鏈夎鍚敤鐨勬潈闄?
                List<PermissionDO> permissionDOS = permissionDOMapper.selectAppEnabledList();
                // 鏉冮檺 ID - 鏉冮檺 DO
                Map<Long, PermissionDO> permissionIdDOMap = permissionDOS.stream().collect(
                        Collectors.toMap(PermissionDO::getId, permissionDO -> permissionDO)
                );

                // 缁勭粐 瑙掕壊ID-鏉冮檺 鍏崇郴
                Map<String, List<String>> roleIdPermissionDOMap = Maps.newHashMap();

                // 寰幆鎵€鏈夎鑹?
                roleDOS.forEach(roleDO -> {
                    // 褰撳墠瑙掕壊 ID
                    Long roleId = roleDO.getId();
                    //褰撳墠瑙掕壊roleKey
                    String roleKey = roleDO.getRoleKey();
                    // 褰撳墠瑙掕壊 ID 瀵瑰簲鐨勬潈闄?ID 闆嗗悎
                    List<Long> permissionIds = roleIdPermissionIdsMap.get(roleId);
                    if (CollUtil.isNotEmpty(permissionIds)) {
                        List<String> perDOS = Lists.newArrayList();
                        permissionIds.forEach(permissionId -> {
                            // 鏍规嵁鏉冮檺 ID 鑾峰彇鍏蜂綋鐨勬潈闄?DO 瀵硅薄
                            PermissionDO permissionDO = permissionIdDOMap.get(permissionId);
                            if (Objects.nonNull(permissionDO)) {
                                perDOS.add(permissionDO.getPermissionKey());
                            }
                        });
                        roleIdPermissionDOMap.put(roleKey, perDOS);
                    }
                });

                // 鍚屾鑷?Redis 涓紝鏂逛究鍚庣画缃戝叧鏌ヨ閴存潈浣跨敤
                roleIdPermissionDOMap.forEach((roleKey, permissions) -> {
                    String key = RedisKeyConstants.buildRolePermissionKey(roleKey);
                    redisTemplate.opsForValue().set(key, JsonUtils.toJsonString(permissions));
                });
            }

            log.info("==> 鏈嶅姟鍚姩锛屾垚鍔熷悓姝ヨ鑹叉潈闄愭暟鎹埌 Redis 涓?..");
        } catch (Exception e) {
            log.error("==> 鍚屾瑙掕壊鏉冮檺鏁版嵁鍒?Redis 涓け璐? ", e);
        }

    }
}
