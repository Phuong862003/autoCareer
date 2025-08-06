package com.demo.autocareer.model;

import java.io.Serializable;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.demo.autocareer.model.enums.ApplyJobStatus;
import com.demo.autocareer.model.enums.JobStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "applyjob")
public class ApplyJob extends BaseEntity<Long> implements Serializable{
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ApplyJobStatus applyJobStatus;

    @Column(name = "attachment")
    String attachment;

    @Column(name = "cover_letter")
    String cover_letter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    @JsonIgnore
    Job job;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    Student student;

    public void setJobStatus(ApplyJobStatus applyJobStatus){
        this.applyJobStatus = applyJobStatus;
    }

    public void setAttachment(String attachment){
        this.attachment = attachment;
    }

    public void setJob(Job job){
        this.job = job;
    }

    public void setStudent(Student student){
        this.student = student;
    }

    public ApplyJobStatus getJobStatus(){
        return applyJobStatus;
    }

    public Job getJob(){
        return job;
    }

    public Student getStudent(){
        return student;
    }

    public String getAttachment(){
        return attachment;
    }

    public String getCoverLetter(){
        return cover_letter;
    }
}
