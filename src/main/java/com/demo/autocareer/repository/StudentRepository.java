package com.demo.autocareer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.Organization;
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
    @Query("SELECT s FROM Student s " +
           "WHERE s.id = :studentId " +
           "AND s.organizationFaculty.organization = :organization")
    Optional<Student> findByIdAndOrganization(@Param("studentId") Long studentId, @Param("organization") Organization organization);

    @Query("""
        SELECT 
            COUNT(s.id) as totalStudent,
            SUM(CASE WHEN i.statusIntern = 'APPROVED' THEN 1 ELSE 0 END) as totalApproved,
            SUM(CASE WHEN i.statusIntern = 'WAITING' THEN 1 ELSE 0 END) as totalWaiting,
            SUM(CASE WHEN i.statusIntern IS NULL OR i.statusIntern = 'REJECTED' THEN 1 ELSE 0 END) as totalNotYet
        FROM Student s
        LEFT JOIN s.internDeclareRequest i
        WHERE s.organizationFaculty.organization.id = :organizationId
    """)
    List<Object[]> getStudentStatics(@Param("organizationId") Long organizationId);

    @Query("""
        SELECT MONTH(i.createdAt), COUNT(i.id)
        FROM InternDeclareRequest i
        WHERE i.statusIntern = 'APPROVED'
          AND i.student.organizationFaculty.organization.id = :organizationId
        GROUP BY MONTH(i.createdAt)
        ORDER BY MONTH(i.createdAt)
    """)
    List<Object[]> countStudentByMonth(@Param("organizationId") Long organizationId);
}
