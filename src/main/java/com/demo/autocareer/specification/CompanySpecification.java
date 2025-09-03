package com.demo.autocareer.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.demo.autocareer.filter.CompanyFilter;
import com.demo.autocareer.filter.StudentInternFilter;
import com.demo.autocareer.model.InternshipRequest;
import com.demo.autocareer.model.Organization;
import com.demo.autocareer.model.Student;
import com.demo.autocareer.model.enums.OrganizationType;
import com.demo.autocareer.model.enums.StatusIntern;
import com.demo.autocareer.model.enums.StatusRequest;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;


@Component
public class CompanySpecification extends BaseSpecification<Organization> {

    public Specification<Organization> buildSpec(CompanyFilter request, Organization currentUniversity) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // chỉ lấy tổ chức là công ty
            predicates.add(cb.equal(root.get("organizationType"), OrganizationType.COMPANY));

            // keyword search
            if (StringUtils.hasText(request.getKeyword())) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("organizationName")), keyword));
            }

            // join internshipRequestsAsCompany (nhiều company → nhiều internshipRequests)
            Join<Organization, InternshipRequest> internJoin =
                    root.join("internshipRequestsAsCompany", JoinType.LEFT);

            if (request.getStatusRequest() != null && currentUniversity != null) {
                switch (request.getStatusRequest()) {
                    case NOT_YET:
                        // Công ty chưa có internshipRequest nào từ university hiện tại
                        Subquery<Long> sub = query.subquery(Long.class);
                        Root<InternshipRequest> subRoot = sub.from(InternshipRequest.class);
                        sub.select(subRoot.get("company").get("id"))
                        .where(cb.equal(subRoot.get("university").get("id"), currentUniversity.getId()));
                        predicates.add(cb.not(root.get("id").in(sub)));
                        break;

                    case PENDING:
                        predicates.add(cb.and(
                            cb.equal(internJoin.get("statusRequest"), StatusRequest.PENDING),
                            cb.equal(internJoin.get("university").get("id"), currentUniversity.getId())
                        ));
                        break;

                    case APPROVED:
                        predicates.add(cb.and(
                            cb.equal(internJoin.get("statusRequest"), StatusRequest.APPROVED),
                            cb.equal(internJoin.get("university").get("id"), currentUniversity.getId())
                        ));
                        break;

                    case REJECTED:
                        predicates.add(cb.and(
                            cb.equal(internJoin.get("statusRequest"), StatusRequest.REJECTED),
                            cb.equal(internJoin.get("university").get("id"), currentUniversity.getId())
                        ));
                        break;
                }
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
