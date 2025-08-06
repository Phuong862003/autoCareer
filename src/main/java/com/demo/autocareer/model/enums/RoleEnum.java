package com.demo.autocareer.model.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN(1, "ADMIN"),
    ADMIN_COM(2, "ADMIN_COMPANY"),
    ADMIN_UNI(3, "ADMIN_UNIVERSITY"),
    STUDENT(4, "STUDENT");

    RoleEnum(Integer roleId, String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    private final Integer roleId;
    private final String roleName;
}
