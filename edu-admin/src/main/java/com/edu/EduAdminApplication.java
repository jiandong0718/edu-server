package com.edu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 教育机构学生管理系统 - 启动类
 */
@SpringBootApplication(exclude = {
        com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure.class
})
@MapperScan("com.edu.**.mapper")
@EnableScheduling
@EnableAsync
public class EduAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduAdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("  教育机构学生管理系统启动成功！");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("========================================");
    }
}
