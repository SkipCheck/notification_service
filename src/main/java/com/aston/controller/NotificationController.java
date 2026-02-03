package com.aston.controller;

import com.aston.dto.EmailRequest;
import com.aston.dto.EmailResponse;
import com.aston.dto.ErrorResponse;
import com.aston.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST-контроллер для работы с уведомлениями
 *
 * Обработка HTTP-запросов для отправки уведомлений
 * с поддержкой Swagger документации и HATEOAS
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "API для управления уведомлениями")
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email")
    @Operation(
            summary = "Отправить email уведомление",
            description = "Отправляет email уведомление указанному пользователю"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Email успешно отправлен",
                    content = @Content(schema = @Schema(implementation = EmailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка при отправке email",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EmailResponse> sendEmail(
            @Parameter(description = "Данные для отправки email", required = true)
            @Valid @RequestBody EmailRequest request) {

        log.info("POST /api/v1/notifications/email - Отправка email на адрес: {}", request.getToEmail());

        emailService.sendUserCreatedEmail(request.getToEmail(), request.getUserName());

        EmailResponse response = EmailResponse.builder()
                .status("SUCCESS")
                .message("Email успешно отправлен")
                .toEmail(request.getToEmail())
                .sentAt(LocalDateTime.now())
                .emailType("USER_CREATED_NOTIFICATION")
                .build();

        // Добавляем HATEOAS ссылки
        response.add(linkTo(methodOn(NotificationController.class).sendEmail(request)).withSelfRel());
        response.add(linkTo(methodOn(NotificationController.class).getServiceStatus()).withRel("status"));
        response.add(linkTo(methodOn(NotificationController.class).sendTestEmail()).withRel("test-email"));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    @Operation(
            summary = "Получить статус сервиса",
            description = "Возвращает текущий статус сервиса уведомлений"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус сервиса получен",
                    content = @Content(schema = @Schema(implementation = EmailResponse.class))
            )
    })
    public ResponseEntity<EmailResponse> getServiceStatus() {

        log.debug("GET /api/v1/notifications/status - Получение статуса сервиса");

        EmailResponse response = EmailResponse.builder()
                .status("RUNNING")
                .message("Сервис уведомлений работает нормально")
                .sentAt(LocalDateTime.now())
                .build();

        // Добавляем HATEOAS ссылки
        response.add(linkTo(methodOn(NotificationController.class).getServiceStatus()).withSelfRel());
        response.add(linkTo(methodOn(NotificationController.class).sendTestEmail()).withRel("test-email"));
        response.add(linkTo(methodOn(NotificationController.class).sendEmail(new EmailRequest())).withRel("send-email"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/test-email")
    @Operation(
            summary = "Отправить тестовый email",
            description = "Отправляет тестовый email для проверки работы сервиса"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Тестовый email успешно отправлен",
                    content = @Content(schema = @Schema(implementation = EmailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка при отправке тестового email",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<EmailResponse> sendTestEmail() {

        log.info("POST /api/v1/notifications/test-email - Отправка тестового email");

        String testEmail = "test@example.com";
        emailService.sendUserCreatedEmail(testEmail, "Тестовый пользователь");

        EmailResponse response = EmailResponse.builder()
                .status("SUCCESS")
                .message("Тестовый email успешно отправлен")
                .toEmail(testEmail)
                .sentAt(LocalDateTime.now())
                .emailType("TEST_NOTIFICATION")
                .build();

        // Добавляем HATEOAS ссылки
        response.add(linkTo(methodOn(NotificationController.class).sendTestEmail()).withSelfRel());
        response.add(linkTo(methodOn(NotificationController.class).getServiceStatus()).withRel("status"));
        response.add(linkTo(methodOn(NotificationController.class).sendEmail(new EmailRequest())).withRel("send-email"));

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "Информация о сервисе уведомлений",
            description = "Возвращает основную информацию о сервисе и доступных эндпоинтах"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о сервисе получена",
                    content = @Content(schema = @Schema(implementation = EmailResponse.class))
            )
    })
    public ResponseEntity<EmailResponse> getServiceInfo() {

        log.debug("GET /api/v1/notifications - Получение информации о сервисе");

        EmailResponse response = EmailResponse.builder()
                .status("AVAILABLE")
                .message("Сервис уведомлений готов к работе")
                .sentAt(LocalDateTime.now())
                .build();

        // Добавляем HATEOAS ссылки для навигации по API
        response.add(linkTo(methodOn(NotificationController.class).getServiceInfo()).withSelfRel());
        response.add(linkTo(methodOn(NotificationController.class).getServiceStatus()).withRel("status"));
        response.add(linkTo(methodOn(NotificationController.class).sendTestEmail()).withRel("test-email"));
        response.add(linkTo(methodOn(NotificationController.class).sendEmail(new EmailRequest())).withRel("send-email"));

        return ResponseEntity.ok(response);
    }
}