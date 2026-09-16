package com.micro.auth.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteItemResponse {

    private UUID id;

    private String text;

    @JsonProperty("isCompleted")
    private Boolean completed;
}
