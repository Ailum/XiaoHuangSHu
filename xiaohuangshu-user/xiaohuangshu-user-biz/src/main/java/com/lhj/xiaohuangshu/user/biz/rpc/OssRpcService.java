package com.lhj.xiaohuangshu.user.biz.rpc;


import com.lhj.framework.common.response.Response;
import com.lhj.xiaohuangshu.oss.api.FileFeignApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class OssRpcService {

    @Resource
    private FileFeignApi fileFeignApi;

    public String uploadFile(MultipartFile file) {
        // 璋冪敤瀵硅薄瀛樺偍鏈嶅姟涓婁紶鏂囦欢
        Response<?> response = fileFeignApi.uploadFile(file);

        if (!response.isSuccess()) {
            return null;
        }

        // 杩斿洖鍥剧墖璁块棶閾炬帴
        return (String) response.getData();
    }
}
