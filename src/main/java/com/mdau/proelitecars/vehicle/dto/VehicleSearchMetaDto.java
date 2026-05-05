package com.mdau.proelitecars.vehicle.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class VehicleSearchMetaDto {
    private List<String> makes;
    private List<String> bodyStyles;
    private List<Integer> years;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minMileage;
    private Integer maxMileage;
}