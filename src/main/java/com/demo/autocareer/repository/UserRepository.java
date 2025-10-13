package com.demo.autocareer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.demo.autocareer.model.Role;
import com.demo.autocareer.model.User;
import com.demo.autocareer.model.enums.AccountStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query(value = """
        SELECT YEAR(u.created_at) as year, MONTH(u.created_at) as month, COUNT(u.id) as cnt
        FROM user u
        GROUP BY YEAR(u.created_at), MONTH(u.created_at)
        ORDER BY year, month
        """, nativeQuery = true)
    List<Object[]> countUserByMonth();

    long countByAccountStatus(AccountStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    @Query(value = """
    SELECT 
        YEAR(u.created_at) AS year, 
        MONTH(u.created_at) AS month, 
        r.role_name AS roleName,
        COUNT(u.id) AS cnt
    FROM user u
    JOIN role r ON u.role_id = r.id
    WHERE u.created_at IS NOT NULL
    GROUP BY YEAR(u.created_at), MONTH(u.created_at), r.role_name
    ORDER BY year, month, roleName
    """, nativeQuery = true)
    List<Object[]> countUserByMonthAndRoleName();
}
