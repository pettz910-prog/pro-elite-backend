package com.mdau.proelitecars.user.dto;

import com.mdau.proelitecars.inquiry.dto.InquiryDto;
import com.mdau.proelitecars.vehicle.dto.VehicleCardDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardDto {
    private VehicleStats vehicles;
    private InquiryStats inquiries;
    private List<VehicleCardDto> recentVehicles;
    private List<InquiryDto> recentInquiries;

    @Data
    @Builder
    public static class VehicleStats {
        private long total;
        private long available;
        private long sold;
        private long reserved;
        private long hidden;
    }

    @Data
    @Builder
    public static class InquiryStats {
        private long total;
        private long newCount;
        private long inProgress;
        private long resolved;
        private long closed;
        private long newToday;
    }
}