package com.lhj.xiaohuangshu.kv.biz;

import com.lhj.framework.common.util.JsonUtils;
import com.lhj.xiaohuangshu.kv.biz.domain.dataobject.NoteContentDO;
import com.lhj.xiaohuangshu.kv.biz.domain.repository.NoteContentRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Slf4j
class CassandraTests {

    @Resource
    private NoteContentRepository noteContentRepository;

    /**
     * 测试修改数据
     */
    @Test
    void testUpdate() {
        NoteContentDO noteContent = NoteContentDO.builder()
                .id(UUID.fromString("1126a5db-4787-4e56-8408-00d8b67572f3"))
                .content("代码测试笔记内容更新")
                .build();

        noteContentRepository.save(noteContent);
    }

    @Test
    void testSelect() {
        Optional<NoteContentDO> optional = noteContentRepository.findById(UUID.fromString("1126a5db-4787-4e56-8408-00d8b67572f3"));
        optional.ifPresent(noteContentDO -> log.info("查询结果：{}", JsonUtils.toJsonString(noteContentDO)));
    }

    @Test
    void testDelete() {
        noteContentRepository.deleteById(UUID.fromString("1126a5db-4787-4e56-8408-00d8b67572f3"));
    }
}
