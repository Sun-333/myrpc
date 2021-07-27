package com.uestc.exmaple.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class ExmapleClientApplication {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ApplicationContext applicationContext  = SpringApplication.run(ExmapleClientApplication.class, args);
        applicationContext.getBean(HelloController.class).test();
    }

}
