package com.ssafy.tlog.exception.global;

import com.ssafy.tlog.exception.custom.InvalidUserException;
import com.ssafy.tlog.exception.custom.NicknameConflictException;
import com.ssafy.tlog.exception.custom.SocialIdConflictException;
import com.ssafy.tlog.exception.custom.TokenExpiredException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 400 BAD_REQUEST
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ErrorResponse error = new ErrorResponse(
                400,
                "BAD_REQUEST",
                "필수 파라미터가 누락되었습니다: " + e.getParameterName()
        );
        return ResponseEntity.status(400).body(error);
    }

    // 401 UNAUTHORIZED
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException e) {
        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                e.getMessage()
        );
        return ResponseEntity.status(401).body(error);
    }

    // 401 UNAUTHORIZED
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                e.getMessage()
        );
        return ResponseEntity.status(401).body(error);
    }

    // 401 UNAUTHORIZED
    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserException(InvalidUserException e) {
        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                e.getMessage()
        );
        return ResponseEntity.status(401).body(error);
    }

    // 409 Conflict
    @ExceptionHandler(NicknameConflictException.class)
    public ResponseEntity<ErrorResponse> handleNicknameConflictException(NicknameConflictException e) {
        ErrorResponse error = new ErrorResponse(
                409,
                "Conflict",
                e.getMessage()
        );
        return ResponseEntity.status(409).body(error);
    }

    // 409 Conflict
    @ExceptionHandler(SocialIdConflictException.class)
    public ResponseEntity<ErrorResponse> handleSocialIdConflictException(SocialIdConflictException e) {
        ErrorResponse error = new ErrorResponse(
                409,
                "Conflict",
                e.getMessage()
        );
        return ResponseEntity.status(409).body(error);
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
