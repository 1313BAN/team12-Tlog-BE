package com.ssafy.tlog.exception.custom;

public class SocialIdConflictException extends RuntimeException{
    public SocialIdConflictException(String message) {
        super(message);
    }
}
