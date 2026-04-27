package com.resume.airesumoptimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.resume.airesumoptimizer")
public class AiResumeOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiResumeOptimizerApplication.class, args);
    }
}
