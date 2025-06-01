package jwd.practice.userservice.exception;

import jwd.practice.userservice.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
@RestControllerAdvice
public class ResourceException {
    @ExceptionHandler(value = AppException.class)
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
}
