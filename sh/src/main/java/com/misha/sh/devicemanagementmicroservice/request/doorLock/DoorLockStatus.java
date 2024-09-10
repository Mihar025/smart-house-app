package com.misha.sh.devicemanagementmicroservice.request.doorLock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class DoorLockStatus {
    private Integer doorLockId;
    private boolean isOpened;
    private LocalDateTime lastOpenedTime;
}
