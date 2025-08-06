package com.demo.autocareer.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class SendInternFileDTORequest {
    private Long internshipRequestId;
    private MultipartFile file;
}
