package com.micro.subject.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResDto {
    private int page;
    private int limit;
    private long total;
    private int totalPages;
}
