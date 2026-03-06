package net.vuega.vuega_backend.Repository.seats.session;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.seats.session.BookingSession;

@Repository
public interface BookingSessionRepository extends JpaRepository<BookingSession, Long> {

    /**
     * Find session by ID. No explicit locking — concurrency handled by
     * unique constraints and transaction isolation.
     */
    Optional<BookingSession> findBySessionId(Long sessionId);

    /**
     * Delete all expired sessions. SeatLocks cascade-delete automatically.
     */
    @Modifying
    @Query("DELETE FROM BookingSession bs WHERE bs.expiresAt < :now")
    int deleteExpiredSessions(@Param("now") LocalDateTime now);
}
