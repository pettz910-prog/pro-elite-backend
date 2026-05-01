package com.mdau.proelitecars.inquiry.service;

import com.mdau.proelitecars.common.exception.ResourceNotFoundException;
import com.mdau.proelitecars.common.security.AuthenticatedUser;
import com.mdau.proelitecars.email.service.EmailService;
import com.mdau.proelitecars.inquiry.dto.*;
import com.mdau.proelitecars.inquiry.entity.*;
import com.mdau.proelitecars.inquiry.repository.InquiryRepository;
import com.mdau.proelitecars.inquiry.repository.InquirySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMapper inquiryMapper;
    private final EmailService emailService;

    @Transactional
    public InquiryDto create(CreateInquiryRequest request) {
        Inquiry inquiry = Inquiry.builder()
                .vehicleId(request.getVehicleId())
                .vehicleTitle(request.getVehicleTitle())
                .customerName(request.getCustomerName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .message(request.getMessage())
                .type(request.getType() != null
                        ? request.getType() : InquiryType.GENERAL)
                .source(request.getSource())
                .status(InquiryStatus.NEW)
                .build();

        Inquiry saved = inquiryRepository.save(inquiry);
        log.info("✅ Inquiry created: {} from {}", saved.getId(), saved.getEmail());

        emailService.sendInquiryAlertToAdmin(saved);
        emailService.sendInquiryConfirmationToCustomer(saved);

        return inquiryMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<InquiryDto> findAll(InquiryStatus status, InquiryType type,
                                    UUID assignedToId, Pageable pageable) {
        Specification<Inquiry> spec = InquirySpecification
                .withFilters(status, type, assignedToId);
        return inquiryRepository.findAll(spec, pageable)
                .map(inquiryMapper::toDto);
    }

    @Transactional(readOnly = true)
    public InquiryDto findById(UUID id) {
        Inquiry inquiry = inquiryRepository.findByIdWithNotes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", id));
        return inquiryMapper.toDto(inquiry);
    }

    @Transactional
    public InquiryDto updateStatus(UUID id, InquiryStatus status) {
        Inquiry inquiry = inquiryRepository.findByIdWithNotes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", id));
        inquiry.setStatus(status);
        Inquiry saved = inquiryRepository.save(inquiry);
        log.info("✅ Inquiry {} status → {}", id, status);
        return inquiryMapper.toDto(saved);
    }

    @Transactional
    public InquiryDto assign(UUID id, AssignInquiryRequest request) {
        Inquiry inquiry = inquiryRepository.findByIdWithNotes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", id));
        inquiry.setAssignedToId(request.getUserId());
        inquiry.setAssignedToName(request.getUserName());
        if (inquiry.getStatus() == InquiryStatus.NEW) {
            inquiry.setStatus(InquiryStatus.IN_PROGRESS);
        }
        Inquiry saved = inquiryRepository.save(inquiry);
        log.info("✅ Inquiry {} assigned to {}", id, request.getUserName());
        return inquiryMapper.toDto(saved);
    }

    @Transactional
    public InquiryNoteDto addNote(UUID id, AddNoteRequest request,
                                  AuthenticatedUser currentUser) {
        Inquiry inquiry = inquiryRepository.findByIdWithNotes(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry", id));

        InquiryNote note = InquiryNote.builder()
                .inquiry(inquiry)
                .body(request.getBody())
                .authorId(currentUser.getId())
                .authorName(currentUser.getEmail())
                .build();

        inquiry.getNotes().add(note);
        inquiryRepository.save(inquiry);
        log.info("✅ Note added to inquiry {} by {}", id, currentUser.getEmail());
        return inquiryMapper.toNoteDto(note);
    }
}