package com.misha.sh.devicemanagementmicroservice.Controllers;

import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor.WeatherSensorRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.addSensor.WeatherSensorResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor.WeatherSensorEnergyRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.energyConsumingSensor.WeatherSensorEnergyResponse;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData.WeatherDataRequest;
import com.misha.sh.devicemanagementmicroservice.request.weatherSensor.weatherData.WeatherDataResponse;
import com.misha.sh.devicemanagementmicroservice.service.weatherSensor.WeatherSensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weatherSensor")
public class WeatherSensorController {

    private final WeatherSensorService weatherSensorService;

    @GetMapping
    public ResponseEntity<PageResponse<WeatherSensorResponse>> findAllAvailableSensors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<WeatherSensorResponse> response = weatherSensorService.findAllAvailableSensors(size, page, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeatherSensorResponse> findSensorById(@PathVariable Integer id, Authentication authentication) {
        WeatherSensorResponse response = weatherSensorService.findSensorById(id, authentication);
        return ResponseEntity.ok(response);
    }



    @PostMapping
    public ResponseEntity<WeatherSensorResponse> addSensor(
            @RequestBody WeatherSensorRequest request,
            Authentication authentication) {
        WeatherSensorResponse response = weatherSensorService.addSensor(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSensor(@PathVariable Integer id, Authentication authentication) {
        weatherSensorService.removeSensor(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sensorId}/energy")
    public ResponseEntity<WeatherSensorEnergyResponse> getWeatherSensorEnergyConsuming(@PathVariable Integer sensorId, Authentication authentication) {
        WeatherSensorEnergyResponse response = weatherSensorService.getWeatherSensorEnergyConsuming(sensorId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{sensorId}/data")
    public ResponseEntity<WeatherDataResponse> getWeatherWeatherData(@PathVariable Integer sensorId, Authentication authentication) {
        WeatherDataResponse response = weatherSensorService.findWeatherData(sensorId, authentication);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{sensorId}/energy")
    public ResponseEntity<WeatherSensorEnergyResponse> changeOnCustomEnergyConsuming(
            @RequestBody WeatherSensorEnergyRequest request,
            @PathVariable Integer sensorId,
            Authentication authentication) {
        WeatherSensorEnergyResponse response = weatherSensorService.changeOnCustomEnergyConsuming(request, sensorId, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{sensorId}/data")
    public ResponseEntity<WeatherDataResponse> sendAndUpdateWeatherData(
            @RequestBody WeatherDataRequest request,
            @PathVariable Integer sensorId,
            Authentication authentication) {
        WeatherDataResponse response = weatherSensorService.sendAndUpdateWeatherData(request, sensorId, authentication);
        return ResponseEntity.ok(response);
    }

}
