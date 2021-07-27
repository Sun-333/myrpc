package com.example.exampleserver;

import com.example.exampleserver.service.HelloServiceImp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ExampleServerApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ExampleServerApplication.class, args);
    }
}
