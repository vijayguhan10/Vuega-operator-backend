error id: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/seats/seat/SeatService.java:java/lang/String#
file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/seats/seat/SeatService.java
empty definition using pc, found symbol in pc: java/lang/String#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1612
uri: file://<WORKSPACE>/src/main/java/net/vuega/vuega_backend/Operator_pannel/Service/seats/seat/SeatService.java
text:
```scala
package net.vuega.vuega_backend.Operator_pannel.Service.seats.seat;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.Operator_pannel.DTO.seats.seat.CreateSeatRequest;
import net.vuega.vuega_backend.Operator_pannel.DTO.seats.seat.CreateSeatsInBatchRequest;
import net.vuega.vuega_backend.Operator_pannel.DTO.seats.seat.SeatDTO;
import net.vuega.vuega_backend.Operator_pannel.DTO.seats.seat.UpdateSeatRequest;
import net.vuega.vuega_backend.Operator_pannel.Model.seats.seat.Seat;
import net.vuega.vuega_backend.Operator_pannel.Repository.seats.seat.SeatRepository;
import net.vuega.vuega_backend.Operator_pannel.Service.cache.ControlPanelCacheService;
import net.vuega.vuega_backend.Operator_pannel.exception.DuplicateSeatException;
import net.vuega.vuega_backend.Operator_pannel.exception.SeatNotFoundException;

@Service
@Slf4j
public class SeatService {

    private final SeatRepository repository;
    private final ControlPanelCacheService cacheService;

    public SeatService(
            SeatRepository repository,
            ControlPanelCacheService cacheService) {
        this.repository = repository;
        this.cacheService = cacheService;
    }

    // Creates a single seat after checking for duplicate seat numbers on the bus.
    @Transactional
    public SeatDTO createSeat(CreateSeatRequest request) {
        if (repository.existsByBusIdAndSeatNo(request.getBusId(), request.getSeatNo())) {
            throw new DuplicateSeatException(@@String.valueOf(request.getBusId()), request.getSeatNo());
        }

        Seat seat = Seat.builder()
                .busId(request.getBusId())
                .seatNo(request.getSeatNo())
                .type(request.getType())
                .basePrice(request.getBasePrice())
                .build();

        return toDTO(repository.save(seat));
    }

    // Creates multiple seats in a single transaction; fails if any duplicate
    // exists.
    @Transactional
    public List<SeatDTO> createSeatsInBatch(CreateSeatsInBatchRequest request) {
        for (CreateSeatRequest r : request.getSeats()) {
            if (repository.existsByBusIdAndSeatNo(r.getBusId(), r.getSeatNo())) {
                throw new DuplicateSeatException(String.valueOf(r.getBusId()), r.getSeatNo());
            }
        }

        List<Seat> seats = request.getSeats().stream()
                .map(r -> Seat.builder()
                        .busId(r.getBusId())
                        .seatNo(r.getSeatNo())
                        .type(r.getType())
                        .basePrice(r.getBasePrice())
                        .build())
                .toList();

        return repository.saveAll(seats).stream()
                .map(this::toDTO)
                .toList();
    }

    // Fetches a seat by ID, enriched with bus details from Control Plane.
    @Transactional(readOnly = true)
    public SeatDTO getSeatById(Long seatId) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));
        return toDTOEnriched(seat);
    }

    // Returns all seats for a given bus, enriched with bus details.
    @Transactional(readOnly = true)
    public List<SeatDTO> getSeatsByBus(Long busId) {
        return repository.findByBusId(busId).stream()
                .map(this::toDTOEnriched)
                .toList();
    }

    // Returns seats not booked for the given schedule segment (fromStop to toStop).
    @Transactional(readOnly = true)
    public List<SeatDTO> getAvailableSeatsForSegment(Long busId, Long scheduleId, int fromStop, int toStop) {
        return repository.findAvailableSeatsForSegment(busId, scheduleId, fromStop, toStop).stream()
                .map(this::toDTO)
                .toList();
    }

    // Partially updates seat fields (seatNo, type, basePrice); validates no
    // duplicate.
    @Transactional
    public SeatDTO updateSeat(Long seatId, UpdateSeatRequest request) {
        Seat seat = repository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException(seatId));

        if (request.getSeatNo() != null
                && !request.getSeatNo().equals(seat.getSeatNo())
                && repository.existsByBusIdAndSeatNo(seat.getBusId(), request.getSeatNo())) {
            throw new DuplicateSeatException(String.valueOf(seat.getBusId()), request.getSeatNo());
        }

        if (request.getSeatNo() != null)
            seat.setSeatNo(request.getSeatNo());
        if (request.getType() != null)
            seat.setType(request.getType());
        if (request.getBasePrice() != null)
            seat.setBasePrice(request.getBasePrice());

        return toDTO(repository.save(seat));
    }

    public SeatDTO toDTO(Seat seat) {
        return SeatDTO.builder()
                .seatId(seat.getSeatId())
                .busId(seat.getBusId())
                .seatNo(seat.getSeatNo())
                .type(seat.getType())
                .basePrice(seat.getBasePrice())
                .build();
    }

    private SeatDTO toDTOEnriched(Seat seat) {
        SeatDTO dto = toDTO(seat);
        dto.setBusDetails(cacheService.getBusDetails(seat.getBusId()));
        return dto;
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: java/lang/String#