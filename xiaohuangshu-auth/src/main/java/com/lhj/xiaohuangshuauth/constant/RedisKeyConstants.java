package com.lhj.xiaohuangshuauth.constant;

public class RedisKeyConstants {
    //楠岃瘉鐮並EY鍓嶇紑
    private static final String VERIFICATION_CODE_KEY_PREFIX = "verification_code:";

    //鏋勫缓楠岃瘉鐮並EY
    public static String buildVerificationCodeKey(String phone){
        return VERIFICATION_CODE_KEY_PREFIX + phone;
    }

   //灏忕殗涔﹀叏灞€id鐢熸垚鍣?
   public static final String XIAOHUANGSHU_ID_GENERATOR_KEY = "xiaohuangshu.id.generator";

   private  static final String USER_ROLES_KEY_PREFIX = "user:roles:";

    /**
     * 鏋勫缓鐢ㄦ埛-瑙掕壊 Key
     * @param
     * @return
     */

    public static String buildUserRoleKey(Long userId){
        return USER_ROLES_KEY_PREFIX + userId;
    }
  //瑙掕壊瀵瑰簲鐨勬潈闄愰泦鍚圞EY鍓嶇紑
    private static String ROLE_PERMISSIONS_KEY_PREFIX = "role:permissions:";

    /**
     * 鏋勫缓瑙掕壊瀵瑰簲鐨勬潈闄愰泦鍚?KEY
     * @param roleKey
     * @return
     */

    public static String buildRolePermissionKey(String roleKey){
        return ROLE_PERMISSIONS_KEY_PREFIX + roleKey;
    }

}
