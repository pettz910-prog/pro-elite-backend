package com.mdau.proelitecars.inquiry.service;

import com.mdau.proelitecars.inquiry.dto.InquiryDto;
import com.mdau.proelitecars.inquiry.dto.InquiryNoteDto;
import com.mdau.proelitecars.inquiry.entity.Inquiry;
import com.mdau.proelitecars.inquiry.entity.InquiryNote;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InquiryMapper {

    public InquiryDto toDto(Inquiry i) {
        return InquiryDto.builder()
                .id(i.getId())
                .vehicleId(i.getVehicleId())
                .vehicleTitle(i.getVehicleTitle())
                .customerName(i.getCustomerName())
                .email(i.getEmail())
                .phone(i.getPhone())
                .message(i.getMessage())
                .type(i.getType() != null ? i.getType().name() : null)
                .source(i.getSource() != null ? i.getSource().name() : null)
                .status(i.getStatus() != null ? i.getStatus().name() : null)
                .assignedToId(i.getAssignedToId())
                .assignedToName(i.getAssignedToName())
                .notes(i.getNotes() != null
                        ? i.getNotes().stream().map(this::toNoteDto).toList()
                        : List.of())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }

    public InquiryNoteDto toNoteDto(InquiryNote n) {
        return InquiryNoteDto.builder()
                .id(n.getId())
                .body(n.getBody())
                .authorId(n.getAuthorId())
                .authorName(n.getAuthorName())
                .createdAt(n.getCreatedAt())
                .build();
    }
}