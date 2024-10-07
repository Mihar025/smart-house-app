package com.misha.sh.devicemanagementmicroservice.Controllers;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.battery.DeviceBatteryResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOffRequest;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOnRequest;
import com.misha.sh.devicemanagementmicroservice.request.device.condition.DeviceTurnOnResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.devReq.DeviceRequest;
import com.misha.sh.devicemanagementmicroservice.request.device.devReq.DeviceResponse;
import com.misha.sh.devicemanagementmicroservice.request.device.devTechReq.DeviceTechnicalResponse;
import com.misha.sh.devicemanagementmicroservice.service.deviceService.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PutMapping("/edit")
    public ResponseEntity<Void> editDevice(@RequestBody DeviceRequest deviceRequest) {
        deviceService.editDevice(deviceRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable Integer id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<DeviceResponse>> getAllDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        return ResponseEntity.ok(deviceService.getAllDevices(size, page, authentication));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<DeviceResponse> findDeviceById(@PathVariable Integer id, Authentication authentication) {
        return ResponseEntity.ok(deviceService.findDeviceById(id, authentication));
    }

    @PostMapping("/{deviceId}/turn-on")
    public ResponseEntity<DeviceTurnOnResponse> turnOnDevice(
            @RequestBody DeviceTurnOnRequest request,
            @PathVariable Integer deviceId,
            @RequestParam String defaultMode) {
        return ResponseEntity.ok(deviceService.turnOnDevice(request, deviceId, defaultMode));
    }

    @PostMapping("/{deviceId}/turn-off")
    public ResponseEntity<DeviceTurnOffResponse> turnOffDevice(
            @RequestBody DeviceTurnOffRequest request,
            @PathVariable Integer deviceId) {
        return ResponseEntity.ok(deviceService.turnOffDevice(request, deviceId));
    }

    @GetMapping("/{deviceId}/energy")
    public ResponseEntity<DeviceTechnicalResponse> getEnergyConsuming(@PathVariable Integer deviceId) {
        return ResponseEntity.ok(deviceService.getEnergyConsuming(deviceId));
    }

    @GetMapping("/{deviceId}/battery")
    public ResponseEntity<DeviceBatteryResponse> getBatteryLevel(@PathVariable Integer deviceId) {
        return ResponseEntity.ok(deviceService.getBatteryLevel(deviceId));
    }

    @PostMapping("/{deviceId}/low-battery")
    public ResponseEntity<DeviceBatteryResponse> lowBatteryMode(
            @PathVariable Integer deviceId,
            @RequestParam String lowMode) {
        return ResponseEntity.ok(deviceService.lowBatteryMode(deviceId, lowMode));
    }


}
