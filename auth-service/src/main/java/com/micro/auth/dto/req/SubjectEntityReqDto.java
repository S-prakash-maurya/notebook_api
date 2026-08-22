package com.micro.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class SubjectEntityReqDto {
    @JsonIgnore
    private UUID userId;

    @NotBlank
    private String title;

    private String color;

    private String icon;
}
