package net.vuega.vuega_backend.Service.seats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.CreateSeatRequest;
import net.vuega.vuega_backend.DTO.seats.CreateSeatsInBatchRequest;
import net.vuega.vuega_backend.DTO.seats.LockSeatRequest;
import net.vuega.vuega_backend.DTO.seats.SeatDTO;
import net.vuega.vuega_backend.DTO.seats.UpdateSeatRequest;
import net.vuega.vuega_backend.Exception.DuplicateSeatException;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Model.seats.Seat;
import net.vuega.vuega_backend.Model.seats.SeatStatus;
import net.vuega.vuega_backend.Repository.seats.SeatRepository;

@Service
@Slf4j
public class SeatService {

    /** How long a LOCKED seat is held before automatic expiry. */
    private static final int LOCK_TTL_MINUTES = 10;

    private final SeatRepository repository;
    private final RestClient controlPlaneClient;

    public SeatService(
            SeatRepository repository,
            @Value("${control-plane.base-url:http://localhost:3000}") String controlPlaneBaseUrl) {
        this.repository = repository;
        this.controlPlaneClient = RestClient.builder()
                .baseUrl(controlPlaneBaseUrl)
                .build();
    }

    // ======================== CRUD ========================

    /**
     * Create a single seat.
     *
     * Edge cases:
     * - Duplicate (bus_id, seat_no) → DuplicateSeatException (409)
     * - price <= 0 → caught by @Valid before reaching here
     */
    @Transactional
    public SeatDTO createSeat(CreateSeatRequest request) {
        if (repository.existsByBusIdAndSeatNo(request.getBusId(), request.getSeatNo())) {
            throw new DuplicateSeatException(
                    String.valueOf(request.getBusId()), request.getSeatNo());
        }

        Seat seat = Seat.builder()
                .busId(request.getBusId())
                .seatNo(request.getSeatNo())
                .type(request.getType())
                .price(request.getPrice())
                .status(SeatStatus.AVAILABLE)
                .build();

        return toDTO(repository.save(seat));
    }

