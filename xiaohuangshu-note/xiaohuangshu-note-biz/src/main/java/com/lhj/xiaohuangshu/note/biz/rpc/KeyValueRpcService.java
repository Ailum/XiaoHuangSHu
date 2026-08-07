package com.lhj.xiaohuangshu.note.biz.rpc;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.kv.api.KeyValueFeignApi;
import com.lhj.xiaohuangshu.kv.dto.req.AddNoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.req.DeleteNoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.req.FindnoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.rsp.FindNoteContentRspDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class KeyValueRpcService {

    @Resource
    private KeyValueFeignApi keyValueFeignApi;

    /**
     * 保存笔记内容
     */
    public boolean saveNoteContent(String uuid, String content) {
        AddNoteContentReqDTO addNoteContentReqDTO = new AddNoteContentReqDTO();
        addNoteContentReqDTO.setUuid(uuid);
        addNoteContentReqDTO.setContent(content);

        Response<?> response = keyValueFeignApi.addNoteContent(addNoteContentReqDTO);

        if(Objects.isNull(response) || !response.isSuccess()){
            return false;
    }
 return true;
 }

    /**
     * 删除笔记内容
     */

    public boolean deleteNoteContent(String uuid) {
        DeleteNoteContentReqDTO deleteNoteContentReqDTO = new DeleteNoteContentReqDTO();
        deleteNoteContentReqDTO.setUuid(uuid);

        Response<?> response = keyValueFeignApi.deleteNoteContent(deleteNoteContentReqDTO);

        if (Objects.isNull(response) || !response.isSuccess()) {
            return false;
        }

        return true;
    }

    /**
     * 查询笔记内容
     */
    public String findNoteContent(String uuid) {
        FindnoteContentReqDTO findNoteContentReqDTO = new FindnoteContentReqDTO();
        findNoteContentReqDTO.setUuid(uuid);

        Response<FindNoteContentRspDTO> response = keyValueFeignApi.findNoteContent(findNoteContentReqDTO);

        if(Objects.isNull(response) || !response.isSuccess() || Objects.isNull(response.getData())){
            return null;
        }
        return response.getData().getContent();
    }

}