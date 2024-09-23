package com.misha.sh.devicemanagementmicroservice.Controllers;

import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorDataRequest;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorDataResponse;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.airQualitySensor.AirQualitySensorResponse;
import com.misha.sh.devicemanagementmicroservice.service.airQualitySensorService.AirQualitySensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/air-quality-sensor")
public class AirQualitySensorController {
    private final AirQualitySensorService airQualitySensorService;

    @PostMapping
    public ResponseEntity<AirQualitySensorResponse> addNewAirQualitySensor(
            @RequestBody AirQualitySensorRequest request,
            Authentication authentication) {
        AirQualitySensorResponse response = airQualitySensorService.addNewAirQualitySensor(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{sensorId}")
    public ResponseEntity<Void> removeAirQualitySensor(
            @PathVariable Integer sensorId,
            Authentication authentication) {
        airQualitySensorService.removeAirQualitySensor(sensorId, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sensorId}")
    public ResponseEntity<AirQualitySensorResponse> findAirQualitySensorById(
            @PathVariable Integer sensorId,
            Authentication authentication) {
        AirQualitySensorResponse response = airQualitySensorService.findAirQualitySensorById(sensorId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<AirQualitySensorResponse>> findAllAirQualitySensors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<AirQualitySensorResponse> response = airQualitySensorService.findAllAirQualitySensors(page, size, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sensorId}/air-data")
    public ResponseEntity<AirQualitySensorDataResponse> sendAirData(
            @PathVariable Integer sensorId,
            @RequestBody AirQualitySensorDataRequest request,
            Authentication authentication) {
        AirQualitySensorDataResponse response = airQualitySensorService.sendAirData(sensorId, request, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sensorId}/air-data")
    public ResponseEntity<AirQualitySensorResponse> getAllAirData(
            @PathVariable Integer sensorId,
            Authentication authentication) {
        AirQualitySensorResponse response = airQualitySensorService.getAllAirData(sensorId, authentication);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sensorId}/low-power-mode")
    public ResponseEntity<Void> setLowPowerMode(
            @PathVariable Integer sensorId,
            Authentication authentication) {
        airQualitySensorService.setLowPowerMode(sensorId, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{sensorId}/default-power-mode")
    public ResponseEntity<Void> setDefaultPowerMode(
            @PathVariable Integer sensorId,
            Authentication authentication) {
        airQualitySensorService.setDefaultPowerMode(sensorId, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{sensorId}/turn-on")
    public ResponseEntity<Void> turnOnAirQualitySensor(
            @PathVariable Integer sensorId,
            Authentication authentication) {
        airQualitySensorService.turnOnAirQualitySensor(sensorId, authentication);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{sensorId}/turn-off")
    public ResponseEntity<Void> turnOffAirQualitySensor(
            @PathVariable Integer sensorId,
            Authentication authentication) {
        airQualitySensorService.turnOffAirQualitySensor(sensorId, authentication);
        return ResponseEntity.ok().build();
    }

}
