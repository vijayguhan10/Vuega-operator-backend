package net.vuega.control_plane.service.buses;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.vuega.control_plane.dto.buses.BusesDto;
import net.vuega.control_plane.model.buses.Buses;
import net.vuega.control_plane.model.seatlayout.SeatLayout;
import net.vuega.control_plane.repository.buses.BusesRepository;
import net.vuega.control_plane.repository.operators.OperatorRepository;
import net.vuega.control_plane.repository.seatlayout.SeatLayoutRepository;
import net.vuega.control_plane.util.BusStatus;

@Service
@Transactional
public class BusesService {

    private final BusesRepository busRepository;
    private final OperatorRepository operatorRepository;
    private final SeatLayoutRepository seatLayoutRepository;

    public BusesService(BusesRepository busRepository,
            OperatorRepository operatorRepository,
            SeatLayoutRepository seatLayoutRepository) {
        this.busRepository = busRepository;
        this.operatorRepository = operatorRepository;
        this.seatLayoutRepository = seatLayoutRepository;
    }

    // ================= CREATE =================
    @Transactional
    public BusesDto createBus(BusesDto dto) {

        validate(dto);

        // Check operator exists
        if (!operatorRepository.existsById(dto.getOperatorId())) {
            throw new RuntimeException("Operator not found with id: " + dto.getOperatorId());
        }

        // Unique bus number check
        if (busRepository.existsByBusNumber(dto.getBusNumber())) {
            throw new IllegalArgumentException("Bus number already exists");
        }

        // Create SeatLayout first
        SeatLayout seatLayout = new SeatLayout();
        seatLayout.setLayoutName("Layout " + dto.getBusNumber());
        seatLayout.setSeatCount(dto.getSeatCount());
        SeatLayout savedLayout = seatLayoutRepository.save(seatLayout);

        Buses bus = new Buses();
        bus.setOperatorId(dto.getOperatorId());
        bus.setBusNumber(dto.getBusNumber());
        bus.setBusType(dto.getBusType());
        bus.setLayoutId(savedLayout.getLayoutId());
        bus.setStatus(dto.getStatus());

        Buses saved = busRepository.save(bus);

        return convertToDto(saved, dto.getSeatCount());
    }

    // ================= GET ALL =================
    @Transactional(readOnly = true)
    public List<BusesDto> getAllBuses() {
        return busRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ================= GET BY ID =================
    @Transactional(readOnly = true)
    public BusesDto getBusById(Long id) {

        Buses bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));

        return convertToDto(bus);
    }

    // ================= UPDATE =================
    @Transactional
    public BusesDto updateBus(Long id, BusesDto dto) {

        validateForUpdate(dto);

        Buses bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));

        // Unique bus number check (only if changed)
        if (!bus.getBusNumber().equals(dto.getBusNumber())
                && busRepository.existsByBusNumber(dto.getBusNumber())) {
            throw new IllegalArgumentException("Bus number already exists");
        }

        // Update seat layout if seat count changed
        SeatLayout existingLayout = seatLayoutRepository.findById(bus.getLayoutId())
                .orElseThrow(() -> new RuntimeException("Seat layout not found"));

        if (dto.getSeatCount() != existingLayout.getSeatCount()) {
            existingLayout.setSeatCount(dto.getSeatCount());
            seatLayoutRepository.save(existingLayout);
        }

        bus.setBusNumber(dto.getBusNumber());
        bus.setBusType(dto.getBusType());
        bus.setStatus(dto.getStatus());

        Buses updated = busRepository.save(bus);

        return convertToDto(updated, dto.getSeatCount());
    }

    // ================= SOFT DELETE =================
    @Transactional
    public void deactivateBus(Long id) {

        Buses bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found with id: " + id));

        if (bus.getStatus() == BusStatus.INACTIVE) {
            throw new IllegalStateException("Bus already inactive");
        }

        bus.setStatus(BusStatus.INACTIVE);
        busRepository.save(bus);
    }

    // ================= VALIDATION =================
    private void validate(BusesDto dto) {

        if (dto == null)
            throw new IllegalArgumentException("Bus cannot be null");

        // if (dto.getOperatorId() == null)
        // throw new IllegalArgumentException("Operator ID is required");

        if (dto.getBusNumber() == null || dto.getBusNumber().isBlank())
            throw new IllegalArgumentException("Bus number is required");

        if (dto.getBusType() == null)
            throw new IllegalArgumentException("Bus type is required");

        if (dto.getSeatCount() <= 0)
            throw new IllegalArgumentException("Seat count must be greater than 0");

        // if (dto.getLayoutId() == null)
        // throw new IllegalArgumentException("Layout ID is required");

        if (dto.getStatus() == null)
            throw new IllegalArgumentException("Status is required");
    }

    private void validateForUpdate(BusesDto dto) {

        if (dto == null)
            throw new IllegalArgumentException("Bus cannot be null");

        if (dto.getBusNumber() == null || dto.getBusNumber().isBlank())
            throw new IllegalArgumentException("Bus number is required");

        if (dto.getBusType() == null)
            throw new IllegalArgumentException("Bus type is required");

        if (dto.getSeatCount() <= 0)
            throw new IllegalArgumentException("Seat count must be greater than 0");

        if (dto.getStatus() == null)
            throw new IllegalArgumentException("Status is required");
    }

    // ================= MAPPER =================
    private BusesDto convertToDto(Buses bus) {
        SeatLayout layout = seatLayoutRepository.findById(bus.getLayoutId())
                .orElse(null);
        int seatCount = layout != null ? layout.getSeatCount() : 0;
        return convertToDto(bus, seatCount);
    }

    private BusesDto convertToDto(Buses bus, int seatCount) {
        return new BusesDto(
                bus.getBusId(),
                bus.getOperatorId(),
                bus.getBusNumber(),
                bus.getBusType(),
                seatCount,
                bus.getLayoutId(),
                bus.getStatus());
    }
}
