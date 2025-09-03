package com.demo.autocareer.service;

import com.demo.autocareer.dto.request.InternshipRequestDTORequest;
import com.demo.autocareer.dto.response.InternRequestStaticDTOResponse;
import com.demo.autocareer.dto.response.InternshipRequestDTOResponse;
import com.demo.autocareer.model.Organization;

public interface  InternshipRequestService {
    Organization getFromToken();
    InternshipRequestDTOResponse createInternshipRequest(InternshipRequestDTORequest request);
    InternRequestStaticDTOResponse getInternRequestStatic();
    InternshipRequestDTOResponse getDetail(Long id);
    void deletedInternRequest(Long id);
    InternshipRequestDTOResponse getDetailInternRequest(Long companyId);
    InternRequestStaticDTOResponse getUniInternRequestStatic();
}
