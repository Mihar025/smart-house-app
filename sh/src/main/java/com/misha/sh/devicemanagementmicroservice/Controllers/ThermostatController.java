package com.misha.sh.devicemanagementmicroservice.Controllers;

import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat.ThermostatRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.addThermostat.ThermostatResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatCoolingModeRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatCoolingModeResponse;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatHeatModeRequest;
import com.misha.sh.devicemanagementmicroservice.request.thermostat.temperatureMode.ThermostatHeatModeResponse;
import com.misha.sh.devicemanagementmicroservice.service.thermostatService.ThermostatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

/**
 * REST controller for managing thermostat operations.
 * This controller handles HTTP requests related to thermostats, including
 * adding, retrieving, removing, and controlling thermostat modes.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/thermostat")
@Api(tags = "Thermostat Management")
public class ThermostatController {
    private final ThermostatService thermostatService;

    /**
     * Adds a new thermostat to the system.
     *
     * @param request The thermostat details
     * @param authentication The authentication object of the current user
     * @return ResponseEntity with the created ThermostatResponse and HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<ThermostatResponse> addThermostat(@RequestBody ThermostatRequest request, Authentication authentication) {
        ThermostatResponse response = thermostatService.addThermostat(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /**
     * Retrieves a paginated list of all thermostats for the authenticated user.
     *
     * @param authentication The authentication object of the current user
     * @param page The page number (default: 0)
     * @param size The number of items per page (default: 10)
     * @return ResponseEntity with PageResponse containing a list of ThermostatResponse objects
     */
    @GetMapping
    public ResponseEntity<PageResponse<ThermostatResponse>> findAllUserThermostats(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ThermostatResponse> response = thermostatService.findAllUserThermostats(page, size, authentication);
        return ResponseEntity.ok(response);
    }

    /**
     * Finds a specific thermostat by its ID.
     *
     * @param thermostatId The ID of the thermostat to find
     * @param authentication The authentication object of the current user
     * @return ResponseEntity with ThermostatResponse object
     */
    @GetMapping("/{thermostatId}")
    public ResponseEntity<ThermostatResponse> findThermostatById(
            @PathVariable  Integer thermostatId,
            Authentication authentication) {
        ThermostatResponse response = thermostatService.findThermostatById(thermostatId, authentication);
        return ResponseEntity.ok(response);
    }

    /**
     * Removes a thermostat from the system.
     *
     * @param thermostatId The ID of the thermostat to remove
     * @param authentication The authentication object of the current user
     * @return ResponseEntity with no content and HTTP status 204 (No Content)
     */
    @DeleteMapping("/{thermostatId}")
    public ResponseEntity<Void> removeThermostat(
            @PathVariable  Integer thermostatId,
            Authentication authentication) {
        thermostatService.removeThermostat(thermostatId, authentication);
        return ResponseEntity.noContent().build();
    }

    /**
     * Turns off a specific thermostat.
     *
     * @param thermostatId The ID of the thermostat to turn off
     * @param authentication The authentication object of the current user
     * @return ResponseEntity with no content and HTTP status 200 (OK)
     */
    @PostMapping("/{thermostatId}/turn-off")
    public ResponseEntity<Void> turnOffThermostat(
            @PathVariable Integer thermostatId,
            Authentication authentication) {
        thermostatService.turnOffThermostat(thermostatId, authentication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{thermostatId}/turn-on")
    public ResponseEntity<Void> turnOnThermostat(@PathVariable("thermostatId") Integer thermostatId,
                                                 Authentication authentication) {
        thermostatService.turnOnThermostat(thermostatId,authentication);
        return ResponseEntity.ok().build();
    }

    /**
     * Sets a thermostat to cooling mode.
     *
     * @param request The cooling mode details
     * @param thermostatId The ID of the thermostat to set to cooling mode
     * @param authentication The authentication object of the current user
     * @return ResponseEntity with ThermostatCoolingModeResponse object
     */
    @PostMapping("/{thermostatId}/cooling-mode")
    public ResponseEntity<ThermostatCoolingModeResponse> setThermostatCoolingMode(
            @RequestBody @Valid ThermostatCoolingModeRequest request,
            @PathVariable Integer thermostatId,
            Authentication authentication) {
        ThermostatCoolingModeResponse response = thermostatService.setThermostatCoolingMode(request, thermostatId, authentication);
        return ResponseEntity.ok(response);
    }

    /**
     * Sets a thermostat to heat mode.
     *
     * @param request The heat mode details
     * @param thermostatId The ID of the thermostat to set to heat mode
     * @param authentication The authentication object of the current user
     * @return ResponseEntity with ThermostatHeatModeResponse object
     */
    @PostMapping("/{thermostatId}/heat-mode")
    public ResponseEntity<ThermostatHeatModeResponse> setThermostatHeatMode(
            @RequestBody ThermostatHeatModeRequest request,
            @PathVariable Integer thermostatId,
            Authentication authentication) {
        ThermostatHeatModeResponse response = thermostatService.setThermostatHeatMode(request, thermostatId, authentication);
        return ResponseEntity.ok(response);
    }
}


