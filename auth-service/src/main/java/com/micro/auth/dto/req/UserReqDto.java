package com.micro.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserReqDto {
    @Email
    @NotNull
    @NotBlank
    @Size(min = 6, max = 60)
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 6, max = 60)
    private String password;

    @NotNull
    @NotBlank
    @Size(min = 5, max = 60)
    private String name;
}
