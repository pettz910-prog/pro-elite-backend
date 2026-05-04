package com.mdau.proelitecars.vehicle.service;

import com.mdau.proelitecars.vehicle.dto.VehicleCardDto;
import com.mdau.proelitecars.vehicle.dto.VehicleDto;
import com.mdau.proelitecars.vehicle.entity.Vehicle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class VehicleMapper {

    public VehicleCardDto toCardDto(Vehicle v) {
        return VehicleCardDto.builder()
                .id(v.getId())
                .title(v.getTitle())
                .make(v.getMake())
                .model(v.getModel())
                .trim(v.getTrim())
                .year(v.getYear())
                .price(v.getPrice())
                .msrp(v.getMsrp())
                .status(v.getStatus())
                .condition(v.getCondition())
                .bodyStyle(v.getBodyStyle())
                .fuelType(v.getFuelType())
                .transmission(v.getTransmission())
                .badge(v.getBadge())
                .drivetrain(v.getDrivetrain())
                .mileage(v.getMileage())
                .exteriorColor(v.getExteriorColor())
                .primaryImageUrl(v.getPrimaryImageUrl())
                .location(v.getLocation())
                .featured(v.isFeatured())
                .images(v.getImages() != null ? v.getImages() : new ArrayList<>())
                .createdAt(v.getCreatedAt())
                .build();
    }

    public VehicleDto toDto(Vehicle v) {
        return VehicleDto.builder()
                .id(v.getId())
                .title(v.getTitle())
                .make(v.getMake())
                .model(v.getModel())
                .trim(v.getTrim())
                .year(v.getYear())
                .vin(v.getVin())
                .stockNumber(v.getStockNumber())
                .price(v.getPrice())
                .msrp(v.getMsrp())
                .status(v.getStatus())
                .condition(v.getCondition())
                .bodyStyle(v.getBodyStyle())
                .fuelType(v.getFuelType())
                .transmission(v.getTransmission())
                .badge(v.getBadge())
                .drivetrain(v.getDrivetrain())
                .mileage(v.getMileage())
                .exteriorColor(v.getExteriorColor())
                .interiorColor(v.getInteriorColor())
                .engine(v.getEngine())
                .doors(v.getDoors())
                .seats(v.getSeats())
                .location(v.getLocation())
                .description(v.getDescription())
                .images(v.getImages() != null ? v.getImages() : new ArrayList<>())
                .primaryImageUrl(v.getPrimaryImageUrl())
                .featured(v.isFeatured())
                .features(v.getFeatures() != null ? v.getFeatures() : new ArrayList<>())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}