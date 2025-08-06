package com.demo.autocareer.service;

import com.demo.autocareer.dto.request.InternshipRequestDTORequest;
import com.demo.autocareer.dto.response.InternshipRequestDTOResponse;
import com.demo.autocareer.model.Organization;

public interface  InternshipRequestService {
    Organization getUniFromToken();
    InternshipRequestDTOResponse createInternshipRequest(InternshipRequestDTORequest request);
}
