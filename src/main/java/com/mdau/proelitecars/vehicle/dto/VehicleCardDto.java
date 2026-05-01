package com.mdau.proelitecars.vehicle.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
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
    private String status;
    private String condition;
    private String bodyStyle;
    private String fuelType;
    private String transmission;
    private String badge;
    private Integer mileage;
    private String exteriorColor;
    private String primaryImageUrl;
    private String location;
    private boolean featured;
    private Instant createdAt;
}