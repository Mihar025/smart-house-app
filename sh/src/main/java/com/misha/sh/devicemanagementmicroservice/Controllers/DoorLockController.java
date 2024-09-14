package com.misha.sh.devicemanagementmicroservice.Controllers;

import com.misha.sh.devicemanagementmicroservice.exception.BusinessException;
import com.misha.sh.devicemanagementmicroservice.model.User;
import com.misha.sh.devicemanagementmicroservice.pagination.PageResponse;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.*;
import com.misha.sh.devicemanagementmicroservice.request.smartOutlet.SmartOutletResponse;
import com.misha.sh.devicemanagementmicroservice.service.doorLockService.DoorLockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<DoorLockResponse> addDoorLock(@RequestBody DoorLockRequest doorLockRequest, Authentication authentication) {
        DoorLockResponse response = doorLockService.addDoorLock(doorLockRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{doorLockId}/access-code")
    public ResponseEntity<Void> changeAccessCode(Authentication authentication, @RequestBody ChangeDoorLockRequest doorLockRequest, @PathVariable Integer doorLockId) {
        Integer pinCode = Integer.parseInt(doorLockRequest.getDoorCode());
        doorLockService.changeAccessCode(authentication, pinCode , doorLockId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{doorLockId}/open")
    public ResponseEntity<DoorLockStatus> openDoorLock(Authentication authentication, @PathVariable Integer doorLockId, @RequestBody DoorLockAccessCodeRequest accessCodeRequest) {
        Integer accessCode = Integer.parseInt(accessCodeRequest.getAccessCode());
        DoorLockStatus status = doorLockService.openDoorLock(authentication, doorLockId, accessCode);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{doorLockId}/close")
    public ResponseEntity<DoorLockStatus> closeDoorLock(Authentication authentication, @PathVariable Integer doorLockId, @RequestBody DoorLockAccessCodeRequest accessCodeRequest) {
        Integer accessCode = Integer.parseInt(accessCodeRequest.getAccessCode());
        DoorLockStatus status = doorLockService.closeDoorLock(authentication, doorLockId, accessCode);
        return ResponseEntity.ok(status);
    }

}
