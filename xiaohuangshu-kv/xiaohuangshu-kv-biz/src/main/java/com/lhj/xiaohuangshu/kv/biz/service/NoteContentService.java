package com.lhj.xiaohuangshu.kv.biz.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.kv.dto.req.AddNoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.req.DeleteNoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.req.FindnoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.rsp.FindNoteContentRspDTO;

/**
 *笔记内容存储
 */
public interface NoteContentService {
  /**
  *添加笔记内容
   */

   Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO);


    /**
     * 查询笔记内容
     *
     * @param findNoteContentReqDTO
     * @return
     */
    Response<FindNoteContentRspDTO> findNoteContent(FindnoteContentReqDTO findNoteContentReqDTO);

    /**
     * 删除笔记内容
     *
     * @param deleteNoteContentReqDTO
     * @return
     */
    Response<?> deleteNoteContent(DeleteNoteContentReqDTO deleteNoteContentReqDTO);

}
