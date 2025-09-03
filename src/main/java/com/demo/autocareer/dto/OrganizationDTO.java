package com.demo.autocareer.dto;

import java.io.Serializable;

import com.demo.autocareer.dto.response.UserDTOResponse;
import com.demo.autocareer.model.User;
import com.demo.autocareer.model.enums.OrganizationType;
import com.demo.autocareer.model.enums.StatusRequest;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrganizationDTO implements Serializable{
    private Long id;
    private String organizationName;
    private int foundedYear;
    private int member;
    private String websiteUrl;
    private String description;
    private String address;
    private String logo_img;
    private String banner_img;
    UserDTOResponse user;
    private StatusRequest statusRequest;

    public void setStatusRequest(StatusRequest statusRequest){
        this.statusRequest = statusRequest;
    }
}
