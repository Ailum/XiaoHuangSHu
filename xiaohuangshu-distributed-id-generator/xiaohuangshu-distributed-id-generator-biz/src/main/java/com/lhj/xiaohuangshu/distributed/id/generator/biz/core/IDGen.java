package com.lhj.xiaohuangshu.distributed.id.generator.biz.core;

import com.lhj.xiaohuangshu.distributed.id.generator.biz.core.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
