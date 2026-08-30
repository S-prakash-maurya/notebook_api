package com.micro.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class QuestionAnswerReqDto {
//    @NotNull
//    private UUID subjectId;

    @NotNull
    private UUID lessonId;

    @NotBlank
    private String question;

    private String answerText;

    private String answerImageUrl;
}
