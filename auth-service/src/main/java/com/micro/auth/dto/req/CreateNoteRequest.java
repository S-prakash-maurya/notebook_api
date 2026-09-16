package com.micro.auth.dto.req;

import com.micro.auth.enums.NoteType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoteRequest {
    private NoteType type;

    private String title;

    private String content;

    private String colorValue;

    private Boolean isPinned;

    private Boolean isFavorite;

    private List<NotePointRequest> points;
}
