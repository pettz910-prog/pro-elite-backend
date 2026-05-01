package com.mdau.proelitecars.vehicle.dto;

import com.mdau.proelitecars.vehicle.entity.VehicleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleStatusRequest {
    @NotNull(message = "Status is required")
    private VehicleStatus status;
}