
package com.example.exampleserver.service;

import com.example.commoninterface.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class HelloController {
    @Autowired
    private HelloService helloService;
    public void test(){

    }
}
