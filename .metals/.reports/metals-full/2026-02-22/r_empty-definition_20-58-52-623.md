error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/SeatService.java:_empty_/Transactional#isolation#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/SeatService.java
empty definition using pc, found symbol in pc: _empty_/Transactional#isolation#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 6180
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/SeatService.java
text:
```scala
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
import net.vuega.vuega_backend.DTO.seats.SeatUpdateMessage;
import net.vuega.vuega_backend.DTO.seats.UpdateSeatRequest;
import net.vuega.vuega_backend.Exception.DuplicateSeatException;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatLockConflictException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Model.seats.Seat;
import net.vuega.vuega_backend.Model.seats.SeatStatus;
import net.vuega.vuega_backend.Repository.seats.SeatRepository;

// Seat service — CRUD, lock/unlock/book, and scheduled lock expiry.
@Service
@Slf4j
public class SeatService {

    private static final int LOCK_TTL_MINUTES = 10;

    private final SeatRepository repository;
    private final RestClient controlPlaneClient;
    private final SeatSocketService socketService;

    public SeatService(
            SeatRepository repository,
            SeatSocketService socketService,
            @Value("${control-plane.base-url:http://localhost:3000}") String controlPlaneBaseUrl) {
        this.repository = repository;
        this.socketService = socketService;
        this.controlPlaneClient = RestClient.builder()
                .baseUrl(controlPlaneBaseUrl)
                .build();
    }

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

    @Transactional
    public List<SeatDTO> createSeatsInBatch(CreateSeatsInBatchRequest request) {
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

    @Transactional(readOnly = true)
    public SeatDTO getSeatById(Long seatId) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));
        return toDTOEnriched(seat);
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> getSeatsByBus(Long busId) {
        return repository.findByBusId(busId).stream()
                .map(this::toDTOEnriched)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeatDTO> getAvailableSeats(Long busId) {
        return repository.findAvailableSeatsByBusId(busId).stream()
                .map(this::toDTO)
                .toList();
    }

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

    @Transactional(isolati@@on = Isolation.READ_COMMITTED)
    public SeatDTO lockSeat(Long seatId, LockSeatRequest request) {
        validateStopRange(request.getFromStopOrder(), request.getToStopOrder());

        Seat seat = repository.findByIdWithPessimisticLock(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        switch (seat.getStatus()) {
            case BOOKED -> throw new SeatNotAvailableException(
                    "Seat " + seatId + " is already BOOKED and cannot be locked.");
            case LOCKED -> throw new SeatLockConflictException(
                    "Seat " + seatId + " is already locked by session: " + seat.getLockedBy()
                            + ". Lock expires at " + seat.getLockedAt().plusMinutes(LOCK_TTL_MINUTES) + ".");
            default -> {
            }
        }

        seat.setStatus(SeatStatus.LOCKED);
        seat.setLockedBy(request.getLockedBy());
        seat.setLockedAt(LocalDateTime.now());
        seat.setFromStopOrder(request.getFromStopOrder());
        seat.setToStopOrder(request.getToStopOrder());

        SeatDTO dto = toDTO(repository.save(seat));
        socketService.broadcast(SeatUpdateMessage.builder()
                .event(SeatUpdateMessage.Event.LOCKED)
                .seatId(dto.getSeatId())
                .busId(dto.getBusId())
                .seatNo(dto.getSeatNo())
                .status(dto.getStatus())
                .count(1)
                .timestamp(LocalDateTime.now())
                .build());
        return dto;
    }

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
        SeatDTO dto = toDTO(repository.save(seat));
        socketService.broadcast(SeatUpdateMessage.builder()
                .event(SeatUpdateMessage.Event.UNLOCKED)
                .seatId(dto.getSeatId())
                .busId(dto.getBusId())
                .seatNo(dto.getSeatNo())
                .status(dto.getStatus())
                .count(1)
                .timestamp(LocalDateTime.now())
                .build());
        return dto;
    }

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

            LocalDateTime lockExpiry = seat.getLockedAt().plusMinutes(LOCK_TTL_MINUTES);
            if (lockExpiry.isBefore(LocalDateTime.now())) {
                clearLock(seat);
                repository.save(seat);
                throw new SeatNotAvailableException(
                        "The lock on seat " + seatId + " expired at " + lockExpiry
                                + ". Please lock the seat again.");
            }

            seat.setStatus(SeatStatus.BOOKED);
            seat.setLockedBy(null);
            seat.setLockedAt(null);

            SeatDTO dto = toDTO(repository.save(seat));
            socketService.broadcast(SeatUpdateMessage.builder()
                    .event(SeatUpdateMessage.Event.BOOKED)
                    .seatId(dto.getSeatId())
                    .busId(dto.getBusId())
                    .seatNo(dto.getSeatNo())
                    .status(dto.getStatus())
                    .count(1)
                    .timestamp(LocalDateTime.now())
                    .build());
            return dto;

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new SeatLockConflictException(
                    "Seat " + seatId + " was concurrently modified. Please retry.");
        }
    }

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
        SeatDTO dto = toDTO(repository.save(seat));
        socketService.broadcast(SeatUpdateMessage.builder()
                .event(SeatUpdateMessage.Event.CANCELLED)
                .seatId(dto.getSeatId())
                .busId(dto.getBusId())
                .seatNo(dto.getSeatNo())
                .status(dto.getStatus())
                .count(1)
                .timestamp(LocalDateTime.now())
                .build());
        return dto;
    }

    @Scheduled(fixedRate = 30_000)
    @Transactional
    public void releaseExpiredLocks() {
        LocalDateTime expiryThreshold = LocalDateTime.now().minusMinutes(LOCK_TTL_MINUTES);
        int released = repository.bulkReleaseExpiredLocks(expiryThreshold);
        if (released > 0) {
            log.info("[SeatService] Released {} expired seat lock(s).", released);
            socketService.broadcast(SeatUpdateMessage.builder()
                    .event(SeatUpdateMessage.Event.EXPIRED)
                    .count(released)
                    .timestamp(LocalDateTime.now())
                    .build());
        }
    }

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

```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Transactional#isolation#