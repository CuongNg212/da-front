package jwd.practice.notificationservice.service;


import jwd.practice.notificationservice.dto.request.SendEmailRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    @Autowired
    JavaMailSender mailSender;

    public String sendEmail(SendEmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setText(request.getHtmlContent());
        mailSender.send(message);
        return "Đã gửi tin nhắn";
    }
}
