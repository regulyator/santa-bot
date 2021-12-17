package com.regulyator.santabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SantaBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SantaBotApplication.class, args);
    }

}
