package com.lhj.xiaohuangshu.distributed.id.generator.biz.controller;

import com.lhj.xiaohuangshu.distributed.id.generator.biz.core.common.Result;
import com.lhj.xiaohuangshu.distributed.id.generator.biz.core.common.Status;
import com.lhj.xiaohuangshu.distributed.id.generator.biz.exception.LeafServerException;
import com.lhj.xiaohuangshu.distributed.id.generator.biz.exception.NoKeyException;
import com.lhj.xiaohuangshu.distributed.id.generator.biz.service.SegmentService;
import com.lhj.xiaohuangshu.distributed.id.generator.biz.service.SnowflakeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/id")
public class LeafController {
   @Resource
   private SegmentService segmentService;
   @Resource
   private SnowflakeService snowflakeService;

    @RequestMapping(value = "/api/segment/get/{key}")
    public String getSegmentId(@PathVariable("key") String key) {
        return get(key, segmentService.getId(key));
    }

    @RequestMapping(value = "/api/snowflake/get/{key}")
    public String getSnowflakeId(@PathVariable("key") String key) {
        return get(key, snowflakeService.getId(key));
    }

    private String get(@PathVariable("key") String key, Result id) {
        Result result;
        if (key == null || key.isEmpty()) {
            throw new NoKeyException();
        }
        result = id;
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(result.toString());
        }
        return String.valueOf(result.getId());
    }
}
