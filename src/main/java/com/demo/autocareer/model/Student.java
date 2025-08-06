package com.demo.autocareer.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.demo.autocareer.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name = "student")
public class Student extends BaseEntity<Long> implements Serializable{

    @Column(name = "name")
    String name;

    @Column(name = "student_code", unique = true)
    String studentCode;

    @Column(name = "email", unique = true)
    String email;

    @Column(name = "phone_number", unique = true)
    String phoneNumber;

    @Column(name = "graduated_year")
    Integer graduatedYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;


    @Column(name = "attachment")
    String attachment;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    Date dob;

    @Column(name = "logo_img")
    String logo_img;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    // @JsonIgnore
    District district; 

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sub_field_id", nullable = true)
    // @JsonIgnore
    SubField subField;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_faculty_id", nullable = true)
    // @JsonIgnore
    OrganizationFaculty organizationFaculty;

    @OneToOne(mappedBy="student", fetch = FetchType.LAZY, orphanRemoval = true)
    private InternDeclareRequest internDeclareRequest;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<StudentSkill> studentSkills;

    public void setName(String name){
        this.name = name;
    }

    public void setStudentCode(String studentCode){
        this.studentCode = studentCode;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setDob(Date dob){
        this.dob = dob;
    }

    public void setGraduatedYear(Integer graduatedYear){
        this.graduatedYear = graduatedYear;
    }

    public void setGender(Gender gender){
        this.gender = gender;
    }

    public void setDistrict(District district){
        this.district = district;
    }

    public void setSubField(SubField subField){
        this.subField = subField;
    }

    public void setOrganizationFaculty(OrganizationFaculty organizationFaculty){
        this.organizationFaculty = organizationFaculty;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAttachment(String attachment){
        this.attachment = attachment;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getDob() {
        return dob;
    }

    public Integer getGraduatedYear() {
        return graduatedYear;
    }

    public String getAttachment(){
        return attachment;
    }

    public Gender getGender() {
        return gender;
    }

    public District getDistrict() {
        return district;
    }

    public SubField getSubField() {
        return subField;
    }

    public OrganizationFaculty getOrganizationFaculty() {
        return organizationFaculty;
    }

    public String getLogo_img() {
        return logo_img;
    }

    public void setLogo_img(String logo_img) {
        this.logo_img = logo_img;
    }

    public User getUser() {
        return user;
    }

    public InternDeclareRequest getInternDeclareRequest() {
        return internDeclareRequest;
    }

    public void setInternDeclareRequest(InternDeclareRequest internDeclareRequest) {
        this.internDeclareRequest = internDeclareRequest;
    }

    public List<StudentSkill> getStudentSkills() {
        return studentSkills;
    }

    public void setStudentSkills(List<StudentSkill> studentSkills) {
        this.studentSkills = studentSkills;
    }


}
