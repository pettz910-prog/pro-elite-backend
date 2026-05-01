package com.mdau.proelitecars.vehicle.repository;

import com.mdau.proelitecars.vehicle.entity.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VehicleSpecification {

    private VehicleSpecification() {}

    public static Specification<Vehicle> withFilters(
            String make,
            String model,
            Integer minYear,
            Integer maxYear,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minMileage,
            Integer maxMileage,
            VehicleCondition condition,
            BodyStyle bodyStyle,
            FuelType fuelType,
            Transmission transmission,
            String location,
            VehicleStatus status,
            VehicleBadge badge,
            String keyword
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (make != null && !make.isBlank())
                predicates.add(cb.like(cb.lower(root.get("make")),
                        "%" + make.toLowerCase() + "%"));

            if (model != null && !model.isBlank())
                predicates.add(cb.like(cb.lower(root.get("model")),
                        "%" + model.toLowerCase() + "%"));

            if (minYear != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("year"), minYear));

            if (maxYear != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("year"), maxYear));

            if (minPrice != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));

            if (maxPrice != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));

            if (minMileage != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("mileage"), minMileage));

            if (maxMileage != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("mileage"), maxMileage));

            if (condition != null)
                predicates.add(cb.equal(root.get("condition"), condition));

            if (bodyStyle != null)
                predicates.add(cb.equal(root.get("bodyStyle"), bodyStyle));

            if (fuelType != null)
                predicates.add(cb.equal(root.get("fuelType"), fuelType));

            if (transmission != null)
                predicates.add(cb.equal(root.get("transmission"), transmission));

            if (location != null && !location.isBlank())
                predicates.add(cb.like(cb.lower(root.get("location")),
                        "%" + location.toLowerCase() + "%"));

            if (status != null)
                predicates.add(cb.equal(root.get("status"), status));
            else
                predicates.add(cb.notEqual(root.get("status"), VehicleStatus.HIDDEN));

            if (badge != null)
                predicates.add(cb.equal(root.get("badge"), badge));

            if (keyword != null && !keyword.isBlank()) {
                String kw = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("make")),  kw),
                        cb.like(cb.lower(root.get("model")), kw),
                        cb.like(cb.lower(root.get("trim")),  kw),
                        cb.like(cb.lower(root.get("description")), kw),
                        cb.like(cb.lower(root.get("location")), kw)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}