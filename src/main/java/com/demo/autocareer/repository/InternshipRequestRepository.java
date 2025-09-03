package com.demo.autocareer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.InternshipRequest;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.enums.StatusRequest;


@Repository
public interface InternshipRequestRepository extends JpaRepository<InternshipRequest, Long>, JpaSpecificationExecutor<InternshipRequest>{
     // Tổng số yêu cầu theo công ty
    @Query("SELECT COUNT(r) FROM InternshipRequest r WHERE r.company.id = :companyId")
    Long countTotalRequests(@Param("companyId") Long companyId);

    // Đếm theo trạng thái (pending, approved, completed...)
    Long countAllByCompany_IdAndStatusRequest(Long companyId, StatusRequest statusRequest);

    // Tổng số trường gửi yêu cầu tới công ty
    @Query("SELECT COUNT(DISTINCT r.university.id) FROM InternshipRequest r WHERE r.company.id = :companyId")
    Long countDistinctUniversity(@Param("companyId") Long companyId);

    // Thống kê theo tháng cho từng công ty
    @Query("SELECT MONTH(r.createdAt), COUNT(r) " +
           "FROM InternshipRequest r " +
           "WHERE r.company.id = :companyId AND YEAR(r.createdAt) = :year " +
           "GROUP BY MONTH(r.createdAt) " +
           "ORDER BY MONTH(r.createdAt)")
    List<Object[]> countRequestsByMonth(@Param("companyId") Long companyId, @Param("year") int year);

    Optional<InternshipRequest> findByCompany_IdAndUniversity(Long companyId, Organization university);

       // Tổng số yêu cầu theo truong
    @Query("SELECT COUNT(r) FROM InternshipRequest r WHERE r.university.id = :universityId")
    Long countUniTotalRequests(@Param("universityId") Long universityId);

    // Đếm theo trạng thái (pending, approved, completed...)
    Long countUniAllByUniversity_IdAndStatusRequest(Long universityId, StatusRequest statusRequest);

    // Tổng số trường gửi yêu cầu tới công ty
    @Query("SELECT COUNT(DISTINCT r.company.id) FROM InternshipRequest r WHERE r.university.id = :universityId")
    Long countDistinctCompany(@Param("universityId") Long universityId);

    // Thống kê theo tháng cho từng công ty
    @Query("SELECT MONTH(r.createdAt), COUNT(r) " +
           "FROM InternshipRequest r " +
           "WHERE r.university.id = :universityId AND YEAR(r.createdAt) = :year " +
           "GROUP BY MONTH(r.createdAt) " +
           "ORDER BY MONTH(r.createdAt)")
    List<Object[]> countUniRequestsByMonth(@Param("universityId") Long universityId, @Param("year") int year);
}
