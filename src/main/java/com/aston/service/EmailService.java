package com.aston.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.site.url}")
    private String siteUrl;

    @CircuitBreaker(name = "emailService", fallbackMethod = "sendEmailFallback")
    @Retry(name = "emailService")
    public void sendUserCreatedEmail(String toEmail, String userName) {
        String subject = "Добро пожаловать!";
        String message = String.format(
                "Здравствуйте, %s!\n\n" +
                        "Ваш аккаунт на сайте %s был успешно создан.\n\n" +
                        "С уважением,\n" +
                        "Команда поддержки",
                userName != null ? userName : "пользователь",
                siteUrl
        );

        sendEmail(toEmail, subject, message);
    }

    @CircuitBreaker(name = "emailService", fallbackMethod = "sendEmailFallback")
    @Retry(name = "emailService")
    public void sendUserDeletedEmail(String toEmail, String userName) {
        String subject = "Аккаунт удален";
        String message = String.format(
                "Здравствуйте%s!\n\n" +
                        "Ваш аккаунт был удалён.\n\n" +
                        "С уважением,\n" +
                        "Команда поддержки",
                userName != null ? ", " + userName : ""
        );

        sendEmail(toEmail, subject, message);
    }

    private void sendEmail(String toEmail, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email успешно отправлен на адрес: {}", toEmail);
        } catch (Exception e) {
            log.error("Ошибка при отправке email на адрес {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Не удалось отправить email на адрес: " + toEmail, e);
        }
    }

    // ========== FALLBACK METHODS ==========

    // Fallback для sendUserCreatedEmail
    public void sendEmailFallback(String toEmail, String userName, Throwable throwable) {
        log.warn("FALLBACK вызван для sendUserCreatedEmail. Email НЕ отправлен на адрес: {}. Причина: {}",
                toEmail, throwable != null ? throwable.getMessage() : "неизвестна");
    }

    // Fallback для sendUserDeletedEmail (если нужен отдельный)
    public void sendUserDeletedEmailFallback(String toEmail, String userName, Throwable throwable) {
        log.warn("FALLBACK вызван для sendUserDeletedEmail. Email НЕ отправлен на адрес: {}. Причина: {}",
                toEmail, throwable != null ? throwable.getMessage() : "неизвестна");
    }
}