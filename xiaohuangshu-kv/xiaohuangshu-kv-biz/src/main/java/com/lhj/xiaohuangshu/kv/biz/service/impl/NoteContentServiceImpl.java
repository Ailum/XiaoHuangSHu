package com.lhj.xiaohuangshu.kv.biz.service.impl;


import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.kv.biz.domain.dataobject.NoteContentDO;
import com.lhj.xiaohuangshu.kv.biz.domain.repository.NoteContentRepository;
import com.lhj.xiaohuangshu.kv.biz.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshu.kv.biz.service.NoteContentService;
import com.lhj.xiaohuangshu.kv.dto.req.AddNoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.req.DeleteNoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.req.FindnoteContentReqDTO;
import com.lhj.xiaohuangshu.kv.dto.rsp.FindNoteContentRspDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class NoteContentServiceImpl implements NoteContentService {

    @Resource
    private NoteContentRepository noteContentRepository;

    @Override
    public Response<?> addNoteContent(AddNoteContentReqDTO addNoteContentReqDTO) {
        //笔记内容UUID
        String uuid = addNoteContentReqDTO.getUuid();
        //笔记内容
        String content = addNoteContentReqDTO.getContent();

        //构建数据库DO实体类
        NoteContentDO noteContent =  NoteContentDO.builder()
                .id(UUID.fromString(uuid)) // TODO: 暂时用 UUID, 目的是为了下一章讲解压测，不用动态传笔记 ID。后续改为笔记服务传过来的笔记 ID
                .content(content)
                .build();

        //插入数据
        noteContentRepository.save(noteContent);

        return Response.success();
    }

    /**
     * 查询笔记内容
     *
     * @param findNoteContentReqDTO
     * @return
     */
    @Override
    public Response<FindNoteContentRspDTO> findNoteContent(FindnoteContentReqDTO findNoteContentReqDTO) {
        // 笔记内容 UUID
        String uuid = findNoteContentReqDTO.getUuid();
        // 根据笔记 ID 查询笔记内容
        Optional<NoteContentDO> optional = noteContentRepository.findById(UUID.fromString(uuid));

        // 若笔记内容不存在
        if (!optional.isPresent()) {
            throw new BizException(ResponseCodeEnum.NOTE_CONTENT_NOT_FOUND);
        }

        NoteContentDO noteContentDO = optional.get();
        // 构建返参 DTO
        FindNoteContentRspDTO findNoteContentRspDTO = FindNoteContentRspDTO.builder()
                .uuid(noteContentDO.getId())
                .content(noteContentDO.getContent())
                .build();

        return Response.success(findNoteContentRspDTO);
    }
    /**
     * 删除笔记内容
     *
     * @param deleteNoteContentReqDTO
     * @return
     */
    @Override
    public Response<?> deleteNoteContent(DeleteNoteContentReqDTO deleteNoteContentReqDTO) {
        // 笔记内容 UUID
        String uuid = deleteNoteContentReqDTO.getUuid();
        // 删除笔记内容
        noteContentRepository.deleteById(UUID.fromString(uuid));

        return Response.success();
    }
}
