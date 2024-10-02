package com.misha.sh.devicemanagementmicroservice.Controllers;


import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.ScheduleRequestOn;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletRequest;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming.SmartOutletEnergyConsumingRequest;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.energyConsuming.SmartOutletEnergyConsumingResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.lastActivity.SmartOutletLastActivityResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.scheduling.SmartOutletScheduleResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests.ScheduleRequestOff;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests.SmartOutletTurnOffResponse;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.turnOnRequests.SmartOutletTurnOnResponse;
import com.misha.sh.devicemanagementmicroservice.service.smartOutletService.SmartOutletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/outlet")
public class SmartOutletController {
    private final SmartOutletService smartOutletService;


    @PostMapping
    public ResponseEntity<SmartOutletResponse> addSmartOutlet(@RequestBody SmartOutletRequest request, Authentication authentication) {
        SmartOutletResponse response = smartOutletService.addSmartOutlet(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{outletId}")
    public ResponseEntity<SmartOutletResponse> findOutletById(
            @PathVariable Integer outletId,

            Authentication authentication) {
        SmartOutletResponse response = smartOutletService.findOutletById(outletId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<SmartOutletResponse>> findAllOutlets(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<SmartOutletResponse> response = smartOutletService.findAllOutlets(authentication, size, page);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{outletId}/turn-on")
    public ResponseEntity<SmartOutletTurnOnResponse> turnOnSmartOutlet(
            @PathVariable Integer outletId,
            Authentication authentication) {
        SmartOutletTurnOnResponse response = smartOutletService.turnOnSmartOutlet(outletId, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{outletId}/turn-off")
    public ResponseEntity<SmartOutletTurnOffResponse> turnOffSmartOutlet(
            @PathVariable Integer outletId,
            Authentication authentication) {
        SmartOutletTurnOffResponse response = smartOutletService.turnOffSmartOutlet(outletId, authentication);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/{outletId}/schedule-on")
    public ResponseEntity<SmartOutletScheduleResponse> scheduleTurnOn(
            @PathVariable Integer outletId,
            @RequestBody ScheduleRequestOn request,
            Authentication authentication) {
        LocalDateTime scheduledTime = LocalDateTime.parse(request.getScheduledTimeOn());
        SmartOutletScheduleResponse response = smartOutletService.scheduleTurnOn(outletId, scheduledTime, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{outletId}/schedule-off")
    public ResponseEntity<SmartOutletScheduleResponse> scheduleTurnOff(
            @PathVariable Integer outletId,
            @RequestBody ScheduleRequestOff requestOff,
            Authentication authentication) {
        LocalDateTime scheduledTime = LocalDateTime.parse(requestOff.getScheduledTimeOff());
        SmartOutletScheduleResponse response = smartOutletService.scheduleTurnOff(outletId, scheduledTime, authentication);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{outletId}/power-usage")
    public ResponseEntity<SmartOutletEnergyConsumingResponse> getCurrentPowerUsage(
            @PathVariable Integer outletId,
            Authentication authentication) {
        SmartOutletEnergyConsumingResponse response = smartOutletService.currentPowerUsing(outletId, authentication);
        return ResponseEntity.ok(response);
    }




    @PostMapping("/{outletId}/power-usage")
    public ResponseEntity<SmartOutletEnergyConsumingResponse> setCustomPowerUsage(
            @PathVariable Integer outletId,
            @RequestBody SmartOutletEnergyConsumingRequest request,
            Authentication authentication) {
        SmartOutletEnergyConsumingResponse response = smartOutletService.setCustomPowerUsing(outletId, request, authentication);
        return ResponseEntity.ok(response);
    }




    @PostMapping("/{outletId}/reset-power-usage")
    public ResponseEntity<SmartOutletEnergyConsumingResponse> resetPowerUsage(
            @PathVariable Integer outletId,
            Authentication authentication) {
        SmartOutletEnergyConsumingResponse response = smartOutletService.setDefaultPowerUsing(outletId, authentication);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/{outletId}/last-activity")
    public ResponseEntity<SmartOutletLastActivityResponse> getLastActivity(
            @PathVariable Integer outletId,
            Authentication authentication) {
        SmartOutletLastActivityResponse response = smartOutletService.getLastActivity(outletId, authentication);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{outletId}")
    public ResponseEntity<Void> deleteSmartOutlet(@PathVariable Integer outletId, Authentication authentication) {
        try {
            smartOutletService.deleteSmartOutletById(outletId, authentication);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
