package com.demo.autocareer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.OrganizationFaculty;

@Repository
public interface  OrganizationFacultyRepository extends JpaRepository<OrganizationFaculty, Long>{
    Optional<OrganizationFaculty> findById(Long id);
}
