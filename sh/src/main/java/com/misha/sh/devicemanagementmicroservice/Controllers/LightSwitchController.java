package com.misha.sh.devicemanagementmicroservice.Controllers;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.addSwitch.LightSwitchRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.addSwitch.LightSwitchResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.BrightnessRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.BrightnessResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.ColorTemperatureRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.brightness.ColorTemperatureResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOff.SwitchTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOn.SwitchTurnOnRequest;
import com.misha.sh.devicemanagementmicroservice.request.lightSwitch.switchTurnOn.SwitchTurnOnResponse;
import com.misha.sh.devicemanagementmicroservice.service.lightSwitchService.LightSwitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lightSwitch")
public class LightSwitchController {

    private final LightSwitchService lightSwitchService;
    @GetMapping
    //working
    public ResponseEntity<PageResponse<LightSwitchResponse>> getAllSwitches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<LightSwitchResponse> response = lightSwitchService.findAllSwitches(size, page, authentication);
        return ResponseEntity.ok(response);
    }

    //working
    @GetMapping("/{switchId}")
    public ResponseEntity<LightSwitchResponse> getSwitchById(@PathVariable Integer switchId, Authentication authentication) {
        LightSwitchResponse response = lightSwitchService.findSwitchById(switchId, authentication);
        return ResponseEntity.ok(response);
    }
    // working
    @PostMapping("/{switchId}/turn-on")
    public ResponseEntity<SwitchTurnOnResponse> turnOnSwitch(
            @PathVariable Integer switchId,
            Authentication authentication
           ) throws BusinessException {
        SwitchTurnOnResponse response = lightSwitchService.turnOnLightSwitch(switchId, authentication);
        return ResponseEntity.ok(response);
    }
    //working
    @PostMapping("/{switchId}/preferences")
    public ResponseEntity<SwitchTurnOnResponse> setUserPreferences(
            @PathVariable Integer switchId,
            @RequestBody SwitchTurnOnRequest request,
            Authentication authentication) {
        SwitchTurnOnResponse response = lightSwitchService.userLightSwitchPreferences(switchId, request, authentication);
        return ResponseEntity.ok(response);
    }
    //working
    @PostMapping("/{switchId}/turn-off")
    public ResponseEntity<SwitchTurnOffResponse> turnOffSwitch(@PathVariable Integer switchId, Authentication authentication) {
        SwitchTurnOffResponse response = lightSwitchService.switchTurnOff(switchId, authentication);
        return ResponseEntity.ok(response);
    }

    // working
    @PutMapping("/{switchId}/brightness")
    public ResponseEntity<BrightnessResponse> changeBrightness(
            @RequestBody BrightnessRequest request,
            @PathVariable Integer switchId,
            Authentication authentication) {
        BrightnessResponse response = lightSwitchService.changeBrightness(request, switchId, authentication);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{switchId}/color-temperature")
    public ResponseEntity<ColorTemperatureResponse> changeColorTemperature(
            @RequestBody ColorTemperatureRequest request,
            @PathVariable Integer switchId,
            Authentication authentication) {
        ColorTemperatureResponse response = lightSwitchService.changeColorTemperature(request, switchId, authentication);
        return ResponseEntity.ok(response);
    }

    //working
    @PostMapping
    public ResponseEntity<LightSwitchResponse> addLightSwitch(
            @RequestBody LightSwitchRequest request,
            Authentication authentication) {
        LightSwitchResponse response = lightSwitchService.addSwitchLight(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//working
    @DeleteMapping("/{switchId}")
    public ResponseEntity<Void> deleteLightSwitch(@PathVariable Integer switchId,
                                                     Authentication authentication) {
        lightSwitchService.deleteSwitchLight(switchId, authentication);
        return ResponseEntity.noContent().build();
    }

}
