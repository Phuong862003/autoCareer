package com.demo.autocareer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.autocareer.model.Province;

@Repository
public interface  ProvinceRepository extends JpaRepository<Province, Long>{
    
}
