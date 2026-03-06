package net.vuega.control_plane.controller.buses;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.vuega.control_plane.dto.buses.BusesDto;
import net.vuega.control_plane.dto.common.ApiResponse;
import net.vuega.control_plane.service.buses.BusesService;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusesService busService;

    public BusController(BusesService busService) {
        this.busService = busService;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<BusesDto>> createBus(@RequestBody BusesDto dto) {
        BusesDto created = busService.createBus(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        HttpStatus.CREATED.value(),
                        "Bus created successfully",
                        created));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<BusesDto>>> getAllBuses() {
        List<BusesDto> buses = busService.getAllBuses();
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Buses fetched successfully",
                buses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusesDto>> getBus(@PathVariable Long id) {
        BusesDto bus = busService.getBusById(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Bus fetched successfully",
                bus));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BusesDto>> updateBus(
            @PathVariable Long id,
            @RequestBody BusesDto dto) {
        BusesDto updated = busService.updateBus(id, dto);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Bus updated successfully",
                updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateBus(@PathVariable Long id) {
        busService.deactivateBus(id);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Bus deactivated successfully",
                null));
    }
}
