package com.mdau.proelitecars.vehicle.dto;

import com.mdau.proelitecars.vehicle.entity.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateVehicleRequest {

    @NotBlank(message = "Make is required")
    private String make;

    @NotBlank(message = "Model is required")
    private String model;

    private String trim;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    private Integer year;

    private String vin;
    private String stockNumber;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal msrp;

    @NotNull(message = "Condition is required")
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
    private Drivetrain drivetrain;
    private Integer doors;
    private Integer seats;
    private String location;
    private String description;
    private List<String> images;
    private String primaryImageUrl;
    private boolean featured;
    private List<String> features;
}