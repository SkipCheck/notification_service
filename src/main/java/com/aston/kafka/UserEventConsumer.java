package com.aston.kafka;

import com.aston.dto.UserEvent;
import com.aston.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "${kafka.topics.user-events}")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handleUserEvent(UserEvent event) {
        log.info("Получено событие из Kafka: {}", event);

        try {
            switch (event.getEventType()) {
                case USER_CREATED:
                    emailService.sendUserCreatedEmail(event.getEmail(), event.getName());
                    log.info("Отправлено email приветствия для пользователя: {}", event.getEmail());
                    break;
                case USER_DELETED:
                    emailService.sendUserDeletedEmail(event.getEmail(), event.getName());
                    log.info("Отправлено email об удалении для пользователя: {}", event.getEmail());
                    break;
                default:
                    log.warn("Неизвестный тип события: {}", event.getEventType());
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке события {}: {}", event.getEventType(), e.getMessage(), e);
            throw e; // Перебрасываем для retry
        }
    }
}