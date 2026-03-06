package net.vuega.control_plane.service.heartbeats;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.vuega.control_plane.dto.heartbeats.HeartBeatDto;
import net.vuega.control_plane.model.heartbeats.Heartbeat;
import net.vuega.control_plane.repository.heartbeats.HeartbeatRepository;
import net.vuega.control_plane.repository.operators.OperatorRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class HeartBeatService {

    private final HeartbeatRepository repository;
    private final OperatorRepository operatorRepository;

    // ================= RECEIVE HEARTBEAT =================
    public HeartBeatDto receiveHeartbeat(HeartBeatDto dto) {

        if (dto == null)
            throw new IllegalArgumentException("Heartbeat cannot be null");

        if (dto.getOperatorId() == null)
            throw new IllegalArgumentException("Operator ID is required");

        if (!operatorRepository.existsById(dto.getOperatorId()))
            throw new RuntimeException("Operator not found");

        if (dto.getBusCount() == null || dto.getBusCount() < 0)
            throw new IllegalArgumentException("Invalid bus count");

        if (dto.getRouteCount() == null || dto.getRouteCount() < 0)
            throw new IllegalArgumentException("Invalid route count");

        if (dto.getTripCount() == null || dto.getTripCount() < 0)
            throw new IllegalArgumentException("Invalid trip count");

        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setOperatorId(dto.getOperatorId());
        heartbeat.setBusCount(dto.getBusCount());
        heartbeat.setRouteCount(dto.getRouteCount());
        heartbeat.setTripCount(dto.getTripCount());

        Heartbeat saved = repository.save(heartbeat);

        return convertToDto(saved);
    }

    // ================= GET BY OPERATOR =================
    @Transactional(readOnly = true)
    public List<HeartBeatDto> getByOperator(Long operatorId) {

        if (!operatorRepository.existsById(operatorId))
            throw new RuntimeException("Operator not found");

        return repository.findByOperatorIdOrderByHeartbeatIdDesc(operatorId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private HeartBeatDto convertToDto(Heartbeat heartbeat) {

        return new HeartBeatDto(
                heartbeat.getHeartbeatId(),
                heartbeat.getOperatorId(),
                heartbeat.getBusCount(),
                heartbeat.getRouteCount(),
                heartbeat.getTripCount());
    }
}