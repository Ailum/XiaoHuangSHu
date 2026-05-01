package com.lhj.framework.biz.context.task;

import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import org.springframework.core.task.TaskDecorator;

public class LoginUserTransmitTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Long userId = LoginUserContextHolder.getUserId();
        return () -> {
            if (userId != null) {
                LoginUserContextHolder.setUserId(userId);
            }
            try {
                runnable.run();
            } finally {
                LoginUserContextHolder.remove();
            }
        };
    }
}
