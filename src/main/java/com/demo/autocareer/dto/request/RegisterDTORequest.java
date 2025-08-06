package com.demo.autocareer.dto.request;

import java.io.Serializable;

import com.demo.autocareer.model.District;
import com.demo.autocareer.model.Role;
import com.demo.autocareer.model.enums.AccountStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RegisterDTORequest implements Serializable {
    @NotBlank(message = "Username must not blank")
    String username;

    @NotBlank(message = "Email must not blank")
    String email;

    @NotBlank(message = "Password must not blank")
    String password;

    @NotBlank(message = "Phone must not blank")
    @Pattern(regexp = "^0\\d{9}$")
    String phoneNumber;

    Long managerId;

    District district;

    @NotNull(message = "Role must not be null")
    Role role;

    AccountStatus accountStatus;

    public String getPassword() {
        return password;
    }

    public RegisterDTORequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public Long getManagerId(){
        return managerId;
    }

    public District getDistrict(){
        return district;
    }
}