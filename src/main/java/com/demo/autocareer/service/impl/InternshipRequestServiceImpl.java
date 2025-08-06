package com.demo.autocareer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.request.InternshipRequestDTORequest;
import com.demo.autocareer.dto.request.JobDTORequest;
import com.demo.autocareer.dto.response.InternshipRequestDTOResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;
import com.demo.autocareer.exception.ErrorCode;
import com.demo.autocareer.mapper.InternshipMapper;
import com.demo.autocareer.model.InternshipRequest;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.enums.StatusRequest;
import com.demo.autocareer.repository.InternshipRequestRepository;
import com.demo.autocareer.repository.OrganizationRepository;
import com.demo.autocareer.service.InternshipRequestService;
import com.demo.autocareer.specification.BaseSpecification;
import com.demo.autocareer.utils.ExceptionUtil;
import com.demo.autocareer.utils.JwtUtil;

@Service
public class InternshipRequestServiceImpl implements InternshipRequestService{
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private InternshipRequestRepository internshipRequestRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private InternshipMapper internshipMapper;

    private final BaseSpecification<InternshipRequest> baseSpecification = new BaseSpecification<>();

    @Override
    public Organization getUniFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return organizationRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.UNIVERSITY_NOT_FOUND));
    }

    @Override
    public InternshipRequestDTOResponse createInternshipRequest(InternshipRequestDTORequest request) {

        Organization uni = getUniFromToken();
        Organization company = organizationRepository.findById(request.getCompanyId())
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
        
        InternshipRequest intern = internshipMapper.mapRequestToEntity(request);
        intern.setUniversity(uni);
        intern.setCompany(company);
        intern.setStatusRequest(StatusRequest.PENDING);

        internshipRequestRepository.save(intern);
        return internshipMapper.mapEntityToResponse(intern);
    }


}
