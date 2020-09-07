package com.aaa.yk.es;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Param
 * @ClassName ApplicationRun
 * @Description TODO
 * @Author yk
 * @Date 2020/5/5 0005 16:00
 * @Return
 **/
@SpringBootApplication
@MapperScan("com.aaa.yk.es.mapper")
public class ApplicationRun {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationRun.class,args);
    }
}
