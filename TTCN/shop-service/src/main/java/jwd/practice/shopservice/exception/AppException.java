package jwd.practice.shopservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppException extends RuntimeException {
    public ErrException errException;
}
