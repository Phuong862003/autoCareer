package com.demo.autocareer.dto;

import java.io.Serializable;
import java.util.Date;

import com.demo.autocareer.model.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
// @Builder
public class InternshipAssignmentExcelDTO implements Serializable{
    private String studentCode;
    private String name;
    private String email;
    private String phoneNumber;
    private Integer graduatedYear;
    private Gender gender;
    private Date dob;
    private String facultyName;
    private String skill;

    public InternshipAssignmentExcelDTO(){}

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

    public void setGraduatedYear(int graduatedYear){
        this.graduatedYear = graduatedYear;
    }

    public void setGender(Gender gender){
        this.gender = gender;
    }

    public void setFacultyName(String facultyName){
        this.facultyName = facultyName;
    }

    public void setSkill(String skill){
        this.skill = skill;
    }

    public void setGraduatedYear(Integer graduatedYear) {
        this.graduatedYear = graduatedYear;
    }

    public String getName() {
        return name;
    }

    public String getStudentCode(){
        return studentCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getGraduatedYear() {
        return graduatedYear;
    }

    public Date getDob() {
        return dob;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getSkill() {
        return skill;
    }

    public Gender getGender() {
        return gender;
    }
}

