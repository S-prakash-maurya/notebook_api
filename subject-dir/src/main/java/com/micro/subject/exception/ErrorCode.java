package com.micro.subject.exception;

public final class ErrorCode {
    private ErrorCode() {
    }

    public static final String SUBJECT_NOT_FOUND = "SUBJECT_NOT_FOUND";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
}
