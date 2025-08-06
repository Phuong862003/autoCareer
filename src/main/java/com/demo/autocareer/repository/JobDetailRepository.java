package com.demo.autocareer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Organization;

@Repository
public interface JobDetailRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job>{
    Page<Job> findByOrganization(Organization organization, Pageable pageable);

}
