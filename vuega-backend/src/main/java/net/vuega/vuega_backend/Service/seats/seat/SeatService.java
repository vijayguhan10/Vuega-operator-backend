package net.vuega.vuega_backend.Service.seats.seat;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import net.vuega.vuega_backend.DTO.seats.seat.CreateSeatRequest;
import net.vuega.vuega_backend.DTO.seats.seat.CreateSeatsInBatchRequest;
import net.vuega.vuega_backend.DTO.seats.seat.SeatDTO;
import net.vuega.vuega_backend.DTO.seats.seat.UpdateSeatRequest;
import net.vuega.vuega_backend.Exception.DuplicateSeatException;
import net.vuega.vuega_backend.Exception.SeatNotFoundException;
import net.vuega.vuega_backend.Model.seats.seat.Seat;
import net.vuega.vuega_backend.Repository.seats.seat.SeatRepository;

@Service
@Slf4j
public class SeatService {

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

    @Transactional
    public SeatDTO createSeat(CreateSeatRequest request) {
        if (repository.existsByBusIdAndSeatNo(request.getBusId(), request.getSeatNo())) {
            throw new DuplicateSeatException(String.valueOf(request.getBusId()), request.getSeatNo());
        }

        Seat seat = Seat.builder()
                .busId(request.getBusId())
                .seatNo(request.getSeatNo())
                .type(request.getType())
                .basePrice(request.getBasePrice())
                .build();

        return toDTO(repository.save(seat));
    }

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
    public List<SeatDTO> getAvailableSeatsForSegment(Long busId, Long scheduleId, int fromStop, int toStop) {
        return repository.findAvailableSeatsForSegment(busId, scheduleId, fromStop, toStop).stream()
                .map(this::toDTO)
                .toList();
    }

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