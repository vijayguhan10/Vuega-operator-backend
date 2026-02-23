package net.vuega.vuega_backend.Repository.seats.lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.vuega.vuega_backend.Model.seats.lock.SeatLock;

@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {

    Optional<SeatLock> findBySeat_SeatId(Long seatId);

    Optional<SeatLock> findBySeat_SeatIdAndPartnerId(Long seatId, Long partnerId);

    boolean existsBySeat_SeatId(Long seatId);

    List<SeatLock> findByExpiresAtBefore(LocalDateTime now);

    @Modifying
    @Query("DELETE FROM SeatLock sl WHERE sl.expiresAt < :now")
    int deleteExpiredLocks(@Param("now") LocalDateTime now);
}
