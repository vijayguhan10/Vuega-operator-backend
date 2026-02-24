package net.vuega.vuega_backend.Model.passengers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.vuega.vuega_backend.Model.bookings.Booking;

@Entity
@Table(name = "passengers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private Long passengerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;
}
