package com.lhj.xiaohuangshu.note.biz.service;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.note.biz.model.vo.PublishNoteReqVO;

public interface NoteService {
    Response<?> publishNote(PublishNoteReqVO publishNoteReqVO);

}
