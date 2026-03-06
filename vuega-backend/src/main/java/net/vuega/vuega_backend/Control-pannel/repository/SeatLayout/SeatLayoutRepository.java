package net.vuega.control_plane.repository.seatlayout;

import org.springframework.data.jpa.repository.JpaRepository;

import net.vuega.control_plane.model.seatlayout.SeatLayout;

public interface SeatLayoutRepository extends JpaRepository<SeatLayout, Long> {
}
