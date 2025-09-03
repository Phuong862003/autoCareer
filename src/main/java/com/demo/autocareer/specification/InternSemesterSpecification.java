package com.demo.autocareer.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.demo.autocareer.dto.request.BaseFilterRequest;
import com.demo.autocareer.model.InternshipSemester;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.Semester;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.StatusInternSemester;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Component
public class InternSemesterSpecification extends BaseSpecification<InternshipSemester>{
    public Specification<InternshipSemester> buildSpec(BaseFilterRequest request, Organization org){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<InternshipSemester, Student> studentJoin = root.join("student", JoinType.LEFT);
            Join<InternshipSemester, Semester> semesterJoin = root.join("semester", JoinType.LEFT);

            if (StringUtils.hasText(request.getKeyword())){
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(studentJoin.get("name")), keyword),
                    cb.like(cb.lower(studentJoin.get("studentCode")), keyword),
                    cb.like(cb.lower(studentJoin.get("email")), keyword)
                ));
            }

            if (request.getEnumValue() != null && !request.getEnumValue().isBlank()) {
                predicates.add(cb.equal(root.get("status"), StatusInternSemester.valueOf(request.getEnumValue())));
            }

            if (request.getFilters() != null) {
                String semesterCode = request.getFilters().get("semesterCode");
                if (StringUtils.hasText(semesterCode)) {
                    predicates.add(cb.equal(cb.lower(semesterJoin.get("code")), semesterCode.toLowerCase()));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
