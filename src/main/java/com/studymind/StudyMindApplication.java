package com.studymind;

import com.studymind.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class StudyMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyMindApplication.class, args);
    }
}
