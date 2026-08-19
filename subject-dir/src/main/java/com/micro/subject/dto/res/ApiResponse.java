package com.micro.subject.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Single consistent response envelope for every endpoint in this service.
 *
 * Success:
 * { "success": true,  "message": "...", "data": {...} }
 *
 * Error:
 * { "success": false, "message": "...", "error": "SOME_ERROR_CODE" }
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static ApiResponse<Void> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode);
    }
}
