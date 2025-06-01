package jwd.practice.userservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public enum ErrException {
    USER_EXISTED(101,"user already existed"),
    USER_NOT_EXISTED(102,"user not existed"),
    INVALID_USERNAME(103,"invalid username"),
    INVALID_PASSWORD(104,"invalid password"),
    INVALID_KEY(105,"invalid key"),
    INVALID_EMAIL(106,"invalid email"),
    NOT_FILE(107, "Not file"),
    DIRECTORY_CREATION_FAILED(108, "Directory creation failed"),
    FILE_STORAGE_FAILED(109, "File storage failed"),
    UNAUTHENTICATED(1006, "Unauthenticated"),
    UNAUTHORIZED(1007, "You do not have permission"),
    ;
    private int code;
    private String message;
}
