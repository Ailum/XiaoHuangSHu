package com.lhj.xiaohuangshu.note.biz.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.note.biz.model.vo.FindNoteDetailReqVO;
import com.lhj.xiaohuangshu.note.biz.model.vo.FindNoteDetailRspVO;
import com.lhj.xiaohuangshu.note.biz.model.vo.PublishNoteReqVO;
import com.lhj.xiaohuangshu.note.biz.model.vo.UpdateNoteReqVO;

public interface NoteService {
    Response<?> publishNote(PublishNoteReqVO publishNoteReqVO);

    /**
     * 笔记详情
     */
    Response<FindNoteDetailRspVO> findNoteDetail(FindNoteDetailReqVO findNoteDetailReqVO);

    /**
     * 笔记更新
     * @param updateNoteReqVO
     * @return
     */
    Response<?> updateNote(UpdateNoteReqVO updateNoteReqVO);
}
