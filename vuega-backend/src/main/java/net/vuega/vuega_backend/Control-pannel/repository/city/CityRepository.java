package net.vuega.control_plane.repository.city;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.vuega.control_plane.model.city.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    // Check if city exists by cityName (case-insensitive)
    boolean existsByCityNameIgnoreCase(String cityName);

    // Find city by exact cityName (case-insensitive)
    Optional<City> findByCityNameIgnoreCase(String cityName);

    // Search cities containing text (for dropdown search)
    List<City> findByCityNameContainingIgnoreCase(String cityName);

    // Find cities by state
    List<City> findByStateIgnoreCase(String state);
}
