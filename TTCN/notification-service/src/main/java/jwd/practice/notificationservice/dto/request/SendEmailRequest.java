package jwd.practice.notificationservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendEmailRequest {
    String to;
    String subject;
    String htmlContent;
}
