package com.hzy.controller.admin;

import com.hzy.constant.MessageConstant;
import com.hzy.result.Result;
import com.hzy.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
//TODO 文件上传
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file.getOriginalFilename());
        try {
            //获取文件名字
            String originalFilename = file.getOriginalFilename();
            //调用工具类
            String url= aliOssUtil.upload(file.getBytes(), originalFilename);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
