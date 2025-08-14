package com.demo.autocareer.specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.model.Job;
import com.demo.autocareer.model.JobProvince;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class BaseSpecification<T> {
    public Specification<T> build(BaseFilterRequest request,
                                    String keywordField,
                                    String enumField,
                                    String createdAtField,
                                    String districtFieldPath,
                                    String provinceFieldPath){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(StringUtils.hasText(request.getKeyword()) && keywordField != null){
                 String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(getPath(root, keywordField).as(String.class)), keyword));
            }

            if (StringUtils.hasText(request.getEnumValue()) && enumField != null) {
                predicates.add(cb.equal(getPath(root, enumField).as(String.class), request.getEnumValue()));
            }

            Map<String, String> filters = request.getFilters();
            if (filters != null && !filters.isEmpty()) {
                for (Map.Entry<String, String> entry : filters.entrySet()) {
                    if (StringUtils.hasText(entry.getValue())) {
                        predicates.add(cb.equal(getPath(root, entry.getKey()).as(String.class), entry.getValue()));
                    }
                }
            }

            if (request.getCreatedAtFrom() != null && createdAtField != null) {
                predicates.add(cb.greaterThanOrEqualTo(getPath(root, createdAtField).as(Date.class), request.getCreatedAtFrom()));
            }
            if (request.getCreatedAtTo() != null && createdAtField != null) {
                predicates.add(cb.lessThanOrEqualTo(getPath(root, createdAtField).as(Date.class), request.getCreatedAtTo()));
            }

            if (request.getDistrictId() != null && districtFieldPath != null) {
                predicates.add(cb.equal(getPath(root, districtFieldPath).get("id"), request.getDistrictId()));
            }

            if (request.getProvinceId() != null) {
                Join<Job, JobProvince> join = root.join("jobProvinces", JoinType.INNER);
                predicates.add(cb.equal(join.get("province").get("id"), request.getProvinceId()));
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Sort buildSort(BaseFilterRequest request, String sortField) {
        if (StringUtils.hasText(sortField)) {
            return "DESC".equalsIgnoreCase(request.getSortDirection())
                ? Sort.by(Sort.Order.desc(sortField))
                : Sort.by(Sort.Order.asc(sortField));
        }
        return Sort.unsorted();
    }

    private Path<?> getPath(From<?, ?> root, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Path<?> path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return path;
    }
}
