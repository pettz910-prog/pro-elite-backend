package com.mdau.proelitecars.vehicle.dto;

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
    private String status;
    private String condition;
    private String bodyStyle;
    private String fuelType;
    private String transmission;
    private String badge;
    private Integer mileage;
    private String exteriorColor;
    private String interiorColor;
    private String engine;
    private String drivetrain;
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