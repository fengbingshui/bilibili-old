package com.zhu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BiliBiliApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(BiliBiliApplication.class,args);
    }
}
