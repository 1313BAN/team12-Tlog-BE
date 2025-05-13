package com.ssafy.tlog.exception.global;

import com.ssafy.tlog.exception.custom.BadCredentialsException;
import com.ssafy.tlog.exception.custom.InvalidDataException;
import com.ssafy.tlog.exception.custom.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 BAD_REQUEST
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDataException(InvalidDataException e) {
        ErrorResponse error = new ErrorResponse(
                400,
                "BAD_REQUEST",
                e.getMessage()
        );
        return ResponseEntity.status(400).body(error);
    }

    // 401 BAD_REQUEST
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                e.getMessage()
        );
        return ResponseEntity.status(401).body(error);
    }

    // 404 NOT_FOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                404,
                "NOT_FOUND",
                e.getMessage()
        );
        return ResponseEntity.status(404).body(error);
    }

    // 500 INTERNAL_SERVER_ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e) {
        ErrorResponse error = new ErrorResponse(
                500,
                "INTERNAL_SERVER_ERROR",
                e.getMessage()
        );
        return ResponseEntity.status(500).body(error);
    }
}
