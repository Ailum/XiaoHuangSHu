package com.lhj.xiaohuangshu.kv.api;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.kv.constant.ApiConstants;
import com.lhj.xiaohuangshu.kv.dto.req.AddNoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.req.DeleteNoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.req.FindnoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.rsp.FindNoteContentRspDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.SERVICE_NAME)
public interface KeyValueFeignApi {

    String PREFIX = "/kv";

    @PostMapping(value = PREFIX + "/note/content/add")
    Response<?> addNoteContent(@RequestBody AddNoteContentReqDTO addNoteContentReqDTO);

    @PostMapping(value = PREFIX + "/note/content/find")
    Response<FindNoteContentRspDTO> findNoteContent(@RequestBody FindnoteContentReqDTO findnoteContentReqDTO);

    @PostMapping(value = PREFIX + "/note/content/delete")
    Response<?> deleteNoteContent(@RequestBody DeleteNoteContentReqDTO deleteNoteContentReqDTO);
}
