package com.demo.autocareer.model;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.demo.autocareer.model.enums.Gender;
import com.demo.autocareer.model.enums.JobStatus;
import com.demo.autocareer.model.enums.WorkingType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "jobdetail")
public class Job extends BaseEntity<Long> implements Serializable{
    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "requirement")
    String requirement;

    @Column(name = "welfare")
    String welfare;

    @Column(name = "salary_start")
    Integer salary_start;

    @Column(name = "salary_end")
    Integer salary_end;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_status")
    JobStatus jobStatus;

    @Column(name = "quality")
    int quality;
    
    @Column(name = "working_time")
    String working_time;

    @Enumerated(EnumType.STRING)
    @Column(name = "working_type")
    WorkingType workingType;

    @Column(name = "working_address")
    String working_address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    @JsonIgnore
    Organization organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "field_id", nullable = false)
    @JsonIgnore
    Field field;


    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobProvince> jobProvinces;

    @ManyToMany
    @JoinTable(
        name = "jobsubfield",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "sub_field_id")
    )
    private List<SubField> subFields;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getWelfare() {
        return welfare;
    }

    public void setWelfare(String welfare) {
        this.welfare = welfare;
    }

    public Integer getSalary_start() {
        return salary_start;
    }

    public void setSalary_start(Integer salary_start) {
        this.salary_start = salary_start;
    }

    public Integer getSalary_end() {
        return salary_end;
    }

    public void setSalary_end(Integer salary_end) {
        this.salary_end = salary_end;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getWorking_time() {
        return working_time;
    }

    public void setWorking_time(String working_time) {
        this.working_time = working_time;
    }

    public WorkingType getWorkingType() {
        return workingType;
    }

    public void setWorkingType(WorkingType workingType) {
        this.workingType = workingType;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setWorkingAddress(String working_address){
        this.working_address = working_address;
    }

    public String getWorkingAddress(){
        return working_address;
    }

    public void setField(Field field){
        this.field = field;
    }

    public Field getField(){
        return field;
    }

    public void setSubFields(List<SubField> subFields){
        this.subFields = subFields;
    }

    public List<SubField> getSubFields(){
        return subFields;
    }
}
