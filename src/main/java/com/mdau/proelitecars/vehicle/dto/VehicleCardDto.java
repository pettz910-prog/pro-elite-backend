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
public class VehicleCardDto {
    private UUID id;
    private String title;
    private String make;
    private String model;
    private String trim;
    private Integer year;
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
    private String primaryImageUrl;
    private String location;
    private boolean featured;
    private List<String> images;
    private Instant createdAt;
}