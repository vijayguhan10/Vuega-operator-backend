package net.vuega.vuega_backend.Service.seats.session;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.Repository.seats.session.BookingSessionRepository;

/**
 * Periodically cleans up expired BookingSessions.
 * SeatLock rows cascade-delete automatically when their session is deleted.
 * Does NOT touch any Booking data.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingSessionCleanupService {

    private final BookingSessionRepository sessionRepository;

    @Scheduled(fixedRate = 15_000)
    @Transactional
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        int deleted = sessionRepository.deleteExpiredSessions(now);
        if (deleted > 0) {
            log.info("[SessionCleanup] Deleted {} expired session(s) and their cascade-deleted locks", deleted);
        }
    }
}
