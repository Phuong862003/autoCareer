package com.demo.autocareer.dto.response;

import com.demo.autocareer.dto.StudentDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveJobDTOResponse {
    private Long id;
    private JobDTOResponse job;
}
