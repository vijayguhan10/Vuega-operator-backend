package net.vuega.control_plane.repository.heartbeats;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.control_plane.model.heartbeats.Heartbeat;

@Repository
public interface HeartbeatRepository extends JpaRepository<Heartbeat, Long> {

    List<Heartbeat> findByOperatorIdOrderByHeartbeatIdDesc(Long operatorId);

    boolean existsByOperatorId(Long operatorId);
}