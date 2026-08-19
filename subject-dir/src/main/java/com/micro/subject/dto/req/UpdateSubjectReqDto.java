package com.micro.subject.dto.req;

import com.micro.subject.validation.ValidHexColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Update DTO intentionally does NOT include id, userId, createdAt, or
 * deletedAt - those fields are never accepted from the client. The
 * service layer only ever reads title/color/icon off this DTO and sets
 * updatedAt itself.
 */
@Data
public class UpdateSubjectReqDto {

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
