package jwd.practice.userservice.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    int userId;
    @Size(min = 3, message = "INVALID_USERNAME")
    String username;
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;
    String email;
    String phone;
    String avatar;
    String address;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean enabled;
}