package com.demo.autocareer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.SubField;
import com.demo.autocareer.model.enums.JobStatus;

@Repository
public interface JobDetailRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job>{
    Page<Job> findByOrganization(Organization organization, Pageable pageable);

    long countAllByOrganization_Id(Long organizationId);

    long countByOrganization_IdAndJobStatus(Long organizationId, JobStatus status);

    @Query("""
       SELECT MONTH(j.createdAt) as month, COUNT(j) as count
       FROM Job j
       WHERE j.organization.id = :organizationId
         AND j.organization.organizationType = com.demo.autocareer.model.enums.OrganizationType.COMPANY
       GROUP BY MONTH(j.createdAt)
       ORDER BY month
       """)
    List<Object[]> countJobsByMonth(@Param("organizationId") Long organizationId);

    Optional<Job> findByIdAndOrganization(Long id, Organization organization);

    @Query("SELECT sf FROM Job j JOIN j.subFields sf WHERE j.id = :jobId")
    List<SubField> findSubFieldsByJobId(@Param("jobId") Long jobId);




}
