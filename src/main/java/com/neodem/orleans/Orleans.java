package com.neodem.orleans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/25/19
 */
@SpringBootApplication
@RestController
public class Orleans {

    @RequestMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    public static void main(String[] args) {
        SpringApplication.run(Orleans.class, args);
    }
}