package com.demo.autocareer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.InternshipAssignment;
import com.demo.autocareer.model.InternshipRequest;

@Repository
public interface InternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Long>, JpaSpecificationExecutor<InternshipAssignment>{
    
}
