package com.demo.autocareer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.ApplyJob;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.ApplyJobStatus;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;


@Repository
public interface ApplyJobRepository extends JpaRepository<ApplyJob, Long>, JpaSpecificationExecutor<ApplyJob>{
    boolean existsByStudentAndJob(Student student, Job job);
    Page<ApplyJob> findByStudent(Student student, Pageable pageable);

    long countByJob_Organization_Id(Long organizationId);

    long countByJob_Organization_IdAndApplyJobStatus(Long organizationId, ApplyJobStatus status);

    @Query("""
            SELECT MONTH(a.createdAt) AS month, COUNT(a)
            FROM ApplyJob a
            WHERE a.job.organization.id = :organizationId
            AND a.job.organization.organizationType = com.demo.autocareer.model.enums.OrganizationType.COMPANY
            GROUP BY MONTH(a.createdAt)
            ORDER BY month
        """)
    List<Object[]> countApplicantsByMonth(@Param("organizationId") Long organizationId);

}
