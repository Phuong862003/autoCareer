package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.demo.autocareer.dto.DistrictDTO;
import com.demo.autocareer.dto.OrganizationFacultyDTO;
import com.demo.autocareer.dto.StudentSkillDTO;
import com.demo.autocareer.dto.SubFieldDTO;
import com.demo.autocareer.model.enums.Gender;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentDTOResponse implements Serializable {
    private Long id;
    private String name;
    private String studentCode;
    private String email;
    private String phoneNumber;
    private Date dob;
    private int graduatedYear;
    private Gender gender;
    private String attachment;
    private String logo_img;
    private DistrictDTO district;
    private OrganizationFacultyDTO organizationFaculty;
    private SubFieldDTO subField; 
    private List<StudentSkillDTO> studentSkills;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStudentCode() { return studentCode; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Date getDob() { return dob; }
    public int getGraduatedYear() { return graduatedYear; }
    public Gender getGender() { return gender; }
    public String getAttachment() { return attachment; }
    public String getLogo_img() { return logo_img; }
    public DistrictDTO getDistrict() { return district; }
    public OrganizationFacultyDTO getOrganizationFaculty() { return organizationFaculty; }
    public SubFieldDTO getSubField() { return subField; }
    public List<StudentSkillDTO> getStudentSkills() { return studentSkills; }
}
