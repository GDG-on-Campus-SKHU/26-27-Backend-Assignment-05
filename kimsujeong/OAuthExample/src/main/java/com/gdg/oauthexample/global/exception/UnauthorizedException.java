package com.gdg.oauthexample.global.exception;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
