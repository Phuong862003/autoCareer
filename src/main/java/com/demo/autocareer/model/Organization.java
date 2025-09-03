package com.demo.autocareer.model;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.demo.autocareer.model.enums.OrganizationType;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
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
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "organization")
public class Organization extends BaseEntity<Long> implements Serializable{
    @Column(name = "organization_name")
    String organizationName;

    @Column(name = "founded_year")
    int foundedYear;

    @Column(name = "member")
    int member;

    @Column(name = "website_url")
    String websiteUrl;

    @Column(name = "logo_img")
    String logo_img;

    @Column(name = "banner_img")
    String banner_img;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name="address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type")
    OrganizationType organizationType;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

    @OneToMany(mappedBy = "company")
    private List<InternshipRequest> internshipRequestsAsCompany;

    @OneToMany(mappedBy = "university")
    private List<InternshipRequest> internshipRequestsAsUniversity;


    public void setOrganizationName(String organizationName){
        this.organizationName = organizationName;
    }

    public void setOrganizationType(OrganizationType organizationType){
        this.organizationType = organizationType;
    }

    public List<InternshipRequest> getInternshipRequestsAsCompany(){
        return internshipRequestsAsCompany;
    }
}
