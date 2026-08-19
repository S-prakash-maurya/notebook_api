package com.micro.subject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * NOTE: JwtTokenProvider in the auth service throws com.generic.service
 * .exception.GenericException(int statusCode, String message) for its
 * error cases. That class only carries a status code + message, with no
 * dedicated "error code" field, so it doesn't map cleanly onto the
 * { success, message, error } envelope required for this API.
 * This service therefore defines its own lightweight exception type that
 * carries both. If GenericException already supports an error-code field
 * in your actual codebase, swap this out for that instead.
 */
@Getter
public class SubjectException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public SubjectException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static SubjectException notFound() {
        return new SubjectException(HttpStatus.NOT_FOUND, ErrorCode.SUBJECT_NOT_FOUND, "Subject not found");
    }

    public static SubjectException unauthorized() {
        return new SubjectException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, "Unauthorized");
    }
}
