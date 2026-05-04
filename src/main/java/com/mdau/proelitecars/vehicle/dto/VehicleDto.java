package com.mdau.proelitecars.vehicle.dto;

import com.mdau.proelitecars.vehicle.entity.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class VehicleDto {
    private UUID id;
    private String title;
    private String make;
    private String model;
    private String trim;
    private Integer year;
    private String vin;
    private String stockNumber;
    private BigDecimal price;
    private BigDecimal msrp;
    private VehicleStatus status;
    private VehicleCondition condition;
    private BodyStyle bodyStyle;
    private FuelType fuelType;
    private Transmission transmission;
    private VehicleBadge badge;
    private Drivetrain drivetrain;
    private Integer mileage;
    private String exteriorColor;
    private String interiorColor;
    private String engine;
    private Integer doors;
    private Integer seats;
    private String location;
    private String description;
    private List<String> images;
    private String primaryImageUrl;
    private boolean featured;
    private List<String> features;
    private Instant createdAt;
    private Instant updatedAt;
}