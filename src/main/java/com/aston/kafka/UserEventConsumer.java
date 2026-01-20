package com.aston.kafka;

import com.aston.dto.UserEvent;
import com.aston.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topics.user-events}")
    public void handleUserEvent(UserEvent event) {
        log.info("Получено событие из Kafka: {}", event);

        try {
            switch (event.getEventType()) {
                case "USER_CREATED":
                    emailService.sendUserCreatedEmail(event.getEmail(), event.getName());
                    break;
                case "USER_DELETED":
                    emailService.sendUserDeletedEmail(event.getEmail(), event.getName());
                    break;
                default:
                    log.warn("Неизвестный тип события: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке события: {}", e.getMessage(), e);
        }
    }
}