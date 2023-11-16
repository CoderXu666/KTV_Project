package com.ktv.controller;

import com.ktv.utils.MinioUtil;
import com.ktv.utils.R;
import com.ktv.utils.ResponseEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author 徐志斌
 * @Date: 2023/11/16 19:46
 * @Version 1.0
 * @Description: 文件上传
 */
@RestController
@RequestMapping("/file")
public class FileUploadController {
    @Autowired
    private MinioUtil minioUtil;

    /**
     * 文件上传
     * http://101.42.13.186:9000/avatar-bucket/1700135638634_1700135821321.jpg
     */
    @PostMapping("/upload")
    public R upload(MultipartFile file) throws Exception {
        String url = minioUtil.upload(file, "avatar-bucket");
        return R.out(ResponseEnum.SUCCESS, url);
    }
}
