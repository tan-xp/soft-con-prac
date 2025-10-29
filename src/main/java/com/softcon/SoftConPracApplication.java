package com.softcon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SoftConPracApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoftConPracApplication.class, args);
    }

}
