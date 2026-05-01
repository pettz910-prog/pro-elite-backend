package com.mdau.proelitecars.inquiry.repository;

import com.mdau.proelitecars.inquiry.entity.Inquiry;
import com.mdau.proelitecars.inquiry.entity.InquiryStatus;
import com.mdau.proelitecars.inquiry.entity.InquiryType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InquirySpecification {

    private InquirySpecification() {}

    public static Specification<Inquiry> withFilters(
            InquiryStatus status,
            InquiryType type,
            UUID assignedToId
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));

            if (type != null)
                predicates.add(cb.equal(root.get("type"), type));

            if (assignedToId != null)
                predicates.add(cb.equal(root.get("assignedToId"), assignedToId));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}