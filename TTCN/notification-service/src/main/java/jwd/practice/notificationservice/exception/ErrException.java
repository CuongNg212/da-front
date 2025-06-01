package jwd.practice.notificationservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public enum ErrException {
    INVALID_KEY(105,"invalid key"),
    INVALID_EMAIL(106,"invalid email"),
    UNAUTHENTICATED(1006, "Unauthenticated"),
    UNAUTHORIZED(108, "You do not have permission"),
    CANNOT_SEND_EMAIL(109, "cannot send email"),
    ;
    private int code;
    private String message;
}
