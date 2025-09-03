package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.util.Date;

import com.demo.autocareer.model.enums.StatusRequestFile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternRequestFileDTOResponse implements Serializable{
    private Long id;
    private InternshipRequestDTOResponse internshipRequest;
    private String originalFileName;
    private String filePath;
    private StatusRequestFile status;
    private Date createdAt;
    private Date updatedAt;
}
