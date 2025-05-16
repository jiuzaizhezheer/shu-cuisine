package com.hzy.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hzy.alioss")
@Data
public class AliOssProperties {
    // 阿里云oss文件上传的域名
    private String endpoint;
    // 阿里云oss文件上传的accessKeyId
    private String accessKeyId;
    // 阿里云oss文件上传的accessKeySecret
    private String accessKeySecret;
    // 阿里云oss文件上传的Bucket名称
    private String bucketName;

}
