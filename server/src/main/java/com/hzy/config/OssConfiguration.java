package com.hzy.config;

import com.hzy.properties.AliOssProperties;
import com.hzy.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class OssConfiguration {
    //自定义第三方Bean, 注入到容器中
    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliosspro) {
        log.info("开始创建阿里云OSS客户端对象：{}", aliosspro);
        return new AliOssUtil(aliosspro.getEndpoint(), aliosspro.getAccessKeyId(), aliosspro.getAccessKeySecret(), aliosspro.getBucketName());

    }
}
