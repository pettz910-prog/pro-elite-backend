package com.mdau.proelitecars.vehicle.repository;

import com.mdau.proelitecars.vehicle.entity.Vehicle;
import com.mdau.proelitecars.vehicle.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository
        extends JpaRepository<Vehicle, UUID>,
                JpaSpecificationExecutor<Vehicle> {

    List<Vehicle> findTop6ByFeaturedTrueAndStatusOrderByCreatedAtDesc(
            VehicleStatus status);

    long countByStatus(VehicleStatus status);

    @Query("SELECT DISTINCT v.make FROM Vehicle v ORDER BY v.make")
    List<String> findDistinctMakes();

    @Query("SELECT DISTINCT v.model FROM Vehicle v WHERE v.make = :make ORDER BY v.model")
    List<String> findDistinctModelsByMake(String make);
}