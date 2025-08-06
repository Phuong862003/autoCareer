package com.demo.autocareer.dto;

import java.io.Serializable;
import java.util.Date;

import com.demo.autocareer.model.enums.Gender;

import lombok.Data;


public class StudentDTO implements Serializable{
    private String name;
    private String studentCode;
    private String email;
    private String phoneNumber;
    private Date dob;
    private Integer graduatedYear;
    private Gender gender;
    private Long districtId;
    private Long organizationFacultyId;
    private Long subFieldId;

    public StudentDTO(){}

    public String getName(){
        return name;
    }

    public String getStudentCode(){
        return studentCode;
    }

    public String getEmail(){
        return email;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public Date getDob(){
        return dob;
    }

    public int getGraduatedYear(){
        return graduatedYear;
    }

    public Gender getGender(){
        return gender;
    }

    public Long getDistrictId(){
        return districtId;
    }

    public Long getOrganizationFacultyId(){
        return organizationFacultyId;
    }

    public Long getSubFieldId(){
        return subFieldId;
    }

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

    public void setDistrictId(Long districtId){
        this.districtId = districtId;
    }

    public void setOrganizationFacultyId(Long organizationFacultyId){
        this.organizationFacultyId = organizationFacultyId;
    }

    public void setSubFieldId(Long subFieldId){
        this.subFieldId = subFieldId;
    }
}
