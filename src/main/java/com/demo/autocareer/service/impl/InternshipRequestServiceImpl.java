package com.demo.autocareer.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.autocareer.dto.request.InternshipRequestDTORequest;
import com.demo.autocareer.dto.request.JobDTORequest;
import com.demo.autocareer.dto.response.InternRequestStaticDTOResponse;
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
    public Organization getFromToken() {
        String email = jwtUtil.getCurrentUserEmail(); 
        return organizationRepository.findByUserEmail(email)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.UNIVERSITY_NOT_FOUND));
    }

    @Override
    public InternshipRequestDTOResponse createInternshipRequest(InternshipRequestDTORequest request) {

        Organization uni = getFromToken();
        Organization company = organizationRepository.findById(request.getCompanyId())
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.COMPANY_NOT_FOUND));
        
        InternshipRequest intern = internshipMapper.mapRequestToEntity(request);
        intern.setUniversity(uni);
        intern.setCompany(company);
        intern.setStatusRequest(StatusRequest.PENDING);

        internshipRequestRepository.save(intern);
        return internshipMapper.mapEntityToResponse(intern);
    }

    @Override
    public InternRequestStaticDTOResponse getInternRequestStatic(){
        Organization company = getFromToken();
        Long companyId = company.getId();
        Long totalRequest = internshipRequestRepository.countTotalRequests(companyId);
        Long approvedRequest = internshipRequestRepository.countAllByCompany_IdAndStatusRequest(companyId, StatusRequest.APPROVED);
        Long completedRequest = internshipRequestRepository.countAllByCompany_IdAndStatusRequest(companyId, StatusRequest.COMPLETED);
        Long totalUni = internshipRequestRepository.countDistinctUniversity(companyId);

        List<Object[]> rawMonthly = internshipRequestRepository.countRequestsByMonth(companyId, LocalDate.now().getYear());
        Map<Integer, Long> requestsByMonth = rawMonthly.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));
        return InternRequestStaticDTOResponse.builder()
                .totalInternRequest(totalRequest)
                .approvedRequest(approvedRequest)
                .completedRequest(completedRequest)
                .totalUni(totalUni)
                .monthlyRequestStats(requestsByMonth)
                .build();
    }

    @Override
    public InternshipRequestDTOResponse getDetail(Long id){
        Organization company = getFromToken();
        InternshipRequest intern = internshipRequestRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_NOT_FOUND));
        
        return internshipMapper.mapEntityToResponse(intern);
    }

    @Override
    public void deletedInternRequest(Long id){
        InternshipRequest intern = internshipRequestRepository.findById(id)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_NOT_FOUND));
        internshipRequestRepository.delete(intern);
    }


    @Override
    public InternshipRequestDTOResponse getDetailInternRequest(Long companyId){
        Organization uni = getFromToken();
        InternshipRequest intern = internshipRequestRepository.findByCompany_IdAndUniversity(companyId, uni)
            .orElseThrow(() -> ExceptionUtil.fromErrorCode(ErrorCode.INTERN_REQUEST_NOT_FOUND));
        
        return internshipMapper.mapEntityToResponse(intern);
    }

    @Override
    public InternRequestStaticDTOResponse getUniInternRequestStatic(){
        Organization uni = getFromToken();
        Long uniId = uni.getId();
        Long totalRequest = internshipRequestRepository.countUniTotalRequests(uniId);
        Long approvedRequest = internshipRequestRepository.countUniAllByUniversity_IdAndStatusRequest(uniId, StatusRequest.APPROVED);
        Long completedRequest = internshipRequestRepository.countUniAllByUniversity_IdAndStatusRequest(uniId, StatusRequest.COMPLETED);
        Long totalUni = internshipRequestRepository.countDistinctCompany(uniId);

        List<Object[]> rawMonthly = internshipRequestRepository.countRequestsByMonth(uniId, LocalDate.now().getYear());
        Map<Integer, Long> requestsByMonth = rawMonthly.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));
        return InternRequestStaticDTOResponse.builder()
                .totalInternRequest(totalRequest)
                .approvedRequest(approvedRequest)
                .completedRequest(completedRequest)
                .totalUni(totalUni)
                .monthlyRequestStats(requestsByMonth)
                .build();
    }
}
