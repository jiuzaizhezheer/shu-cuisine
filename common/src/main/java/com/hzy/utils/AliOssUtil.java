package com.hzy.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 文件上传
     *
     * @param bytes      文件字节内容
     * @param objectName 原始文件名（带后缀）
     * @return 文件访问路径
     */
    public String upload(byte[] bytes, String objectName) {
        // 参数校验
        if (bytes == null || bytes.length == 0) {
            log.warn("上传失败：文件内容为空");
            throw new IllegalArgumentException("文件内容不能为空");
        }
        if (objectName == null || objectName.trim().isEmpty()) {
            log.warn("上传失败：文件名为空");
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 获取当前系统日期目录
        String dir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 获取文件后缀，若无后缀则默认为空
        int lastDotIndex = objectName.lastIndexOf(".");
        String suffix = (lastDotIndex != -1) ? objectName.substring(lastDotIndex) : "";

        // 生成唯一文件名
        objectName = dir + "/" + UUID.randomUUID() + suffix;

        // 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 上传文件
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            log.error("OSS 服务端异常: ErrorCode={}, ErrorMessage={}, RequestId={}, HostId={}",
                    oe.getErrorCode(), oe.getErrorMessage(), oe.getRequestId(), oe.getHostId(), oe);
            throw new RuntimeException("文件上传失败", oe);
        } catch (ClientException ce) {
            log.error("OSS 客户端异常: Message={}", ce.getMessage(), ce);
            throw new RuntimeException("文件上传失败", ce);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 构建访问链接
        String fileUrl = String.format("https://%s.%s/%s", bucketName, endpoint, objectName);
        log.info("文件上传到: {}", fileUrl);
        return fileUrl;
    }
}
