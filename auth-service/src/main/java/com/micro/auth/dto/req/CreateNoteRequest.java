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

    private String content;

    private List<NotePointRequest> points;
}
