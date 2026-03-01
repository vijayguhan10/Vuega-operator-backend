package net.vuega.vuega_backend.Model.seats.session;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "booking_sessions", indexes = @Index(name = "idx_session_expires_at", columnList = "expires_at"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "passenger_id", nullable = false)
    private Long passengerId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
