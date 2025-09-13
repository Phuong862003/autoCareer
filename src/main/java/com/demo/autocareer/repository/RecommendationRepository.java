package com.demo.autocareer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.RecommendationJob;

@Repository
public interface RecommendationRepository extends JpaRepository<RecommendationJob, Long>, JpaSpecificationExecutor<RecommendationJob>{
    Page<RecommendationJob> findByStudentId(Long studentId, Pageable pageable);
}
