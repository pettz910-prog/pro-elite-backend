package com.mdau.proelitecars.inquiry.repository;

import com.mdau.proelitecars.inquiry.entity.Inquiry;
import com.mdau.proelitecars.inquiry.entity.InquiryStatus;
import com.mdau.proelitecars.inquiry.entity.InquiryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InquiryRepository
        extends JpaRepository<Inquiry, UUID>,
                JpaSpecificationExecutor<Inquiry> {

    long countByStatus(InquiryStatus status);

    long countByType(InquiryType type);

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE i.createdAt >= :since")
    long countCreatedSince(Instant since);

    // ── Eager-fetch notes to avoid LazyInitializationException ───────────
    @Query("SELECT DISTINCT i FROM Inquiry i LEFT JOIN FETCH i.notes " +
           "ORDER BY i.createdAt DESC")
    List<Inquiry> findRecentWithNotes(Pageable pageable);

    @Query("SELECT DISTINCT i FROM Inquiry i LEFT JOIN FETCH i.notes " +
           "WHERE i.id = :id")
    Optional<Inquiry> findByIdWithNotes(UUID id);
}