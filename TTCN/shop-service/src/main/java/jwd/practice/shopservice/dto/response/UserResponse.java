package jwd.practice.shopservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    int userId;
    String username;
    String password;
    String email;
    String phone;
    String avatar;
    String address;
    LocalDateTime createAt;
    LocalDateTime updateAt;
    boolean enabled;

    Set<String> roles;
}