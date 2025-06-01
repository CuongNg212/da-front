package jwd.practice.userservice.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    String oldPassword;
    String newPassword;
}
