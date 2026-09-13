package com.micro.auth.dto.res;

import com.micro.auth.dto.req.NotePointRequest;
import com.micro.auth.enums.NoteType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponse {

    private NoteType type;

    private String content;

    private List<NotePointRequest> points;
}
