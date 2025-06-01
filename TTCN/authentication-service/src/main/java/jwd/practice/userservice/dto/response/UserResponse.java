package jwd.practice.userservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    int userId;
    String username;
    String password;
    String email;
    String phone;
    String avatar;
    String address;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean enabled;

    Set<String> roles;
}
