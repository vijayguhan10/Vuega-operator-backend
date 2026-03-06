package net.vuega.vuega_backend.Control_pannel.repository.buses;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Control_pannel.model.buses.Buses;
import net.vuega.vuega_backend.Control_pannel.util.BusStatus;

@Repository
public interface BusesRepository extends JpaRepository<Buses, Long> {

    boolean existsByBusNumber(String busNumber);

    List<Buses> findByStatus(BusStatus status);

    List<Buses> findByOperatorId(Long operatorId);

    long countByOperatorIdAndStatus(Long operatorId, BusStatus status);
}
