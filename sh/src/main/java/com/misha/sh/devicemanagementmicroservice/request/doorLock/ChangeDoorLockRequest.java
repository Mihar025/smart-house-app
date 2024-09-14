package com.misha.sh.devicemanagementmicroservice.request.doorLock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDoorLockRequest {
    private String doorCode;
}
