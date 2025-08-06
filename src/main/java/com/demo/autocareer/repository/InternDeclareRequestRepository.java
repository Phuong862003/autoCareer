package com.demo.autocareer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.InternDeclareRequest;

@Repository
public interface  InternDeclareRequestRepository extends JpaRepository<InternDeclareRequest, Long>{
    Optional<InternDeclareRequest> findTopByStudentIdOrderByCreatedAtDesc(Long studentId);
}
