package com.demo.autocareer.service;

import java.io.ByteArrayInputStream;

import org.springframework.data.domain.Pageable;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.InternSemesterDTOResponse;
import com.demo.autocareer.model.Organization;

public interface InternSemesterService {
    Organization getOrgFromToken();
    BasePageResponse<InternSemesterDTOResponse> getList(BaseFilterRequest request, Pageable pageable);
    ByteArrayInputStream exportInternSemesterByFilter(BaseFilterRequest request);
}
