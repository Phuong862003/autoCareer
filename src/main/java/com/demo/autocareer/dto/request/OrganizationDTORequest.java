package com.demo.autocareer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrganizationDTORequest{
    private Long id;
    private String organizationName;
    private int foundedYear;
    private int member;
    private String websiteUrl;
    private String description;
    private String logo_img;
    private String banner_img;
    // User user;
}
