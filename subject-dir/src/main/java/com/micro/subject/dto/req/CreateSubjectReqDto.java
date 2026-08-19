package com.micro.subject.dto.req;

import com.micro.subject.validation.ValidHexColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSubjectReqDto {

    @NotBlank(message = "title is required")
    @Size(max = 120, message = "title must be at most 120 characters")
    private String title;

    @NotBlank(message = "color is required")
    @ValidHexColor
    private String color;

    @NotBlank(message = "icon is required")
    @Size(max = 60, message = "icon must be at most 60 characters")
    private String icon;
}
