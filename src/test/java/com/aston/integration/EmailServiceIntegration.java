package com.aston.integration;

import com.aston.NotificationServiceApplication;
import com.aston.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = NotificationServiceApplication.class)
@ActiveProfiles("test")
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void testSendUserCreatedEmail() {
        String toEmail = "user@example.com";
        String userName = "Иван Иванов";

        emailService.sendUserCreatedEmail(toEmail, userName);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendUserDeletedEmail() {
        String toEmail = "user@example.com";
        String userName = "Иван Иванов";

        emailService.sendUserDeletedEmail(toEmail, userName);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}