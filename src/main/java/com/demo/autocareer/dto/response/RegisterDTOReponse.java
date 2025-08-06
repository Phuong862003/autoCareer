package com.demo.autocareer.dto.response;

import java.io.Serializable;

import com.demo.autocareer.dto.DistrictDTO;
import com.demo.autocareer.dto.RoleDTO;
import com.demo.autocareer.model.District;
import com.demo.autocareer.model.Role;
import com.demo.autocareer.model.enums.AccountStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
public class RegisterDTOReponse implements Serializable {
    private Long id;
    private String email;
    private String username;
    private String phoneNumber;
    private Long managerId;
    AccountStatus accountStatus;
    boolean enabled;
    RoleDTO role;
    DistrictDTO district;
}
