package com.aston;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Slf4j
@SpringBootApplication
public class NotificationServiceApplication {

    @PostConstruct
    public void init(){
        log.info("Сервис уведомлений запущен {}", LocalDateTime.now());
    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}