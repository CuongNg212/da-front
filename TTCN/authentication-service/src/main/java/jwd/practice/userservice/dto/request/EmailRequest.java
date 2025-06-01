package jwd.practice.userservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {
    String to;
    String subject;
    String htmlContent;
}
