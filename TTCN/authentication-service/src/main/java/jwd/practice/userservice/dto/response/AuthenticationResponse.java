package jwd.practice.userservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    boolean check;
    int userId;
    String username;
}
