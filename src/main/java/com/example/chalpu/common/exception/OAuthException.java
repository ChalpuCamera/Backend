package com.example.chalpu.common.exception;

/**
 * OAuth 관련 예외
 */
public class OAuthException extends BaseException {
    
    public OAuthException(ErrorMessage errorMessage) {
        super(errorMessage);
    }
}
