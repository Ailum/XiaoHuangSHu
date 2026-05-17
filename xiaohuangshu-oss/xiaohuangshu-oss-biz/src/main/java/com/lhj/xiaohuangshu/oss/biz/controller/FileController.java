package com.lhj.xiaohuangshu.oss.biz.controller;

import com.lhj.framework.biz.context.holder.LoginUserContextHolder;
import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.oss.biz.service.FileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public Response<?> updateFile(@RequestParam("file") MultipartFile file) {
        log.info("当前用户 ID: {}", LoginUserContextHolder.getUserId());


        return fileService.uploadFile(file);
    }
}
