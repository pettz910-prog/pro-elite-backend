package com.mdau.proelitecars.vehicle.entity;

import com.mdau.proelitecars.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Entity
@Table(
    name = "carfax_reports",
    indexes = {
        @Index(name = "idx_carfax_vin",        columnList = "vin", unique = true),
        @Index(name = "idx_carfax_fetched_at",  columnList = "fetched_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarfaxReport extends BaseEntity {

    @Column(name = "vin", nullable = false, unique = true, length = 17)
    private String vin;

    // ── Quick summary fields ───────────────────────────────────────────────
    @Column(name = "owner_count")
    private Integer ownerCount;

    @Column(name = "has_accidents")
    private Boolean hasAccidents;

    @Column(name = "accident_count")
    private Integer accidentCount;

    @Column(name = "clean_title")
    private Boolean cleanTitle;

    @Column(name = "service_record_count")
    private Integer serviceRecordCount;

    @Column(name = "last_service_date", length = 20)
    private String lastServiceDate;

    // ── Extended history fields ────────────────────────────────────────────
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "use_types", columnDefinition = "jsonb")
    private List<String> useTypes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recalls", columnDefinition = "jsonb")
    private List<String> recalls;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "odometer_readings", columnDefinition = "jsonb")
    private List<OdometerReading> odometerReadings;

    // ── Report links ───────────────────────────────────────────────────────
    @Column(name = "report_url", length = 500)
    private String reportUrl;

    @Column(name = "badge_url", length = 500)
    private String badgeUrl;

    // ── Cache metadata ─────────────────────────────────────────────────────
    @Column(name = "fetched_at", nullable = false)
    private Instant fetchedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private CarfaxSource source;

    // ── Cache check ────────────────────────────────────────────────────────
    public boolean isStale(int cacheDays) {
        return fetchedAt.plusSeconds(cacheDays * 86400L)
                .isBefore(Instant.now());
    }

    // ── Embedded odometer reading ──────────────────────────────────────────
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OdometerReading {
        private String date;
        private Integer miles;
    }

    public enum CarfaxSource {
        FREE,
        DEALER_API
    }
}