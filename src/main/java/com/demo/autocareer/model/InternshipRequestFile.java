package com.demo.autocareer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.demo.autocareer.model.enums.StatusRequestFile;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "internship_request_file")
public class InternshipRequestFile extends BaseEntity<Long> implements Serializable{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_request_id", nullable = false)
    private InternshipRequest internshipRequest;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "uploaded_by")
    private String uploadedBy;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private StatusRequestFile status;

    public InternshipRequest getInternshipRequest(){
        return internshipRequest;
    }

    public String getFilePath(){
        return filePath;
    }

    public String getOriginalFileName(){
        return originalFileName;
    }

    public String getUploadedBy(){
        return uploadedBy;
    }

    public StatusRequestFile getStatus(){
        return status;
    }

    public void setInternshipRequest(InternshipRequest internshipRequest){
        this.internshipRequest = internshipRequest;
    }

    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    public void setOriginalFileName(String originalFileName){
        this.originalFileName = originalFileName;
    }

    public void setUploadedBy(String uploadedBy){
        this.uploadedBy = uploadedBy;
    }

    public void setStatus(StatusRequestFile status){
        this.status = status;
    }
}
