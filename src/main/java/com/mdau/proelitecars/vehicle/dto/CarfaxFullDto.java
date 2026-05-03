package com.mdau.proelitecars.vehicle.dto;

import com.mdau.proelitecars.vehicle.entity.CarfaxReport;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CarfaxFullDto {
    private String vin;
    private boolean available;
    private String unavailableMessage;

    // Summary
    private Integer ownerCount;
    private Boolean hasAccidents;
    private Integer accidentCount;
    private Boolean cleanTitle;
    private Integer serviceRecordCount;
    private String lastServiceDate;

    // Extended
    private List<String> useTypes;
    private List<String> recalls;
    private List<CarfaxReport.OdometerReading> odometerReadings;

    // Links
    private String reportUrl;
    private String badgeUrl;
    private String source;
    private String fetchedAt;
}