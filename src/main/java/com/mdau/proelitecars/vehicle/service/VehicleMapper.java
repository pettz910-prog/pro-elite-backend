package com.mdau.proelitecars.vehicle.service;

import com.mdau.proelitecars.vehicle.dto.VehicleCardDto;
import com.mdau.proelitecars.vehicle.dto.VehicleDto;
import com.mdau.proelitecars.vehicle.entity.Vehicle;
import org.springframework.stereotype.Component;

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
                .status(v.getStatus() != null ? v.getStatus().name() : null)
                .condition(v.getCondition() != null ? v.getCondition().name() : null)
                .bodyStyle(v.getBodyStyle() != null ? v.getBodyStyle().name() : null)
                .fuelType(v.getFuelType() != null ? v.getFuelType().name() : null)
                .transmission(v.getTransmission() != null ? v.getTransmission().name() : null)
                .badge(v.getBadge() != null ? v.getBadge().name() : null)
                .mileage(v.getMileage())
                .exteriorColor(v.getExteriorColor())
                .primaryImageUrl(v.getPrimaryImageUrl())
                .location(v.getLocation())
                .featured(v.isFeatured())
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
                .status(v.getStatus() != null ? v.getStatus().name() : null)
                .condition(v.getCondition() != null ? v.getCondition().name() : null)
                .bodyStyle(v.getBodyStyle() != null ? v.getBodyStyle().name() : null)
                .fuelType(v.getFuelType() != null ? v.getFuelType().name() : null)
                .transmission(v.getTransmission() != null ? v.getTransmission().name() : null)
                .badge(v.getBadge() != null ? v.getBadge().name() : null)
                .mileage(v.getMileage())
                .exteriorColor(v.getExteriorColor())
                .interiorColor(v.getInteriorColor())
                .engine(v.getEngine())
                .drivetrain(v.getDrivetrain())
                .doors(v.getDoors())
                .seats(v.getSeats())
                .location(v.getLocation())
                .description(v.getDescription())
                .images(v.getImages())
                .primaryImageUrl(v.getPrimaryImageUrl())
                .featured(v.isFeatured())
                .features(v.getFeatures())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}