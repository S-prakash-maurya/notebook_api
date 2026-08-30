package com.micro.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class LessonReqDto {
    @NotNull
    private UUID subjectId;

    @NotBlank
    private String name;
}
