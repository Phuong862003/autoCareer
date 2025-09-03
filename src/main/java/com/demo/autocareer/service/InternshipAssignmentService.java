package com.demo.autocareer.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternRequestFileDTOResponse;
import com.demo.autocareer.dto.response.InternshipAssignDTOResponse;
import com.demo.autocareer.model.Organization;

public interface InternshipAssignmentService {
    Organization getUniFromToken();
    void sendInternship(MultipartFile file, Long internshipId);
    BasePageResponse<InternshipAssignDTOResponse> getListInternship(Long internshipRequestId, BaseFilterRequest request, Pageable pageable);
    BasePageResponse<InternRequestFileDTOResponse> getListInternRequestFile(BaseFilterRequest request, Pageable pageable);
    BasePageResponse<InternRequestFileDTOResponse> getUniListInternRequestFile(BaseFilterRequest request, Pageable pageable);
    BasePageResponse<InternshipAssignDTOResponse> getUniListInternship(Long internshipRequestId, BaseFilterRequest request, Pageable pageable);
}
