package com.ssafy.tlog.exception.custom;

public class NicknameConflictException extends RuntimeException{
    public NicknameConflictException(String message) {
        super(message);
    }
}
