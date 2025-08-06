package com.demo.autocareer.model;

import java.io.Serializable;
import java.time.LocalDate;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.demo.autocareer.model.enums.StatusIntern;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "interndeclarerequest")
public class InternDeclareRequest extends BaseEntity<Long> implements Serializable{
    @Column(name = "semester")
    private String semester;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_address")
    private String companyAddress;

    private String position;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_intern", nullable = false)
    private StatusIntern statusIntern;

    @ManyToOne
    @JoinColumn(name = "jobdetail_id", referencedColumnName = "id")
    private Job job;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", referencedColumnName = "id", unique = true)
    private Student student;

    @Column(name="note")
    private String note;

    @Column(name="approvedBy")
    private String approvedBy;

    public String getNote() {
        return note;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy){
        this.approvedBy = approvedBy;
    }

    public void setNote(String note){
        this.note = note;
    }

    public String getSemester() {
        return semester;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getPosition() {
        return position;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public StatusIntern getStatusIntern() {
        return statusIntern;
    }

    public Job getJobDetail() {
        return job;
    }

    public Student getStudent() {
        return student;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public void setStatusIntern(StatusIntern statusIntern) {
        this.statusIntern = statusIntern;
    }

    public void setJobDetail(Job jobDetail) {
        this.job = jobDetail;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
