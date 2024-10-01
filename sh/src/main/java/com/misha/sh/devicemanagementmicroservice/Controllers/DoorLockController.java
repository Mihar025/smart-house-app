package com.misha.sh.devicemanagementmicroservice.Controllers;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.model.doorLock.LockMechanism;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.*;
import com.misha.sh.devicemanagementmicroservice.service.doorLockService.DoorLockService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doorLock")
public class DoorLockController {

    private final DoorLockService doorLockService;

    @GetMapping("/{lockId}")
    public ResponseEntity<DoorLockResponse> getLockById(@PathVariable Integer lockId, Authentication authentication) {
        try {
            DoorLockResponse response = doorLockService.findLockById(lockId, authentication);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<PageResponse<DoorLockResponse>> findAllDoorLock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        PageResponse<DoorLockResponse> response = doorLockService.findAllDoorLocks(authentication, size, page);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DoorLockResponse> addDoorLock(@Valid @RequestBody DoorLockRequest doorLockRequest, Authentication authentication) {
        DoorLockResponse response = doorLockService.addDoorLock(doorLockRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{doorLockId}/access-code")
    public ResponseEntity<Void> changeAccessCode(Authentication authentication, @Valid @RequestBody ChangeDoorLockRequest doorLockRequest, @PathVariable Integer doorLockId) {
        doorLockService.changeAccessCode(authentication, doorLockRequest, doorLockId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{doorLockId}/open")
    public ResponseEntity<DoorLockStatus> openDoorLock(Authentication authentication, @PathVariable Integer doorLockId, @RequestBody DoorLockAccessCodeRequest accessCodeRequest) {
        Integer accessCode = accessCodeRequest.getAccessCode();
        String fingerPrintCode = accessCodeRequest.getFingerPrintCode();
        DoorLockStatus status = doorLockService.openDoorLock(authentication, doorLockId, accessCode, fingerPrintCode);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{doorLockId}/close")
    public ResponseEntity<DoorLockStatus> closeDoorLock(Authentication authentication, @PathVariable Integer doorLockId, @RequestBody DoorLockAccessCodeRequest accessCodeRequest) {
        Integer accessCode = accessCodeRequest.getAccessCode();
        DoorLockStatus status = doorLockService.closeDoorLock(authentication, doorLockId, accessCode);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/{doorLockId}")
    public ResponseEntity<Void> deleteDoorLock(@PathVariable Integer doorLockId, Authentication authentication) {
        try {
            doorLockService.deleteDoorLockById(doorLockId, authentication);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{doorLockId}/opening-type")
    public ResponseEntity<Void> setOpeningType(
            @PathVariable Integer doorLockId,
            @RequestParam LockMechanism openingType,
            @RequestParam(required = false) Integer accessCode,
            Authentication authentication) {
        doorLockService.setOpeningType(doorLockId, openingType, accessCode, authentication);
        return ResponseEntity.ok().build();
    }
}