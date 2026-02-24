error id: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/seat/SeatService.java:net/vuega/vuega_backend/DTO/seats/seat/CreateSeatsInBatchRequest#
file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/seat/SeatService.java
empty definition using pc, found symbol in pc: net/vuega/vuega_backend/DTO/seats/seat/CreateSeatsInBatchRequest#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 562
uri: file:///C:/Projects/Vuega-backend/vuega-backend/src/main/java/net/vuega/vuega_backend/Service/seats/seat/SeatService.java
text:
```scala
package net.vuega.vuega_backend.Service.seats.seat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.seat.CreateSeatRequest;
import net.vuega.vuega_backend.DTO.seats.seat.@@CreateSeatsInBatchRequest;
import net.vuega.vuega_backend.DTO.seats.seat.SeatDTO;
import net.vuega.vuega_backend.DTO.seats.seat.UpdateSeatRequest;
import net.vuega.vuega_backend.DTO.seats.socket.SeatUpdateMessage;
import net.vuega.vuega_backend.Exception.DuplicateSeatException;
import net.vuega.vuega_backend.Exception.InvalidStopRangeException;
import net.vuega.vuega_backend.Exception.SeatNotAvailableException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Model.seats.seat.Seat;
import net.vuega.vuega_backend.Model.seats.seat.SeatStatus;
import net.vuega.vuega_backend.Repository.seats.seat.SeatRepository;
import net.vuega.vuega_backend.Service.seats.socket.SeatSocketService;

// Seat service — CRUD operations and cancellation.
// Locking and booking are handled by SeatLockService.
@Service
@Slf4j
public class SeatService {

    private final SeatRepository repository;
    private final SeatSocketService socketService;
    private final RestClient controlPlaneClient;

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

    // ─── CREATE ─────────────────────────────────────────────────────────────────

    @Transactional
    public SeatDTO createSeat(CreateSeatRequest request) {
        validateStopRange(request.getFromStopOrder(), request.getToStopOrder());

        if (repository.existsByBusIdAndSeatNo(request.getBusId(), request.getSeatNo())) {
            throw new DuplicateSeatException(String.valueOf(request.getBusId()), request.getSeatNo());
        }

        Seat seat = Seat.builder()
                .busId(request.getBusId())
                .seatNo(request.getSeatNo())
                .type(request.getType())
                .price(request.getPrice())
                .fromStopOrder(request.getFromStopOrder())
                .toStopOrder(request.getToStopOrder())
                .status(SeatStatus.AVAILABLE)
                .build();

        return toDTO(repository.save(seat));
    }

    @Transactional
    public List<SeatDTO> createSeatsInBatch(CreateSeatsInBatchRequest request) {
        for (CreateSeatRequest r : request.getSeats()) {
            validateStopRange(r.getFromStopOrder(), r.getToStopOrder());
            if (repository.existsByBusIdAndSeatNo(r.getBusId(), r.getSeatNo())) {
                throw new DuplicateSeatException(String.valueOf(r.getBusId()), r.getSeatNo());
            }
        }

        List<Seat> seats = request.getSeats().stream()
                .map(r -> Seat.builder()
                        .busId(r.getBusId())
                        .seatNo(r.getSeatNo())
                        .type(r.getType())
                        .price(r.getPrice())
                        .fromStopOrder(r.getFromStopOrder())
                        .toStopOrder(r.getToStopOrder())
                        .status(SeatStatus.AVAILABLE)
                        .build())
                .toList();

        return repository.saveAll(seats).stream()
                .map(this::toDTO)
                .toList();
    }

    // ─── READ ────────────────────────────────────────────────────────────────────

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

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    @Transactional
    public SeatDTO updateSeat(Long seatId, UpdateSeatRequest request) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Cannot update seat " + seatId + " — it is currently " + seat.getStatus()
                            + ". Only AVAILABLE seats can be modified.");
        }

        if (request.getSeatNo() != null
                && !request.getSeatNo().equals(seat.getSeatNo())
                && repository.existsByBusIdAndSeatNo(seat.getBusId(), request.getSeatNo())) {
            throw new DuplicateSeatException(String.valueOf(seat.getBusId()), request.getSeatNo());
        }

        if (request.getSeatNo() != null)
            seat.setSeatNo(request.getSeatNo());
        if (request.getType() != null)
            seat.setType(request.getType());
        if (request.getPrice() != null)
            seat.setPrice(request.getPrice());

        return toDTO(repository.save(seat));
    }

    // ─── TOGGLE AVAILABILITY ───────────────────────────────────────────────────

    @Transactional
    public SeatDTO toggleAvailability(Long seatId) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        seat.setStatus(
                seat.getStatus() == SeatStatus.AVAILABLE
                        ? SeatStatus.NOT_AVAILABLE
                        : SeatStatus.AVAILABLE);

        return toDTO(repository.save(seat));
    }

    // ─── CANCEL BOOKING ──────────────────────────────────────────────────────────

    @Transactional
    public SeatDTO cancelBooking(Long seatId) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (seat.getStatus() != SeatStatus.NOT_AVAILABLE) {
            throw new SeatNotAvailableException(
                    "Seat " + seatId + " is not booked (current status: "
                            + seat.getStatus() + "). Nothing to cancel.");
        }

        seat.setStatus(SeatStatus.AVAILABLE);
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

    // ─── MAPPERS ─────────────────────────────────────────────────────────────────

    public SeatDTO toDTO(Seat seat) {
        return SeatDTO.builder()
                .seatId(seat.getSeatId())
                .busId(seat.getBusId())
                .seatNo(seat.getSeatNo())
                .type(seat.getType())
                .price(seat.getPrice())
                .fromStopOrder(seat.getFromStopOrder())
                .toStopOrder(seat.getToStopOrder())
                .status(seat.getStatus())
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

    // ─── HELPERS ─────────────────────────────────────────────────────────────────

    private void validateStopRange(int from, int to) {
        if (from >= to) {
            throw new InvalidStopRangeException(
                    "fromStopOrder (" + from + ") must be strictly less than toStopOrder (" + to + ").");
        }
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: net/vuega/vuega_backend/DTO/seats/seat/CreateSeatsInBatchRequest#