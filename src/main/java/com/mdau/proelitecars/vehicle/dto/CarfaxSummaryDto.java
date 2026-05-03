package com.mdau.proelitecars.vehicle.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarfaxSummaryDto {
    private String vin;
    private boolean available;
    private String unavailableMessage;

    // Quick summary fields
    private Integer ownerCount;
    private Boolean hasAccidents;
    private Integer accidentCount;
    private Boolean cleanTitle;
    private Integer serviceRecordCount;
    private String lastServiceDate;

    // Links
    private String reportUrl;
    private String badgeUrl;
    private String source;
}