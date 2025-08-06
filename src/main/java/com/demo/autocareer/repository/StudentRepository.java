package com.demo.autocareer.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.OrganizationFaculty;
import com.demo.autocareer.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student>{
    boolean existsByStudentCode(String studentCode);
    Optional<Student> findByUserEmail(String email);
    Page<Student> findByOrganizationFaculty(OrganizationFaculty organizationFaculty, Pageable pageable);
    @EntityGraph(attributePaths = {"district", "subField", "organizationFaculty"})
    Optional<Student> findById(Long id);
    Optional<Student> findByStudentCode(String studentCode);
}
