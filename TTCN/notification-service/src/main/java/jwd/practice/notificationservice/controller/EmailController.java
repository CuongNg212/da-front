package jwd.practice.notificationservice.controller;

import jwd.practice.notificationservice.dto.request.SendEmailRequest;
import jwd.practice.notificationservice.dto.response.ApiResponse;
import jwd.practice.notificationservice.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;

    @PostMapping("/email/send")
    public ApiResponse<String> sendEmail(@RequestBody SendEmailRequest sendEmailRequest) {
        return ApiResponse.<String>builder()
                .code(200)
                .result(emailService.sendEmail(sendEmailRequest))
                .build();
    }
}
