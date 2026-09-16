package com.micro.auth.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.micro.auth.enums.NoteType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponse {
    private UUID id;

    private String title;

    private String content;

    private NoteType type;

    private String colorValue;

    private List<NoteItemResponse> items;

    @JsonProperty("isPinned")
    private Boolean pinned;

    @JsonProperty("isFavorite")
    private Boolean favorite;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
