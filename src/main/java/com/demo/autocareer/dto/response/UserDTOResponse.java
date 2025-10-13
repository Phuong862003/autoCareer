package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.util.Date;

import com.demo.autocareer.dto.DistrictDTO;
import com.demo.autocareer.dto.RoleDTO;
import com.demo.autocareer.model.enums.AccountStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTOResponse implements Serializable{
    private Long id;
    private String email;
    private String username;
    private String phoneNumber;
    private boolean enabled;
    private Long managerId;
    private AccountStatus accountStatus;
    private RoleDTO role;
    private DistrictDTO district;
    private Date createdAt;
}
