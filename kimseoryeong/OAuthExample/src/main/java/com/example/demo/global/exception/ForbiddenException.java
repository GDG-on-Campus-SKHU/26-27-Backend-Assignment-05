package com.example.demo.global.exception;

public class ForbiddenException extends CustomException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
