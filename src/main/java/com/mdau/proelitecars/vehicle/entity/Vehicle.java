package com.mdau.proelitecars.vehicle.entity;

import com.mdau.proelitecars.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "vehicles",
    indexes = {
        @Index(name = "idx_vehicles_status",      columnList = "status"),
        @Index(name = "idx_vehicles_make",        columnList = "make"),
        @Index(name = "idx_vehicles_model",       columnList = "model"),
        @Index(name = "idx_vehicles_year",        columnList = "year"),
        @Index(name = "idx_vehicles_price",       columnList = "price"),
        @Index(name = "idx_vehicles_condition",   columnList = "condition"),
        @Index(name = "idx_vehicles_body_style",  columnList = "body_style"),
        @Index(name = "idx_vehicles_fuel_type",   columnList = "fuel_type"),
        @Index(name = "idx_vehicles_featured",    columnList = "featured"),
        @Index(name = "idx_vehicles_created_at",  columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseEntity {

    // ── Core identity ─────────────────────────────────────────────────────
    @Column(name = "make", nullable = false, length = 100)
    private String make;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "trim", length = 100)
    private String trim;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "vin", unique = true, length = 17)
    private String vin;

    @Column(name = "stock_number", unique = true, length = 50)
    private String stockNumber;

    // ── Pricing ───────────────────────────────────────────────────────────
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "msrp", precision = 12, scale = 2)
    private BigDecimal msrp;

    // ── Classification ────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false, length = 30)
    private VehicleCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_style", length = 20)
    private BodyStyle bodyStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", length = 20)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission", length = 20)
    private Transmission transmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge", length = 20)
    private VehicleBadge badge;

    // ── Details ───────────────────────────────────────────────────────────
    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "exterior_color", length = 50)
    private String exteriorColor;

    @Column(name = "interior_color", length = 50)
    private String interiorColor;

    @Column(name = "engine", length = 100)
    private String engine;

    @Column(name = "drivetrain", length = 20)
    private String drivetrain;

    @Column(name = "doors")
    private Integer doors;

    @Column(name = "seats")
    private Integer seats;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ── Media ─────────────────────────────────────────────────────────────
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Column(name = "primary_image_url", length = 500)
    private String primaryImageUrl;

    // ── Flags ─────────────────────────────────────────────────────────────
    @Column(name = "featured", nullable = false)
    @Builder.Default
    private boolean featured = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "features", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> features = new ArrayList<>();

    // ── Convenience ───────────────────────────────────────────────────────
    public String getTitle() {
        return year + " " + make + " " + model + (trim != null ? " " + trim : "");
    }
}