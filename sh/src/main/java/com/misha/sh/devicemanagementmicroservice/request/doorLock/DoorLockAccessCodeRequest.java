package com.misha.sh.devicemanagementmicroservice.request.doorLock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class DoorLockAccessCodeRequest {

    private Integer accessCode;


}
