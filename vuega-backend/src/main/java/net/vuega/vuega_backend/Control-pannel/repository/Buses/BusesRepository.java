package net.vuega.control_plane.repository.buses;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.control_plane.model.buses.Buses;
import net.vuega.control_plane.util.BusStatus;

@Repository
public interface BusesRepository extends JpaRepository<Buses, Long> {

    boolean existsByBusNumber(String busNumber);

    List<Buses> findByStatus(BusStatus status);

    List<Buses> findByOperatorId(Long operatorId);

    long countByOperatorIdAndStatus(Long operatorId, BusStatus status);
}
