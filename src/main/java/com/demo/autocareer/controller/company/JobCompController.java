package com.demo.autocareer.controller.company;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.autocareer.dto.request.JobDTORequest;
import com.demo.autocareer.dto.response.ResponseData;
import com.demo.autocareer.service.CompanyService;
import com.demo.autocareer.service.JobDetailService;

import org.springframework.web.bind.annotation.RequestParam;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.dto.request.BasePageRequest;
import com.demo.autocareer.dto.response.ApplyJobDTOResponse;
import com.demo.autocareer.dto.response.BasePageResponse;
import com.demo.autocareer.dto.response.JobDTOResponse;


@RestController
@RequestMapping("/api/company")
public class JobCompController {
    private final JobDetailService jobDetailService;

    private final CompanyService companyService;

    public JobCompController(JobDetailService jobDetailService, CompanyService companyService) {
        this.jobDetailService = jobDetailService;
        this.companyService = companyService;
    }

    @PostMapping("/create-job")
    public ResponseData<?> createJob(@RequestBody JobDTORequest request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("CREATE SUCCESS")
                .data(jobDetailService.createJob(request))
                .build();
    }

    @PutMapping("/update-job/{id}")
    public ResponseData<?> updateJob(@PathVariable Long id,@RequestBody JobDTORequest request) {
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("UPDATE SUCCESS")
                .data(jobDetailService.updateJob(id, request))
                .build();
    }

    @DeleteMapping("/delete-job/{id}")
    public ResponseData<?> deleteJob(@PathVariable Long id) {
        jobDetailService.deleteJob(id);
        return ResponseData.builder()
                .status(HttpStatus.OK.value())
                .message("DELETE SUCCESS")
                .data(null)
                .build();
    } 

    @GetMapping("/list-job")
    public ResponseData<BasePageResponse<JobDTOResponse>> getJobComp(@ModelAttribute BaseFilterRequest request,@ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();

        BasePageResponse<JobDTOResponse> result = companyService.getAllJobs(request, pageable);

        return ResponseData.<BasePageResponse<JobDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET JOBS SUCCESS")
                .data(result)
                .build();
    }
    
    @GetMapping("/list-applications")
    public ResponseData<BasePageResponse<ApplyJobDTOResponse>> getApplication(
                                    @ModelAttribute BaseFilterRequest baseFilterRequest,
                                    @ModelAttribute BasePageRequest basePageRequest) {
        Pageable pageable = basePageRequest.toPageable();
        BasePageResponse<ApplyJobDTOResponse> result = companyService.getApplications(baseFilterRequest, pageable);
        return ResponseData.<BasePageResponse<ApplyJobDTOResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("GET APPLICATION SUCCESS")
                .data(result)
                .build();
    }
}
