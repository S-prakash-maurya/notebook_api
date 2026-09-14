package com.micro.auth.dto.req;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotePointRequest {

    private UUID id;

    private String text;

    private Boolean completed;
}

