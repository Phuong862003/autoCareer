package com.demo.autocareer.dto.request;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseFilterRequest {
    private String keyword;

    private String enumValue;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAtFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAtTo;

    private String sortDirection;

    private Long districtId;

    private Long provinceId;

    public String getKeyword() {
        return keyword;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public Date getCreatedAtFrom(){
        return createdAtFrom;
    }

    public Date getCreatedAtTo(){
        return createdAtTo;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public Long getProvinceId() {
        return provinceId;
    }
}