    /**
     * Batch-create seats.
     *
     * Edge cases:
     * - Any duplicate within the batch or with existing data → exception, whole
     * batch rolled back
     * - Batch size capped at DTO layer (100 seats)
     */
    @Transactional
    public List<SeatDTO> createSeatsInBatch(CreateSeatsInBatchRequest request) {
        // Pre-validate all before writing any — fail fast on first conflict
        for (CreateSeatRequest r : request.getSeats()) {
            if (repository.existsByBusIdAndSeatNo(r.getBusId(), r.getSeatNo())) {
                throw new DuplicateSeatException(
                        String.valueOf(r.getBusId()), r.getSeatNo());
            }
        }

        List<Seat> seats = request.getSeats().stream()
                .map(r -> Seat.builder()
                        .busId(r.getBusId())
                        .seatNo(r.getSeatNo())
                        .type(r.getType())
                        .price(r.getPrice())
                        .status(SeatStatus.AVAILABLE)
                        .build())
                .toList();

        return repository.saveAll(seats).stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Get a seat by ID (enriched with bus details from Control Plane).
     *
     * Edge cases:
     * - Not found → SeatNotFoundException (404)
     */
    @Transactional(readOnly = true)
    public SeatDTO getSeatById(Long seatId) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));
        return toDTOEnriched(seat);
    }

    /**
     * Get all seats for a given bus (enriched).
     */
    @Transactional(readOnly = true)
    public List<SeatDTO> getSeatsByBus(Long busId) {
        return repository.findByBusId(busId).stream()
                .map(this::toDTOEnriched)
                .toList();
    }

    /**
     * Get only AVAILABLE seats for a given bus (unenriched — for fast availability
     * checks).
     */
    @Transactional(readOnly = true)
    public List<SeatDTO> getAvailableSeats(Long busId) {
        return repository.findAvailableSeatsByBusId(busId).stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Partial update of a seat.
     *
     * Edge cases:
     * - Not found → SeatNotFoundException (404)
     * - Seat is LOCKED or BOOKED → SeatNotAvailableException (409): cannot mutate
     * a seat that is currently in use
     * - New seatNo collides with another seat on the same bus →
     * DuplicateSeatException (409)
     */
    @Transactional
    public SeatDTO updateSeat(Long seatId, UpdateSeatRequest request) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Cannot update seat " + seatId
                            + " — it is currently " + seat.getStatus()
                            + ". Only AVAILABLE seats can be modified.");
        }

        if (request.getSeatNo() != null
                && !request.getSeatNo().equals(seat.getSeatNo())
                && repository.existsByBusIdAndSeatNo(seat.getBusId(), request.getSeatNo())) {
            throw new DuplicateSeatException(
                    String.valueOf(seat.getBusId()), request.getSeatNo());
        }

        if (request.getSeatNo() != null)
            seat.setSeatNo(request.getSeatNo());
        if (request.getType() != null)
            seat.setType(request.getType());
        if (request.getPrice() != null)
            seat.setPrice(request.getPrice());

        return toDTO(repository.save(seat));
    }

    /**
     * Hard-delete a seat.
     *
     * Edge cases:
     * - Not found → SeatNotFoundException (404)
     * - Seat is LOCKED or BOOKED → SeatNotAvailableException (409)
     */
    @Transactional
    public void deleteSeat(Long seatId) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Cannot delete seat " + seatId
                            + " — it is currently " + seat.getStatus()
                            + ". Only AVAILABLE seats can be deleted.");
        }

        repository.delete(seat);
    }

    // ======================== LOCKING ========================

    /**
     * Lock a seat for a specific journey segment.
     *
     * Concurrency strategy:
     * 1. PESSIMISTIC_WRITE (SELECT … FOR UPDATE) — the DB row is exclusively
     * locked for the duration of this transaction, so two concurrent
     * requests reading the same seat both see its real status; only one
     * will find it AVAILABLE and proceed; the second will see LOCKED and
     * fail immediately with SeatLockConflictException.
     *
     * Edge cases:
     * - Not found → SeatNotFoundException (404)
     * - Already BOOKED → SeatNotAvailableException (422)
     * - Already LOCKED → SeatLockConflictException (409)
     * - fromStop >= toStop → InvalidStopRangeException (400)
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SeatDTO lockSeat(Long seatId, LockSeatRequest request) {
        validateStopRange(request.getFromStopOrder(), request.getToStopOrder());

        // Acquires DB-level exclusive row lock — blocks concurrent lockSeat / bookSeat
        // calls
        Seat seat = repository.findByIdWithPessimisticLock(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        switch (seat.getStatus()) {
            case BOOKED -> throw new SeatNotAvailableException(
                    "Seat " + seatId + " is already BOOKED and cannot be locked.");
            case LOCKED -> throw new SeatLockConflictException(
                    "Seat " + seatId + " is already locked by session: " + seat.getLockedBy()
                            + ". Lock expires at " + seat.getLockedAt().plusMinutes(LOCK_TTL_MINUTES) + ".");
            default -> {
                /* AVAILABLE — proceed */ }
        }

        seat.setStatus(SeatStatus.LOCKED);
        seat.setLockedBy(request.getLockedBy());
        seat.setLockedAt(LocalDateTime.now());
        seat.setFromStopOrder(request.getFromStopOrder());
        seat.setToStopOrder(request.getToStopOrder());

        return toDTO(repository.save(seat));
    }

    /**
     * Release a lock.
     *
     * Concurrency strategy: same PESSIMISTIC_WRITE so unlock and a concurrent
     * book cannot race each other.
     *
     * Edge cases:
     * - Not found → SeatNotFoundException (404)
     * - Not LOCKED → SeatNotAvailableException (422)
     * - lockedBy mismatch → SeatLockConflictException (403)
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SeatDTO unlockSeat(Long seatId, String lockedBy) {
        Seat seat = repository.findByIdWithPessimisticLock(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() != SeatStatus.LOCKED) {
            throw new SeatNotAvailableException(
                    "Seat " + seatId + " is not LOCKED (current status: " + seat.getStatus() + ").");
        }
        if (!seat.getLockedBy().equals(lockedBy)) {
            throw new SeatLockConflictException(
                    "Cannot unlock seat " + seatId
                            + " — it was locked by a different session (" + seat.getLockedBy() + ").");
        }

        clearLock(seat);
        return toDTO(repository.save(seat));
    }

    // ======================== BOOKING ========================

    /**
     * Confirm a booking for a seat that is already LOCKED by the same session.
     *
     * Concurrency strategy:
     * 1. PESSIMISTIC_WRITE prevents a second concurrent book from reading
     * LOCKED status simultaneously.
     * 2. @Version (optimistic locking) is the last line of defence: if a
     * concurrent transaction somehow committed a version change between
     * our read and our write, Hibernate throws
     * ObjectOptimisticLockingFailureException, which is caught and surfaced
     * as SeatLockConflictException so the caller can retry.
     *
     * Edge cases:
     * - Not found → SeatNotFoundException (404)
     * - Already BOOKED → SeatNotAvailableException (422)
     * - Not LOCKED → SeatNotAvailableException (422): must lock first
     * - lockedBy mismatch → SeatLockConflictException (409)
     * - Lock expired → SeatNotAvailableException (422): seat auto-reverts to
     * AVAILABLE
     * - Concurrent write → SeatLockConflictException (409): retry
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SeatDTO bookSeat(Long seatId, String lockedBy) {
        try {
            Seat seat = repository.findByIdWithPessimisticLock(seatId)
                    .orElseThrow(() -> new SeatNotFoundException(seatId));

            if (seat.getStatus() == SeatStatus.BOOKED) {
                throw new SeatNotAvailableException("Seat " + seatId + " is already BOOKED.");
            }
            if (seat.getStatus() != SeatStatus.LOCKED) {
                throw new SeatNotAvailableException(
                        "Seat " + seatId + " must be LOCKED before booking (current status: "
                                + seat.getStatus() + "). Call /lock first.");
            }
            if (!seat.getLockedBy().equals(lockedBy)) {
                throw new SeatLockConflictException(
                        "Cannot book seat " + seatId
                                + " — it was locked by a different session (" + seat.getLockedBy() + ").");
            }

            // Check the lock has not expired
            LocalDateTime lockExpiry = seat.getLockedAt().plusMinutes(LOCK_TTL_MINUTES);
            if (lockExpiry.isBefore(LocalDateTime.now())) {
                clearLock(seat);
                repository.save(seat);
                throw new SeatNotAvailableException(
                        "The lock on seat " + seatId + " expired at " + lockExpiry
                                + ". Please lock the seat again.");
            }

            seat.setStatus(SeatStatus.BOOKED);
            // Preserve fromStopOrder / toStopOrder as the confirmed booking segment.
            // Clear lock-specific metadata.
            seat.setLockedBy(null);
            seat.setLockedAt(null);

            return toDTO(repository.save(seat)); // @Version incremented here

        } catch (ObjectOptimisticLockingFailureException e) {
            // Another transaction updated this row after we read it.
            throw new SeatLockConflictException(
                    "Seat " + seatId + " was concurrently modified. Please retry.");
        }
    }

    /**
     * Cancel a confirmed booking — reverts the seat to AVAILABLE.
     *
     * Edge cases:
     * - Not found → SeatNotFoundException (404)
     * - Not BOOKED → SeatNotAvailableException (422)
     */
    @Transactional
    public SeatDTO cancelBooking(Long seatId) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() != SeatStatus.BOOKED) {
            throw new SeatNotAvailableException(
                    "Seat " + seatId + " is not BOOKED (current status: "
                            + seat.getStatus() + "). Nothing to cancel.");
        }

        clearLock(seat);
        return toDTO(repository.save(seat));
    }

    // ======================== SCHEDULED LOCK EXPIRY ========================

    /**
     * Runs every 60 seconds.
     *
     * Issues a single bulk UPDATE rather than loading and saving N entities,
     * keeping the DB round-trip O(1) regardless of how many locks expire.
     *
     * @Modifying queries clear the Hibernate first-level cache automatically
     *            (via clearAutomatically = true default on Spring
     *            Data's @Modifying),
     *            preventing stale entity reads after the bulk write.
     */
    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime expiryThreshold = LocalDateTime.now().minusMinutes(LOCK_TTL_MINUTES);
        int released = repository.bulkReleaseExpiredLocks(expiryThreshold);
        if (released > 0) {
            log.info("[SeatService] Released {} expired seat lock(s) (threshold: {}).",
                    released, expiryThreshold);
        }
    }

    // ======================== HELPERS ========================

    private void validateStopRange(int from, int to) {
        if (from >= to) {
            throw new InvalidStopRangeException(
                    "fromStopOrder (" + from + ") must be strictly less than toStopOrder (" + to + ").");
        }
    }

    private void clearLock(Seat seat) {
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setLockedBy(null);
        seat.setLockedAt(null);
        seat.setFromStopOrder(null);
        seat.setToStopOrder(null);
    }

    // ---- DTO mapping --------------------------------------------------

    private SeatDTO toDTO(Seat seat) {
        return SeatDTO.builder()
                .seatId(seat.getSeatId())
                .busId(seat.getBusId())
                .seatNo(seat.getSeatNo())
                .type(seat.getType())
                .price(seat.getPrice())
                .fromStopOrder(seat.getFromStopOrder())
                .toStopOrder(seat.getToStopOrder())
                .status(seat.getStatus())
                .lockedBy(seat.getLockedBy())
                .lockedAt(seat.getLockedAt())
                .build();
    }

    /** toDTO + Control Plane enrichment. */
    private SeatDTO toDTOEnriched(Seat seat) {
        SeatDTO dto = toDTO(seat);
        dto.setBusDetails(fetchBusDetails(seat.getBusId()));
        return dto;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchBusDetails(Long busId) {
        try {
            return controlPlaneClient.get()
                    .uri("/api/controlplane/buses/{busId}", busId)
                    .retrieve()
                    .body(Map.class);
        } catch (HttpClientErrorException e) {
            return Map.of("error", "Bus not found", "busId", busId);
        } catch (Exception e) {
            return Map.of("error", "Control Plane unavailable", "busId", busId);
        }
    }
}
