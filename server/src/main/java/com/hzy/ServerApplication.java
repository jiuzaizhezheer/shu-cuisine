package com.hzy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
//TODO 启动类
@SpringBootApplication
@EnableTransactionManagement //显式声明开启注解方式的事务管理，可读性更强
@EnableCaching // 开启缓存注解功能
@EnableScheduling // 开启定时调度功能
@Slf4j
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        log.info("server started successfully!");
    }

}
