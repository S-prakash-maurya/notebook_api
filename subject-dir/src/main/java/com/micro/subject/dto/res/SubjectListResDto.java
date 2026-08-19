package com.micro.subject.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectListResDto {
    private List<SubjectResDto> data;
    private PaginationResDto pagination;
}
