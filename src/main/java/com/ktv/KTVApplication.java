package com.ktv;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.ktv.mapper")
@SpringBootApplication
public class KTVApplication {
    public static void main(String[] args) {
        SpringApplication.run(KTVApplication.class, args);
    }
}
