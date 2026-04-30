package com.lhj.xiaohuangshu.oss.biz.service.impl;

import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.oss.biz.service.FileService;
import com.lhj.xiaohuangshu.oss.biz.strategy.FileStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Resource
    private FileStrategy fileStrategy;

    @Value("${storage.bucket-name}")
    private String bucketName;

    @Override
    public Response<?> uploadFile(MultipartFile file) {
        String url = fileStrategy.uploadFile(file, bucketName);
        return Response.success(url);
    }
}
