package com.misha.sh.devicemanagementmicroservice.Controllers;

import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.*;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alaramMaintenence.AlarmMaintenanceDateResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm.AlarmRequest;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarm.AlarmResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarmSensitivity.AlarmSensitivityRequest;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.alarmSensitivity.AlarmSensitivityResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.modes.SmokeSensorDefaultEnergyConsumingModeResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.modes.SmokeSensorLowEnergyConsumingModeResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.turnOnAndOffSmokeSensor.SmokeSensorTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.smokeSensor.turnOnAndOffSmokeSensor.SmokeSensorTurnOnResponse;
import com.misha.sh.devicemanagementmicroservice.service.smokeService.SmokeSensorService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("smoke-sensor")
public class SmokeSensorController {

    private final SmokeSensorService smokeSensorService;

    @PostMapping
    public ResponseEntity<SmokeSensorResponse> addNewSmokeSensor(@RequestBody @Valid SmokeSensorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(smokeSensorService.addNewSmokeSensor(request));
    }

    @GetMapping("/{smokeSensorId}")
    public ResponseEntity<SmokeSensorResponse> findSensorById(@PathVariable Integer smokeSensorId, Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.findSensorById(smokeSensorId, authentication));
    }

    @DeleteMapping("/{smokeSensorId}")
    public ResponseEntity<Void> removeSmokeSensor(@PathVariable Integer smokeSensorId, Authentication authentication) {
        smokeSensorService.removeSmokeSensor(smokeSensorId, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<SmokeSensorResponse>> findAllSmokeSensors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.findAllSmokeSensors(page, size, authentication));
    }

    @PostMapping("/{smokeSensorId}/alarm")
    public ResponseEntity<AlarmResponse> turnOnAlarmSmokeSensorLevelHigh(
            @PathVariable Integer smokeSensorId,
            @RequestBody @Valid AlarmRequest request,
            Authentication authentication) throws MessagingException {
        return ResponseEntity.ok(smokeSensorService.turnOnAlarmSmokeSensorLevelHigh(smokeSensorId, request, authentication));
    }

    @PostMapping("/{smokeSensorId}/alarm/on")
    public ResponseEntity<Void> turnOnAlarmThroughApplication(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) throws MessagingException {
        smokeSensorService.turnOnAlarmThroughApplication(authentication, smokeSensorId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{smokeSensorId}/alarm/off")
    public ResponseEntity<Void> turnOffAlarmThroughApplication(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) {
        smokeSensorService.turnOffAlarmThroughApplication(smokeSensorId, authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{smokeSensorId}/sensitivity")
    public ResponseEntity<AlarmSensitivityResponse> setAlarmSensitivity(
            @PathVariable Integer smokeSensorId,
            @RequestBody @Valid AlarmSensitivityRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.setAlarmSensitivity(request, smokeSensorId, authentication));
    }

    @GetMapping("/{smokeSensorId}/maintenance")
    public ResponseEntity<AlarmMaintenanceDateResponse> showLastMaintenanceDate(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.showLastMaintenanceDate(smokeSensorId, authentication));
    }

    @PostMapping("/{smokeSensorId}/maintenance")
    public ResponseEntity<AlarmMaintenanceDateResponse> createLastMaintenanceDate(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.createLastMaintenanceDate(smokeSensorId, authentication));
    }

    @PostMapping("/{smokeSensorId}/turn-on")
    public ResponseEntity<SmokeSensorTurnOnResponse> turnOnSmokeSensor(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.turnOnSmokeSensor(smokeSensorId, authentication));
    }

    @PostMapping("/{smokeSensorId}/turn-off")
    public ResponseEntity<SmokeSensorTurnOffResponse> turnOffSmokeSensor(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.turnOffSmokeSensor(smokeSensorId, authentication));
    }

    @PostMapping("/{smokeSensorId}/low-energy")
    public ResponseEntity<SmokeSensorLowEnergyConsumingModeResponse> setLowEnergyConsumingMode(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.setLowEnergyConsumingMode(smokeSensorId, authentication));
    }

    @PostMapping("/{smokeSensorId}/default-energy")
    public ResponseEntity<SmokeSensorDefaultEnergyConsumingModeResponse> setDefaultEnergyConsumingMode(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.setDefaultEnergyConsumingMode(smokeSensorId, authentication));
    }

    @GetMapping("/{smokeSensorId}/self-test")
    public ResponseEntity<Boolean> selfTestPassed(
            @PathVariable Integer smokeSensorId,
            Authentication authentication) {
        return ResponseEntity.ok(smokeSensorService.selfTestPassed(smokeSensorId, authentication));
    }
}