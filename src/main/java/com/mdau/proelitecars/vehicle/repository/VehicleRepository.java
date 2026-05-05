package com.mdau.proelitecars.vehicle.repository;

import com.mdau.proelitecars.vehicle.entity.Vehicle;
import com.mdau.proelitecars.vehicle.entity.VehicleStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository
        extends JpaRepository<Vehicle, UUID>,
                JpaSpecificationExecutor<Vehicle> {

    List<Vehicle> findTop6ByFeaturedTrueAndStatusOrderByCreatedAtDesc(
            VehicleStatus status);

    long countByStatus(VehicleStatus status);

    // ── Search support ────────────────────────────────────────────────────

    @Query("SELECT DISTINCT v.make FROM Vehicle v " +
           "WHERE v.status != 'HIDDEN' " +
           "ORDER BY v.make ASC")
    List<String> findDistinctMakes();

    @Query("SELECT DISTINCT v.model FROM Vehicle v " +
           "WHERE v.make = :make AND v.status != 'HIDDEN' " +
           "ORDER BY v.model ASC")
    List<String> findDistinctModelsByMake(String make);

    @Query("SELECT DISTINCT v.bodyStyle FROM Vehicle v " +
           "WHERE v.status != 'HIDDEN' AND v.bodyStyle IS NOT NULL " +
           "ORDER BY v.bodyStyle ASC")
    List<String> findDistinctBodyStyles();

    @Query("SELECT DISTINCT v.year FROM Vehicle v " +
           "WHERE v.status != 'HIDDEN' " +
           "ORDER BY v.year DESC")
    List<Integer> findDistinctYears();

    @Query("SELECT MIN(v.price) FROM Vehicle v WHERE v.status != 'HIDDEN'")
    BigDecimal findMinPrice();

    @Query("SELECT MAX(v.price) FROM Vehicle v WHERE v.status != 'HIDDEN'")
    BigDecimal findMaxPrice();

    @Query("SELECT MIN(v.mileage) FROM Vehicle v " +
           "WHERE v.status != 'HIDDEN' AND v.mileage IS NOT NULL")
    Integer findMinMileage();

    @Query("SELECT MAX(v.mileage) FROM Vehicle v " +
           "WHERE v.status != 'HIDDEN' AND v.mileage IS NOT NULL")
    Integer findMaxMileage();
}