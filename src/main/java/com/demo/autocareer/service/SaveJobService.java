package com.demo.autocareer.service;

import org.springframework.data.domain.Pageable;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.SaveJobDTORequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.SaveJobDTOResponse;
import com.demo.autocareer.model.Student;

public interface  SaveJobService {
    Student getStudentFromToken();
    SaveJobDTOResponse saveJob(SaveJobDTORequest request);
    BasePageResponse<SaveJobDTOResponse> getList(BaseFilterRequest request, Pageable pageable);
    void deleteSaveJob(Long id);
}
