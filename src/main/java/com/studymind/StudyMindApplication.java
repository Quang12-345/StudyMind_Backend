package com.studymind;

import com.studymind.config.JwtProperties;
import com.studymind.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, StorageProperties.class})
public class StudyMindApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyMindApplication.class, args);
    }
}
