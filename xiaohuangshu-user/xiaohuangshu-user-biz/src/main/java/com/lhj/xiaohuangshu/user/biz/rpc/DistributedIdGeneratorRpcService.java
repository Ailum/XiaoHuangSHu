package com.lhj.xiaohuangshu.user.biz.rpc;


import com.lhj.xiaohuangshu.distributed.id.generator.api.DistributedIdGeneratorFeignApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class DistributedIdGeneratorRpcService {
 @Resource
 private DistributedIdGeneratorFeignApi distributedIdGeneratorFeignApi;

    /**
     * Leaf号段模式：小皇书ID业务标识
     */

    private static final String BIZ_TAG_XIAOHUANGSHU_ID = "leaf-segment-xiaohuangshu-id";

    /**
     * 调用分布式ID生成服务生成小皇书ID
     */

    public String getXiaohuangshuId(){
        return distributedIdGeneratorFeignApi.getSegmentId(BIZ_TAG_XIAOHUANGSHU_ID);
    }
}
