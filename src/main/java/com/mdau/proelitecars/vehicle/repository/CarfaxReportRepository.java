package com.mdau.proelitecars.vehicle.repository;

import com.mdau.proelitecars.vehicle.entity.CarfaxReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarfaxReportRepository
        extends JpaRepository<CarfaxReport, UUID> {

    Optional<CarfaxReport> findByVin(String vin);
}