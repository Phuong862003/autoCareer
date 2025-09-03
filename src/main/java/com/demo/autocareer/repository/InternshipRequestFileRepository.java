package com.demo.autocareer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.InternshipRequestFile;

@Repository
public interface InternshipRequestFileRepository extends JpaRepository<InternshipRequestFile, Long>, JpaSpecificationExecutor<InternshipRequestFile>{
    List<InternshipRequestFile> findByInternshipRequestId(Long requestId);
}
