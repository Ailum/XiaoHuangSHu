package com.lhj.xiaohuangshu.note.biz.service.impl;

import org.apache.commons.lang3.StringUtils;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.exception.BizException;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.note.biz.domain.dataobject.NoteDO;
import com.lhj.xiaohuangshu.note.biz.domain.dataobject.TopicDO;
import com.lhj.xiaohuangshu.note.biz.domain.mapper.NoteDOMapper;
import com.lhj.xiaohuangshu.note.biz.domain.mapper.TopicDOMapper;
import com.lhj.xiaohuangshu.note.biz.enums.NoteStatusEnum;
import com.lhj.xiaohuangshu.note.biz.enums.NoteTypeEnum;
import com.lhj.xiaohuangshu.note.biz.enums.NoteVisibleEnum;
import com.lhj.xiaohuangshu.note.biz.enums.ResponseCodeEnum;
import com.lhj.xiaohuangshu.note.biz.model.vo.PublishNoteReqVO;
import com.lhj.xiaohuangshu.note.biz.rpc.DistributedIdGeneratorRpcService;
import com.lhj.xiaohuangshu.note.biz.rpc.KeyValueRpcService;
import com.lhj.xiaohuangshu.note.biz.service.NoteService;
import cn.hutool.core.collection.CollUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    @Resource
    private NoteDOMapper noteDOMapper;
    @Resource
    private TopicDOMapper topicDOMapper;
    @Resource
    private DistributedIdGeneratorRpcService distributedIdGeneratorRpcService;
    @Resource
    private KeyValueRpcService keyValueRpcService;


    /**
     * 笔记发布
     *
     * @param publishNoteReqVO
     * @return
     */
    @Override
    public Response<?> publishNote(PublishNoteReqVO publishNoteReqVO) {
        // 笔记类型
        Integer type = publishNoteReqVO.getType();

        // 获取对应类型的枚举
        NoteTypeEnum noteTypeEnum = NoteTypeEnum.valueOf(type);

        // 若非图文、视频，抛出业务业务异常
        if (Objects.isNull(noteTypeEnum)) {
            throw new BizException(ResponseCodeEnum.NOTE_TYPE_ERROR);
        }

        String imgUris = null;
        // 笔记内容是否为空，默认值为 true，即空
        Boolean isContentEmpty = true;
        String videoUri = null;
        switch (noteTypeEnum) {
            case IMAGE_TEXT: // 图文笔记
                List<String> imgUriList = publishNoteReqVO.getImgUris();
                // 校验图片是否为空
                Preconditions.checkArgument(CollUtil.isNotEmpty(imgUriList), "笔记图片不能为空");
                // 校验图片数量
                Preconditions.checkArgument(imgUriList.size() <= 8, "笔记图片不能多于 8 张");
                // 将图片链接拼接，以逗号分隔
                imgUris = StringUtils.join(imgUriList, ",");

                break;
            case VIDEO: // 视频笔记
                videoUri = publishNoteReqVO.getVideoUri();
                // 校验视频链接是否为空
                Preconditions.checkArgument(StringUtils.isNotBlank(videoUri), "笔记视频不能为空");
                break;
            default:
                break;
        }

        // RPC: 调用分布式 ID 生成服务，生成笔记 ID
        String snowflakeIdId = distributedIdGeneratorRpcService.getSnowflakeId();
        // 笔记内容 UUID
        String contentUuid = null;

        // 笔记内容
        String content = publishNoteReqVO.getContent();

        // 若用户填写了笔记内容
        if (StringUtils.isNotBlank(content)) {
            // 内容是否为空，置为 false，即不为空
            isContentEmpty = false;
            // 生成笔记内容 UUID
            contentUuid = UUID.randomUUID().toString();
            // RPC: 调用 KV 键值服务，存储短文本
            boolean isSavedSuccess = keyValueRpcService.saveNoteContent(contentUuid, content);

            // 若存储失败，抛出业务异常，提示用户发布笔记失败
            if (!isSavedSuccess) {
                throw new BizException(ResponseCodeEnum.NOTE_PUBLISH_FAIL);
            }
        }

        // 话题
        Long topicId = publishNoteReqVO.getTopicId();
        String topicName = null;
        if (Objects.nonNull(topicId)) {
            // 获取话题名称
            TopicDO topicDO = topicDOMapper.selectByPrimaryKey(topicId);
            if (Objects.nonNull(topicDO)) {
                topicName = topicDO.getName();
            }
        }

        // 发布者用户 ID
        Long creatorId = LoginUserContextHolder.getUserId();

        // 构建笔记 DO 对象
        Date now = new Date();
        NoteDO noteDO = new NoteDO();
        noteDO.setId(Long.valueOf(snowflakeIdId));
        noteDO.setIsContentEmpty(isContentEmpty);
        noteDO.setCreatorId(creatorId);
        noteDO.setImgUris(imgUris);
        noteDO.setTitle(publishNoteReqVO.getTitle());
        noteDO.setTopicId(topicId);
        noteDO.setTopicName(topicName);
        noteDO.setType(type.byteValue());
        noteDO.setVisible(NoteVisibleEnum.PUBLIC.getCode().byteValue());
        noteDO.setCreateTime(now);
        noteDO.setUpdateTime(now);
        noteDO.setStatus(NoteStatusEnum.NORMAL.getCode().byteValue());
        noteDO.setIsTop(Boolean.FALSE);
        noteDO.setVideoUri(videoUri);
        noteDO.setContentUuid(contentUuid);

        try {
            // 笔记入库存储
            noteDOMapper.insert(noteDO);
        } catch (Exception e) {
            log.error("==> 笔记存储失败", e);

            // RPC: 笔记保存失败，则删除笔记内容
            if (StringUtils.isNotBlank(contentUuid)) {
                keyValueRpcService.deleteNoteContent(contentUuid);
            }

            throw new BizException(ResponseCodeEnum.NOTE_PUBLISH_FAIL);
        }

        return Response.success();
    }


}
