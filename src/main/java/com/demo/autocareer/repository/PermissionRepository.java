package com.demo.autocareer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.Permission;

@Repository
public interface  PermissionRepository extends JpaRepository<Permission, Long>{
    
}
