package com.demo.autocareer.dto.request;

import com.demo.autocareer.model.enums.AccountStatus;

public class UserDTORequest {
    private String email;
    private String username;
    private String phoneNumber;
    private Long managerId;
    private AccountStatus accountStatus;
    private Long roleId;
    private Long districtId;

    public String getUsername(){
        return username;
    }

    public Long getRoleId(){
        return roleId;
    }

    public String getEmail(){
        return email;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public Long getDistrictId(){
        return districtId;
    }

    public Long getManagerId(){
        return managerId;
    }
}
