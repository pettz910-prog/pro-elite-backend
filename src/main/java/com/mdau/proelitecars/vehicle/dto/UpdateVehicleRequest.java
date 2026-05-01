package com.mdau.proelitecars.vehicle.dto;

import com.mdau.proelitecars.vehicle.entity.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateVehicleRequest {

    private String make;
    private String model;
    private String trim;

    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;

    private String vin;
    private String stockNumber;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal msrp;
    private VehicleCondition condition;
    private VehicleStatus status;
    private BodyStyle bodyStyle;
    private FuelType fuelType;
    private Transmission transmission;
    private VehicleBadge badge;
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
    private Boolean featured;
    private List<String> features;
}