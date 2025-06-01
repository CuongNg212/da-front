package jwd.practice.notificationservice.exception;


import jwd.practice.notificationservice.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class ResourceException {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> runtimeException(AppException appException) {
        ErrException err = appException.getErrException();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(err.getCode());
        apiResponse.setMessage(err.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> notValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        String enumKey = methodArgumentNotValidException.getFieldError().getDefaultMessage();
        ErrException errException = ErrException.INVALID_KEY;
        try {
            errException = ErrException.valueOf(enumKey);
        } catch (IllegalArgumentException e) {

        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errException.getCode());
        apiResponse.setMessage(errException.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> accessDeniedException(AccessDeniedException accessDeniedException) {
        ErrException errException = ErrException.UNAUTHORIZED;

        return ResponseEntity.status(errException.getCode())
                .body(ApiResponse.builder()
                        .code(errException.getCode()).message(errException.getMessage())
                        .build());
    }

}
