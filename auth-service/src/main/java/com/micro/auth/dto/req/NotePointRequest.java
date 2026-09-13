package com.micro.auth.dto.req;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotePointRequest {

    private String text;

    private Boolean completed;
}

