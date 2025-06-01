package jwd.practice.shopservice.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageDTO {
    int messageId;
    int senderId;
    int receiverId;
    String message;
    Instant createdAt;

}
