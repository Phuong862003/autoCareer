package com.demo.autocareer.dto.request;

import com.demo.autocareer.dto.SemesterDTO;
import com.demo.autocareer.dto.StudentDTO;
import com.demo.autocareer.model.enums.StatusInternSemester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternSemesterDTORequest {
    private StudentDTO student;
    private SemesterDTO semester;
    private StatusInternSemester status;
}
