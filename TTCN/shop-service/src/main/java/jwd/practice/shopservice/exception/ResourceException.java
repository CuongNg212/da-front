package jwd.practice.shopservice.exception;

import jwd.practice.shopservice.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestControllerAdvice
public class ResourceException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> runtimeExceptionHandler(AppException appException) {
        ErrException errException = appException.getErrException();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errException.getCode());
        apiResponse.setMessage(errException.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = IdException.class)
    public ResponseEntity<ApiResponse> idExceptionHandeler(Exception exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

}
