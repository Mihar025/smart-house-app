package com.misha.sh.devicemanagementmicroservice.Controllers;

import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockAccessCodeRequest;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockRequest;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockResponse;
import com.misha.sh.devicemanagementmicroservice.request.doorLock.DoorLockStatus;
import com.misha.sh.devicemanagementmicroservice.service.doorLockService.DoorLockService;
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


    @PostMapping
    public ResponseEntity<DoorLockResponse> addDoorLock(@RequestBody DoorLockRequest doorLockRequest, Authentication authentication) {
        DoorLockResponse response = doorLockService.addDoorLock(doorLockRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{doorLockId}/access-code")
    public ResponseEntity<Void> changeAccessCode(Authentication authentication, @RequestBody Integer accessCode, @PathVariable Integer doorLockId) {
        doorLockService.changeAccessCode(authentication, accessCode, doorLockId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{doorLockId}/open")
    public ResponseEntity<DoorLockStatus> openDoorLock(Authentication authentication, @PathVariable Integer doorLockId, @RequestBody DoorLockAccessCodeRequest accessCodeRequest) {
        DoorLockStatus status = doorLockService.openDoorLock(authentication, doorLockId, accessCodeRequest);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/{doorLockId}/close")
    public ResponseEntity<DoorLockStatus> closeDoorLock(Authentication authentication, @PathVariable Integer doorLockId, @RequestBody DoorLockAccessCodeRequest accessCodeRequest) {
        DoorLockStatus status = doorLockService.closeDoorLock(authentication, doorLockId, accessCodeRequest);
        return ResponseEntity.ok(status);
    }

}
