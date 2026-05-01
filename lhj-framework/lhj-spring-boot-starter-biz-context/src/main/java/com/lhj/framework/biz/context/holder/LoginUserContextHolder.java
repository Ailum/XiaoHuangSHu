package com.lhj.framework.biz.context.holder;

import com.lhj.framework.common.constant.GlobalConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginUserContextHolder {

    private static final ThreadLocal<Map<String, Object>> LOGIN_USER_CONTEXT_THREAD_LOCAL =
            ThreadLocal.withInitial(HashMap::new);

    private LoginUserContextHolder() {
    }

    public static void setUserId(Object value) {
        LOGIN_USER_CONTEXT_THREAD_LOCAL.get().put(GlobalConstants.USER_ID, value);
    }

    public static Long getUserId() {
        Object value = LOGIN_USER_CONTEXT_THREAD_LOCAL.get().get(GlobalConstants.USER_ID);
        if (Objects.isNull(value)) {
            return null;
        }
        return Long.valueOf(value.toString());
    }

    public static void remove() {
        LOGIN_USER_CONTEXT_THREAD_LOCAL.remove();
    }
}
