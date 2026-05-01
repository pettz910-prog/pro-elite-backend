package com.mdau.proelitecars.inquiry.entity;

import com.mdau.proelitecars.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "inquiries",
    indexes = {
        @Index(name = "idx_inquiries_status",       columnList = "status"),
        @Index(name = "idx_inquiries_type",         columnList = "type"),
        @Index(name = "idx_inquiries_email",        columnList = "email"),
        @Index(name = "idx_inquiries_vehicle_id",   columnList = "vehicle_id"),
        @Index(name = "idx_inquiries_assigned_to",  columnList = "assigned_to_id"),
        @Index(name = "idx_inquiries_created_at",   columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry extends BaseEntity {

    // ── Vehicle reference (nullable — general inquiries have no vehicle) ──
    @Column(name = "vehicle_id")
    private UUID vehicleId;

    @Column(name = "vehicle_title", length = 200)
    private String vehicleTitle;

    // ── Customer info ─────────────────────────────────────────────────────
    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    // ── Classification ────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    @Builder.Default
    private InquiryType type = InquiryType.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 20)
    private InquirySource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private InquiryStatus status = InquiryStatus.NEW;

    // ── Assignment ────────────────────────────────────────────────────────
    @Column(name = "assigned_to_id")
    private UUID assignedToId;

    @Column(name = "assigned_to_name", length = 150)
    private String assignedToName;

    // ── Notes (embedded as one-to-many) ───────────────────────────────────
    @OneToMany(
        mappedBy = "inquiry",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<InquiryNote> notes = new ArrayList<>();
}