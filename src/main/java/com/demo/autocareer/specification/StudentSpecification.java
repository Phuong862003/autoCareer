package com.demo.autocareer.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.demo.autocareer.filter.StudentInternFilter;
import com.demo.autocareer.model.InternDeclareRequest;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.OrganizationFaculty;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.StatusIntern;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Component
public class StudentSpecification extends BaseSpecification<Student>{
    public Specification<Student> buildSpec(StudentInternFilter request, Organization org){
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            Join<Student, InternDeclareRequest> internJoin = root.join("internDeclareRequest", JoinType.LEFT);

            if (StringUtils.hasText(request.getKeyword())){
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), keyword),
                    cb.like(cb.lower(root.get("studentCode")), keyword),
                    cb.like(cb.lower(root.get("email")), keyword)
                ));
            }

            if (request.getStatusIntern() != null) {
                switch (request.getStatusIntern()) {
                    case NOT_YET:
                        predicates.add(cb.isNull(root.get("internDeclareRequest")));
                        break;
                    case WAITING:
                        predicates.add(cb.equal(internJoin.get("statusIntern"), StatusIntern.WAITING));
                        break;
                    case APPROVED:
                        predicates.add(cb.equal(internJoin.get("statusIntern"), StatusIntern.APPROVED));
                        break;
                    case REJECTED:
                        predicates.add(cb.equal(internJoin.get("statusIntern"), StatusIntern.REJECTED));
                        break;
                    case NOT_YET_OR_REJECTED:
                        predicates.add(cb.or(
                            cb.isNull(root.get("internDeclareRequest")),
                            cb.equal(internJoin.get("statusIntern"), StatusIntern.REJECTED)
                        ));
                        break;
                }
            }


            if (StringUtils.hasText(request.getSemester())) {
                predicates.add(cb.equal(internJoin.get("semester"), request.getSemester()));
            }

            if (request.getFacultyId() != null) {
                predicates.add(cb.equal(root.get("organizationFaculty").get("faculty").get("id"), request.getFacultyId()));
            }


            Predicate basePredicate = super.build(request, null, null, null, "district", null, null)
                                .toPredicate(root, query, cb);
            if (basePredicate != null) {
                predicates.add(basePredicate);
            }

            if (org != null) {
                predicates.add(cb.equal(root.get("organizationFaculty").get("organization").get("id"), org.getId()));
            }

            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

// public Specification<Student> buildSpec(StudentInternFilter request, Organization org){
//         return (root, query, cb) -> {
//             List<Predicate> predicates = new ArrayList<>();

//             if (StringUtils.hasText(request.getKeyword())){
//                 String keyword = "%" + request.getKeyword().toLowerCase() + "%";
//                 predicates.add(cb.or(
//                     cb.like(cb.lower(root.get("name")), keyword),
//                     cb.like(cb.lower(root.get("studentCode")), keyword),
//                     cb.like(cb.lower(root.get("email")), keyword)
//                 ));
//             }

//             Join<Student, OrganizationFaculty> orgFacultyJoin = root.join("organizationFaculty", JoinType.LEFT);
//             Join<OrganizationFaculty, Faculty> facultyJoin = orgFacultyJoin.join("faculty", JoinType.LEFT);

//             if (request.getFacultyId() != null) {
//                 predicates.add(cb.equal(facultyJoin.get("id"), request.getFacultyId()));
//             }

//             Predicate basePredicate = super.build(request, null, null, null, "district", null)
//                                 .toPredicate(root, query, cb);
//             if (basePredicate != null) {
//                 predicates.add(basePredicate);
//             }

//             return cb.and(predicates.toArray(new Predicate[0]));
//         };
//     }