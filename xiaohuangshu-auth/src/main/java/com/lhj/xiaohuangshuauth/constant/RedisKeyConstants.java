package com.lhj.xiaohuangshuauth.constant;

public class RedisKeyConstants {
    //验证码KEY前缀
    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";

    //构建验证码KEY
    public static String buildVerificationCodeKey(String phone){
        return VERIFICATION_CODE_KEY_PREFIX + phone;
    }

   //小皇书全局id生成器
   public static final String XIAOHUANGSHU_ID_GENERATOR_KEY = "xiaohuangshu.id.generator";

   private  static final String USER_ROLES_KEY_PREFIX = "user:roles:";

    /**
     * 构建用户-角色 Key
     * @param
     * @return
     */

    public static String buildUserRoleKey(Long userId){
        return USER_ROLES_KEY_PREFIX + userId;
    }
  //角色对应的权限集合KEY前缀
    private static String ROLE_PERMISSIONS_KEY_PREFIX = "role:permissions:";

    /**
     * 构建角色对应的权限集合 KEY
     * @param roleKey
     * @return
     */

    public static String buildRolePermissionKey(String roleKey){
        return ROLE_PERMISSIONS_KEY_PREFIX + roleKey;
    }

}
