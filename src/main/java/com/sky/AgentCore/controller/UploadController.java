package com.sky.AgentCore.controller;

import com.sky.AgentCore.dto.common.Result;
import com.sky.AgentCore.service.service.upload.OssUploadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 文件上传控制器 提供前端直传OSS的上传凭证API */
@RestController
@RequestMapping("/upload")
public class UploadController {

    private final OssUploadService ossUploadService;

    public UploadController(OssUploadService ossUploadService) {
        this.ossUploadService = ossUploadService;
    }

    /** 获取上传凭证
     *
     * @return 上传凭证 */
    @GetMapping("/credential")
    public Result<OssUploadService.UploadCredential> getUploadCredential() {

        OssUploadService.UploadCredential credential = ossUploadService.generateUploadCredential();
        System.out.println("上传凭证:"+credential);
        return Result.success(credential);
    }

}
