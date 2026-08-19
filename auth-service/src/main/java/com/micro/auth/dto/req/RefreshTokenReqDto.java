package com.micro.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenReqDto {
    @NotNull
    @NotBlank
    private String refreshToken;
}
